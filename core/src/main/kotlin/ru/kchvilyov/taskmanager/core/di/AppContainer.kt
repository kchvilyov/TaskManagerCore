package ru.kchvilyov.taskmanager.core.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import ru.kchvilyov.taskmanager.core.data.LocalTaskDataSource
import ru.kchvilyov.taskmanager.core.data.TaskRepositoryImpl
import ru.kchvilyov.taskmanager.core.domain.TaskRepository
import ru.kchvilyov.taskmanager.core.presentation.TaskViewModel

class AppContainer {
    private val dataSource: TaskRepository = LocalTaskDataSource()
    private val repository: TaskRepository = TaskRepositoryImpl(dataSource)
    private val viewModelScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    val taskViewModel: TaskViewModel by lazy {
        TaskViewModel(repository, viewModelScope)
    }
}