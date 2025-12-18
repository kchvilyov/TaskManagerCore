package ru.kchvilyov.taskmanager.core.presentation

import ru.kchvilyov.taskmanager.core.domain.Task

data class TaskState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false
)