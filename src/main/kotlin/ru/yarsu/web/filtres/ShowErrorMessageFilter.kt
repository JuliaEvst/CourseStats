package ru.yarsu.web.filtres

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.with
import ru.yarsu.models.NotFoundVM
import ru.yarsu.web.templates.ContextAwareViewRender

class ShowErrorMessageFilter(
    private val htmlView: ContextAwareViewRender,
) : Filter {
    override fun invoke(next: HttpHandler): HttpHandler = { request ->
        val response = next(request)
        if (response.status.successful ||
            response.header("content-type") != null && response.body.length != 0L
        ) {
            response
        } else {
            response.with(htmlView(request) of NotFoundVM(request.uri))
        }
    }
}