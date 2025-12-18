package ru.kchvilyov.taskmanager.core.presentation

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.kchvilyov.taskmanager.core.domain.TaskRepository
import ru.kchvilyov.taskmanager.core.domain.Task

class TaskViewModel(
    private val repository: TaskRepository,
    private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow(TaskState(isLoading = true))
    val state: StateFlow<TaskState> = _state.asStateFlow()

    init {
        // Автоматическая загрузка задач при создании ViewModel
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
        _state.update { it.copy(isLoading = true) }
        val tasks = repository.getAllTasks()
        _state.update { it.copy(tasks = tasks, isLoading = false) }
    }

    private suspend fun addTask(task: Task) {
        repository.insertTask(task)
        loadTasks() // Перезагружаем для обновления списка (можно оптимизировать при необходимости)
    }

    private suspend fun toggleTask(id: Int) {
        val task = repository.getTaskById(id) ?: return
        val updatedTask = task.copy(isCompleted = !task.isCompleted)
        repository.updateTask(updatedTask)
        _state.update { state ->
            val updatedTasks = state.tasks.map { if (it.id == id) updatedTask else it }
            state.copy(tasks = updatedTasks)
        }
    }
}