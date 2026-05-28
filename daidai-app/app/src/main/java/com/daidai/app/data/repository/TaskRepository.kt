package com.daidai.app.data.repository

import com.daidai.app.data.remote.ApiService
import com.daidai.app.data.remote.model.*
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getTasks(
        page: Int = 1,
        pageSize: Int = 20,
        search: String? = null,
        status: String? = null
    ): Result<TaskListResponse> {
        return try {
            val response = apiService.getTasks(page, pageSize, search, status)
            if (response.isSuccessful && response.body()?.data != null) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("获取任务列表失败"))
            } else {
                Result.failure(Exception("获取任务列表失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTask(id: Int): Result<Task> {
        return try {
            val response = apiService.getTask(id)
            if (response.isSuccessful && response.body()?.data != null) {
                response.body()?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("获取任务详情失败"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "获取任务详情失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createTask(request: CreateTaskRequest): Result<Task> {
        return try {
            val response = apiService.createTask(request)
            if (response.isSuccessful && response.body()?.data != null) {
                response.body()?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("创建任务失败"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "创建任务失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTask(id: Int, request: UpdateTaskRequest): Result<Task> {
        return try {
            val response = apiService.updateTask(id, request)
            if (response.isSuccessful && response.body()?.data != null) {
                response.body()?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("更新任务失败"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "更新任务失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(id: Int): Result<Unit> {
        return try {
            val response = apiService.deleteTask(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "删除任务失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun runTask(id: Int): Result<Unit> {
        return try {
            val response = apiService.runTask(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "执行任务失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun stopTask(id: Int): Result<Unit> {
        return try {
            val response = apiService.stopTask(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "停止任务失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun enableTask(id: Int): Result<Unit> {
        return try {
            val response = apiService.enableTask(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "启用任务失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun disableTask(id: Int): Result<Unit> {
        return try {
            val response = apiService.disableTask(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "禁用任务失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTaskLogs(taskId: Int): Result<List<String>> {
        return try {
            val response = apiService.getLogs(taskId = taskId, page = 1, pageSize = 10)
            if (response.isSuccessful && response.body()?.data != null) {
                val logs = response.body()?.data?.map { log ->
                    val logContent = log.output ?: log.content ?: "无输出"
                    val decompressedContent = try {
                        decompressLog(logContent)
                    } catch (e: Exception) {
                        logContent
                    }
                    "[${log.startedAt}] $decompressedContent"
                } ?: emptyList()
                Result.success(logs)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.success(emptyList())
        }
    }
    
    private fun decompressLog(compressed: String): String {
        return try {
            val data = android.util.Base64.decode(compressed, android.util.Base64.DEFAULT)
            val inputStream = java.io.ByteArrayInputStream(data)
            val zlibInputStream = java.util.zip.InflaterInputStream(inputStream)
            val result = zlibInputStream.bufferedReader().use { it.readText() }
            result
        } catch (e: Exception) {
            compressed
        }
    }
}
