package ru.kchvilyov.taskmanager.core.data

import ru.kchvilyov.taskmanager.core.domain.TaskRepository
import ru.kchvilyov.taskmanager.core.domain.Task

class TaskRepositoryImpl(private val dataSource: TaskRepository) : TaskRepository {
    override suspend fun getAllTasks(): List<Task> = dataSource.getAllTasks()
    override suspend fun getTaskById(id: Int): Task? = dataSource.getTaskById(id)
    override suspend fun insertTask(task: Task) = dataSource.insertTask(task)
    override suspend fun updateTask(task: Task) = dataSource.updateTask(task)
    override suspend fun deleteTask(id: Int) = dataSource.deleteTask(id)
}