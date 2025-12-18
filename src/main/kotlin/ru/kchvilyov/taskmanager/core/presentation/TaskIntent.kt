package ru.kchvilyov.taskmanager.core.presentation

import ru.kchvilyov.taskmanager.core.domain.Task

sealed class TaskIntent {
    object LoadTasks : TaskIntent()
    data class AddTask(val task: Task) : TaskIntent()
    data class ToggleTask(val id: Int) : TaskIntent()
}