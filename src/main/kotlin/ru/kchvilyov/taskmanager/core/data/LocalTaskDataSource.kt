package ru.kchvilyov.taskmanager.core.data

import kotlinx.coroutines.delay
import ru.kchvilyov.taskmanager.core.domain.Task
import ru.kchvilyov.taskmanager.core.domain.TaskRepository

class LocalTaskDataSource : TaskRepository {
    private val tasks = mutableListOf<Task>()

    init {
        // Добавим тестовые данные
        tasks.addAll(
            listOf(
                Task(1, "Learn Kotlin", "Study coroutines and flows", false, System.currentTimeMillis() - 1000000),
                Task(2, "Write Code", "Implement clean architecture", false, System.currentTimeMillis() - 900000),
                Task(3, "Review PR", "Check team member's code", true, System.currentTimeMillis() - 800000)
            )
        )
    }

    override suspend fun getAllTasks(): List<Task> {
        delay(100)
        return tasks.toList()
    }

    override suspend fun getTaskById(id: Int): Task? {
        delay(100)
        return tasks.find { it.id == id }
    }

    override suspend fun insertTask(task: Task) {
        delay(100)
        tasks.add(task)
    }

    override suspend fun updateTask(task: Task): Boolean {
        delay(100)
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
            return true
        }
        return false
    }

    override suspend fun deleteTask(id: Int): Boolean {
        delay(100)
        return tasks.removeIf { it.id == id } // removeIf возвращает Boolean
    }
}