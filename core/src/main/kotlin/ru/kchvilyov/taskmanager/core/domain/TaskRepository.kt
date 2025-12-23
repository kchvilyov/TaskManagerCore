package ru.kchvilyov.taskmanager.core.domain

interface TaskRepository {
    suspend fun getAllTasks(): List<Task>
    suspend fun getTaskById(id: Int): Task?
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task): Boolean
    suspend fun deleteTask(id: Int): Boolean
}