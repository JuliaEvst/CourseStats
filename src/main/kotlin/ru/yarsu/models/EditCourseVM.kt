package ru.yarsu.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel
import ru.yarsu.domain.Course

data class EditCourseVM(
    val course: Course,
    val form: WebForm = WebForm()
) : ViewModel