package ru.yarsu.models

import org.http4k.core.Uri
import org.http4k.template.ViewModel

data class NotFoundVM(val uri: Uri) : ViewModel