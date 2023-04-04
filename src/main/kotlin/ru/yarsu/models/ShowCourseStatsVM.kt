package ru.yarsu.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.Course
import ru.yarsu.domain.ScheduleItem

data class ShowCourseStatsVM( val courses: List<Course>, val scheduleItems: List<ScheduleItem>): ViewModel