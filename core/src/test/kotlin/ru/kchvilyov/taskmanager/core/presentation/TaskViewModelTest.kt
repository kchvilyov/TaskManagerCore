package ru.kchvilyov.taskmanager.core.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import ru.kchvilyov.taskmanager.core.di.AppContainer
import ru.kchvilyov.taskmanager.core.domain.Task
import ru.kchvilyov.taskmanager.core.presentation.TaskIntent.AddTask
import ru.kchvilyov.taskmanager.core.presentation.TaskIntent.ToggleTask
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var container: AppContainer
    private lateinit var viewModel: TaskViewModel
    private lateinit var states: MutableList<TaskState>

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        container = AppContainer()
        viewModel = container.taskViewModel
        states = mutableListOf()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    fun `initial state loads tasks correctly`() = runTest {
        // Подписываемся на изменения состояния
        val job = launch {
            viewModel.state.toList(states)
        }

        // Даем время на инициализацию
        testDispatcher.scheduler.advanceUntilIdle()

        // Проверяем, что задачи загрузились
        assertTrue(states.size > 1) { "State should emit at least once" }
        val finalState = states.last()
        // Проверяем по последнему состоянию, что загрузка завершена
        assertFalse(finalState.isLoading)
        // Проверяем, что в списке 3 задачи
        assertEquals(3, finalState.tasks.size)
        job.cancel()
    }

    fun `add task increases task count`() = runTest {
        val job = launch {
            viewModel.state.toList(states)
        }
        //Выполняет поставленные в очередь задачи в указанном порядке,
        // продвигая виртуальное время по мере необходимости, пока не останется задач,
        // связанных с диспетчерами, подключенными к этому планировщику.
        testDispatcher.scheduler.advanceUntilIdle()
        // Начальное количество задач
        val initialCount = states.last().tasks.size
        // Производим действие добавления задачи
        val newTask = Task(4, "New Task", "Description", false, System.currentTimeMillis())
        viewModel.processIntent(AddTask(newTask))
        //Выполняет поставленные в очередь задачи в указанном порядке
        testDispatcher.scheduler.advanceUntilIdle()
        // Проверяем, что количество задач увеличилось на 1
        val finalCount = states.last().tasks.size
        assertEquals(initialCount + 1, finalCount)
        // Проверяем, что добавленная задача появилась в списке
        assertTrue(states.last().tasks.any { it.id == 4 })

        job.cancel()
    }

    fun `toggle task changes completion status`() = runTest {
        val job = launch {
            viewModel.state.toList(states)
        }

        testDispatcher.scheduler.advanceUntilIdle()

        val initialTask = states.last().tasks.find { it.id == 1 }
        // Проверяем, что задача с id 1 существует
        assertNotNull(initialTask)
        val initialStatus = initialTask.isCompleted

        viewModel.processIntent(ToggleTask(1))

        testDispatcher.scheduler.advanceUntilIdle()

        val updatedTask = states.last().tasks.find { it.id == 1 }
        assertNotNull(updatedTask)
        // Проверяем, что статус задачи изменился
        assertEquals(!initialStatus, updatedTask.isCompleted)

        job.cancel()
    }
}