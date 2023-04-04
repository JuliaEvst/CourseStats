package ru.yarsu

import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.BiDiBodyLens
import org.http4k.routing.*
import org.http4k.server.Netty
import org.http4k.server.asServer
import org.http4k.template.PebbleTemplates
import org.http4k.template.ViewModel
import org.http4k.template.viewModel
import ru.yarsu.domain.Course
import ru.yarsu.domain.ScheduleInfo
import ru.yarsu.domain.formSchedule
import ru.yarsu.models.*
import ru.yarsu.web.filtres.ShowErrorMessageFilter
import ru.yarsu.web.templates.ContextAwarePebbleTemplates
import ru.yarsu.web.templates.ContextAwareViewRender

private fun showPebbleTemplate(bodyLens: BiDiBodyLens<ViewModel>,): HttpHandler = {
    val viewModel = PebbleViewModel("Hello there!")
    Response(OK).with(bodyLens of viewModel)
}

private fun showStartPage(bodyLens: BiDiBodyLens<ViewModel>): HttpHandler = {
    val startPage = StartPageVM()
    Response(OK).with(bodyLens of startPage)
}

private fun showCourses(bodyLens: BiDiBodyLens<ViewModel>, scheduleInfo: ScheduleInfo): HttpHandler = {
    val courses = ShowCoursesVM(scheduleInfo.courses)
    Response(OK).with(bodyLens of courses)
}

private fun showCourseStats(bodyLens: BiDiBodyLens<ViewModel>, scheduleInfo: ScheduleInfo): HttpHandler = {
    reqest-> val id = reqest.path("id")
    val courses = ArrayList<Course>()
    for(course in scheduleInfo.courses)
    {
        if (course.id == id)
            courses.add(course)
    }
    val infoCourse = ShowCourseStatsVM(courses.distinctBy { it.id }, scheduleInfo.scheduleItems)
    Response(OK).with(bodyLens of infoCourse)
}

private fun respondWithPong(): HttpHandler = {
    Response(OK).body("pong")
}

fun router(
    scheduleInfo: ScheduleInfo,
    bodyLens: BiDiBodyLens<ViewModel>,
    htmlView: ContextAwareViewRender,
): HttpHandler = routes(
    "/" bind GET to showStartPage(bodyLens),
    "/ping" bind GET to respondWithPong(),
    "/templates/pebble" bind GET to showPebbleTemplate(bodyLens),
    "/courses" bind GET to showCourses(bodyLens, scheduleInfo),
    "/course_stats/{id}" bind GET to showCourseStats(bodyLens, scheduleInfo),
    static(ResourceLoader.Classpath("/ru/yarsu/public/")),
)

fun main() {

    val scheduleInfo = formSchedule()

    val renderer = ContextAwarePebbleTemplates().HotReload("src/main/resources")

    val htmlView = ContextAwareViewRender.withContentType(renderer, ContentType.TEXT_HTML)

    val bodyLens: BiDiBodyLens<ViewModel> =  Body.viewModel(
        PebbleTemplates().HotReload("src/main/resources"),
        ContentType.TEXT_HTML,
    ).toLens()

    val router = router(scheduleInfo, bodyLens, htmlView)

    val app: HttpHandler = ShowErrorMessageFilter(htmlView)
        .then(router)

    val server = app.asServer(Netty(9000)).start()
    println("Server started on http://localhost:" + server.port())

/*    val scheduleInfo = formSchedule()
    val renderer = Body.viewModel(
        PebbleTemplates().HotReload("src/main/resources"),
        ContentType.TEXT_HTML,
    ).toLens()
    val router = router(scheduleInfo, renderer)

    val app: HttpHandler = Filter.NoOp.then(router)
    val server = app.asServer(Netty(9000)).start()
    println("Server started on http://localhost:" + server.port())*/
}
