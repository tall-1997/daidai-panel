package com.daidai.app.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daidai.app.data.remote.model.*
import com.daidai.app.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskListUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = true,
    val searchQuery: String = "",
    val selectedTask: Task? = null,
    val taskLogs: Map<Int, List<String>> = emptyMap()
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks(refresh: Boolean = false) {
        if (refresh) {
            _uiState.value = _uiState.value.copy(currentPage = 1, isRefreshing = true)
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            taskRepository.getTasks(
                page = _uiState.value.currentPage,
                search = _uiState.value.searchQuery.ifBlank { null }
            )
                .onSuccess { taskListResponse ->
                    val newTasks = if (refresh) {
                        taskListResponse.data ?: emptyList()
                    } else {
                        _uiState.value.tasks + (taskListResponse.data ?: emptyList())
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        tasks = newTasks,
                        hasMore = newTasks.size < taskListResponse.total
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = exception.message
                    )
                }
        }
    }

    fun loadMore() {
        if (_uiState.value.isLoading || !_uiState.value.hasMore) return

        _uiState.value = _uiState.value.copy(currentPage = _uiState.value.currentPage + 1)
        loadTasks()
    }

    fun searchTasks(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadTasks(refresh = true)
    }

    fun runTask(taskId: Int) {
        viewModelScope.launch {
            taskRepository.runTask(taskId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(successMessage = "任务已执行")
                    loadTasks(refresh = true)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }
        }
    }

    fun stopTask(taskId: Int) {
        viewModelScope.launch {
            taskRepository.stopTask(taskId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(successMessage = "任务已停止")
                    loadTasks(refresh = true)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }
        }
    }

    fun enableTask(taskId: Int) {
        viewModelScope.launch {
            taskRepository.enableTask(taskId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(successMessage = "任务已启用")
                    loadTasks(refresh = true)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }
        }
    }

    fun disableTask(taskId: Int) {
        viewModelScope.launch {
            taskRepository.disableTask(taskId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(successMessage = "任务已禁用")
                    loadTasks(refresh = true)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(successMessage = "任务已删除")
                    loadTasks(refresh = true)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }
        }
    }

    fun pinTask(taskId: Int) {
        viewModelScope.launch {
            taskRepository.pinTask(taskId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(successMessage = "任务已置顶")
                    loadTasks(refresh = true)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }
        }
    }

    fun unpinTask(taskId: Int) {
        viewModelScope.launch {
            taskRepository.unpinTask(taskId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(successMessage = "已取消置顶")
                    loadTasks(refresh = true)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }
        }
    }

    fun copyTask(taskId: Int) {
        viewModelScope.launch {
            taskRepository.copyTask(taskId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(successMessage = "任务已复制")
                    loadTasks(refresh = true)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }
        }
    }

    fun createTask(name: String, command: String, schedule: String, taskType: String = "cron") {
        viewModelScope.launch {
            taskRepository.createTask(CreateTaskRequest(name, command, schedule, taskType))
                .onSuccess {
                    _uiState.value = _uiState.value.copy(successMessage = "任务创建成功")
                    loadTasks(refresh = true)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }
        }
    }

    fun updateTask(taskId: Int, request: UpdateTaskRequest) {
        viewModelScope.launch {
            taskRepository.updateTask(taskId, request)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(successMessage = "任务更新成功")
                    loadTasks(refresh = true)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }
        }
    }

    fun getTaskLogs(taskId: Int) {
        viewModelScope.launch {
            taskRepository.getTaskLogs(taskId)
                .onSuccess { logs ->
                    _uiState.value = _uiState.value.copy(
                        taskLogs = _uiState.value.taskLogs + (taskId to logs)
                    )
                }
                .onFailure { }
        }
    }

    fun selectTask(task: Task?) {
        _uiState.value = _uiState.value.copy(selectedTask = task)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}
