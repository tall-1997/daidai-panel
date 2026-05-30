package com.daidai.app.ui.screen.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daidai.app.data.remote.model.Task
import com.daidai.app.data.remote.model.TaskStatsDetail
import com.daidai.app.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskDetailUiState(
    val isLoading: Boolean = false,
    val task: Task? = null,
    val stats: TaskStatsDetail? = null,
    val latestLog: String? = null,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    fun loadTask(taskId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val result = taskRepository.getTask(taskId)
            result.fold(
                onSuccess = { task ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        task = task
                    )
                    // 加载统计信息和最新日志
                    loadStats(taskId)
                    loadLatestLog(taskId)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "加载失败"
                    )
                }
            )
        }
    }

    private fun loadStats(taskId: Int) {
        viewModelScope.launch {
            val result = taskRepository.getTaskStats(taskId)
            result.fold(
                onSuccess = { stats ->
                    _uiState.value = _uiState.value.copy(stats = stats)
                },
                onFailure = { /* 忽略统计加载失败 */ }
            )
        }
    }

    private fun loadLatestLog(taskId: Int) {
        viewModelScope.launch {
            val result = taskRepository.getTaskLatestLog(taskId)
            result.fold(
                onSuccess = { log ->
                    _uiState.value = _uiState.value.copy(latestLog = log?.content)
                },
                onFailure = { /* 忽略日志加载失败 */ }
            )
        }
    }

    fun runTask(taskId: Int) {
        viewModelScope.launch {
            val result = taskRepository.runTask(taskId)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(successMessage = "任务已开始执行")
                    loadTask(taskId)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(error = e.message ?: "执行任务失败")
                }
            )
        }
    }

    fun stopTask(taskId: Int) {
        viewModelScope.launch {
            val result = taskRepository.stopTask(taskId)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(successMessage = "任务已停止")
                    loadTask(taskId)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(error = e.message ?: "停止任务失败")
                }
            )
        }
    }

    fun enableTask(taskId: Int) {
        viewModelScope.launch {
            val result = taskRepository.enableTask(taskId)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(successMessage = "任务已启用")
                    loadTask(taskId)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(error = e.message ?: "启用任务失败")
                }
            )
        }
    }

    fun disableTask(taskId: Int) {
        viewModelScope.launch {
            val result = taskRepository.disableTask(taskId)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(successMessage = "任务已禁用")
                    loadTask(taskId)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(error = e.message ?: "禁用任务失败")
                }
            )
        }
    }

    fun pinTask(taskId: Int) {
        viewModelScope.launch {
            val result = taskRepository.pinTask(taskId)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(successMessage = "任务已置顶")
                    loadTask(taskId)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(error = e.message ?: "置顶任务失败")
                }
            )
        }
    }

    fun unpinTask(taskId: Int) {
        viewModelScope.launch {
            val result = taskRepository.unpinTask(taskId)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(successMessage = "已取消置顶")
                    loadTask(taskId)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(error = e.message ?: "取消置顶失败")
                }
            )
        }
    }

    fun copyTask(taskId: Int) {
        viewModelScope.launch {
            val result = taskRepository.copyTask(taskId)
            result.fold(
                onSuccess = { newTask ->
                    _uiState.value = _uiState.value.copy(
                        task = newTask,
                        successMessage = "任务已复制"
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(error = e.message ?: "复制任务失败")
                }
            )
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            val result = taskRepository.deleteTask(taskId)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(successMessage = "任务已删除")
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(error = e.message ?: "删除任务失败")
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}
