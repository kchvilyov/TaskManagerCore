package ru.kchvilyov.taskmanager.core.presentation

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.kchvilyov.taskmanager.core.domain.Task
import ru.kchvilyov.taskmanager.core.domain.TaskRepository

class TaskViewModel(
    private val repository: TaskRepository,
    private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow(TaskState(isLoading = false))
    val state: StateFlow<TaskState> = _state.asStateFlow()

    init {
        scope.launch {
            processIntent(TaskIntent.LoadTasks)
        }
    }

    fun processIntent(intent: TaskIntent) {
        scope.launch {
            when (intent) {
                TaskIntent.LoadTasks -> loadTasks()
                is TaskIntent.AddTask -> addTask(intent.task)
                is TaskIntent.ToggleTask -> toggleTask(intent.id)
            }
        }
    }

    private suspend fun loadTasks() {
        _state.update { it.copy(isLoading = true, error = null) }
        try {
            val tasks = repository.getAllTasks()
            _state.update { it.copy(tasks = tasks, isLoading = false) }
        } catch (e: Exception) {
            _state.update { it.copy(error = "Failed to load tasks: ${e.message}", isLoading = false) }
        }
    }

    private suspend fun addTask(task: Task) {
        _state.update { it.copy(isLoading = true, error = null) }
        try {
            repository.insertTask(task)
            loadTasks() // Перезагружаем список
        } catch (e: Exception) {
            _state.update { it.copy(error = "Failed to add task: ${e.message}", isLoading = false) }
        }
    }

    private suspend fun toggleTask(id: Int) {
        _state.update { it.copy(isLoading = true, error = null) }
        try {
            val task = repository.getTaskById(id) ?: return
            val updatedTask = task.copy(isCompleted = !task.isCompleted)
            repository.updateTask(updatedTask)

            _state.update { state ->
                val updatedTasks = state.tasks.map { if (it.id == id) updatedTask else it }
                state.copy(tasks = updatedTasks, isLoading = false)
            }
        } catch (e: Exception) {
            _state.update { it.copy(error = "Failed to update task: ${e.message}", isLoading = false) }
        }
    }
}