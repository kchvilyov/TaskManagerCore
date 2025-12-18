package ru.kchvilyov.taskmanager.core.domain

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val createdAt: Long
)