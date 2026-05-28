package com.daidai.app.data.remote.model

import com.google.gson.annotations.SerializedName

// Base Response
data class BaseResponse(
    val code: Int,
    val message: String,
    val data: Any?
)

// Auth Models
data class LoginRequest(
    val username: String,
    val password: String,
    @SerializedName("totp_code")
    val totpCode: String? = null
)

data class LoginResponse(
    val message: String?,
    @SerializedName("access_token")
    val accessToken: String?,
    @SerializedName("refresh_token")
    val refreshToken: String?,
    val user: User?
)

data class RefreshTokenRequest(
    @SerializedName("refresh_token")
    val refreshToken: String
)

data class User(
    val id: Int,
    val username: String,
    val role: String,
    @SerializedName("avatar_url")
    val avatarUrl: String?
)

data class UserResponse(
    val user: User?
)

// Task Models
data class TaskListResponse(
    val data: List<Task>?,
    val total: Int,
    val page: Int,
    @SerializedName("page_size")
    val pageSize: Int
)

data class Task(
    val id: Int,
    val name: String,
    val command: String,
    @SerializedName("cron_expression")
    val cronExpression: String?,
    @SerializedName("cron_expressions")
    val cronExpressions: List<String>?,
    @SerializedName("task_type")
    val taskType: String?,
    val status: Double,
    @SerializedName("is_pinned")
    val isPinned: Boolean,
    @SerializedName("last_run_at")
    val lastRunAt: String?,
    @SerializedName("last_run_status")
    val lastRunStatus: Int?,
    @SerializedName("last_running_time")
    val lastRunningTime: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("next_run_at")
    val nextRunAt: String?,
    @SerializedName("log_path")
    val logPath: String?,
    @SerializedName("allow_multiple_instances")
    val allowMultipleInstances: Boolean?,
    @SerializedName("depends_on")
    val dependsOn: String?,
    val labels: List<String>?,
    @SerializedName("display_labels")
    val displayLabels: List<String>?,
    @SerializedName("max_retries")
    val maxRetries: Int?,
    @SerializedName("notification_channel_id")
    val notificationChannelId: Int?,
    @SerializedName("notify_on_failure")
    val notifyOnFailure: Boolean?,
    @SerializedName("notify_on_success")
    val notifyOnSuccess: Boolean?,
    val pid: Int?,
    @SerializedName("random_delay_seconds")
    val randomDelaySeconds: Int?,
    @SerializedName("retry_interval")
    val retryInterval: Int?,
    @SerializedName("sort_order")
    val sortOrder: Int?,
    @SerializedName("stop_schedule")
    val stopSchedule: String?,
    @SerializedName("task_after")
    val taskAfter: String?,
    @SerializedName("task_before")
    val taskBefore: String?,
    val timeout: Int?
) {
    companion object {
        const val STATUS_DISABLED = 0.0
        const val STATUS_QUEUED = 0.5
        const val STATUS_ENABLED = 1.0
        const val STATUS_RUNNING = 2.0
    }
    
    val statusText: String
        get() = when (status) {
            STATUS_DISABLED -> "已禁用"
            STATUS_QUEUED -> "排队中"
            STATUS_ENABLED -> "已启用"
            STATUS_RUNNING -> "运行中"
            else -> "未知"
        }
    
    val isRunning: Boolean
        get() = status == STATUS_RUNNING
    
    val isEnabled: Boolean
        get() = status == STATUS_ENABLED || status == STATUS_RUNNING || status == STATUS_QUEUED
    
    val schedule: String
        get() = cronExpression ?: ""
}

data class TaskResponse(
    val message: String?,
    val data: Task?
)

data class CreateTaskRequest(
    val name: String,
    val command: String,
    @SerializedName("cron_expression")
    val cronExpression: String,
    @SerializedName("task_type")
    val taskType: String = "cron"
)

data class UpdateTaskRequest(
    val name: String? = null,
    val command: String? = null,
    @SerializedName("cron_expression")
    val cronExpression: String? = null,
    @SerializedName("is_enabled")
    val isEnabled: Boolean? = null
)

// Environment Variables
data class EnvListResponse(
    val data: List<Env>?,
    val total: Int,
    val page: Int,
    @SerializedName("page_size")
    val pageSize: Int
)

data class Env(
    val id: Int,
    val name: String,
    val value: String,
    val remark: String?,
    val remarks: String?,
    @SerializedName("is_enabled")
    val isEnabled: Boolean,
    val enabled: Boolean?,
    val group: String?,
    val groups: List<String>?,
    val position: Int?,
    @SerializedName("sort_order")
    val sortOrder: Int?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

data class EnvResponse(
    val message: String?,
    val data: Env?
)

data class CreateEnvRequest(
    val name: String,
    val value: String,
    val remark: String? = null,
    @SerializedName("is_enabled")
    val isEnabled: Boolean = true
)

data class UpdateEnvRequest(
    val name: String? = null,
    val value: String? = null,
    val remark: String? = null,
    @SerializedName("is_enabled")
    val isEnabled: Boolean? = null
)

// Scripts
data class ScriptListResponse(
    val data: List<Script>?,
    val total: Int
)

data class Script(
    val name: String,
    val path: String,
    val size: Long,
    @SerializedName("is_dir")
    val isDir: Boolean = false,
    @SerializedName("modified_at")
    val modifiedAt: String? = null,
    @SerializedName("mtime")
    val mtime: Long? = null
)

data class ScriptContentResponse(
    val data: ScriptContent?
)

data class ScriptContent(
    val content: String,
    val path: String?,
    val binary: Boolean?,
    @SerializedName("is_binary")
    val isBinary: Boolean?
)

data class SaveScriptRequest(
    val path: String,
    val content: String
)

// Logs
data class LogListResponse(
    val data: List<TaskLog>?,
    val total: Int,
    val page: Int,
    @SerializedName("page_size")
    val pageSize: Int
)

data class TaskLog(
    val id: Int,
    @SerializedName("task_id")
    val taskId: Int,
    @SerializedName("task_name")
    val taskName: String?,
    @SerializedName("task_type")
    val taskType: String?,
    val status: Int?,
    val content: String?,
    val output: String?,
    @SerializedName("started_at")
    val startedAt: String,
    @SerializedName("ended_at")
    val endedAt: String?,
    @SerializedName("finished_at")
    val finishedAt: String?,
    @SerializedName("duration")
    val duration: Double?,
    @SerializedName("log_path")
    val logPath: String?,
    val labels: List<String>?,
    val task: LogTask?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
)

data class LogTask(
    val labels: List<String>?,
    @SerializedName("task_type")
    val taskType: String?
)

// 日志详情直接返回 TaskLog 对象，不需要包装
data class LogDetailResponse(
    val id: Int,
    @SerializedName("task_id")
    val taskId: Int,
    @SerializedName("task_name")
    val taskName: String?,
    @SerializedName("task_type")
    val taskType: String?,
    val status: Int?,
    val content: String?,
    @SerializedName("started_at")
    val startedAt: String,
    @SerializedName("ended_at")
    val endedAt: String?,
    @SerializedName("duration")
    val duration: Double?,
    @SerializedName("log_path")
    val logPath: String?,
    val labels: List<String>?,
    val task: LogTask?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
)

// System
data class SystemInfoResponse(
    val data: SystemInfo?
)

data class SystemInfo(
    val hostname: String?,
    @SerializedName("machine_code")
    val machineCode: String?,
    @SerializedName("cpu_usage")
    val cpuUsage: Double?,
    @SerializedName("memory_total")
    val memoryTotal: Long?,
    @SerializedName("memory_used")
    val memoryUsed: Long?,
    @SerializedName("memory_free")
    val memoryFree: Long?,
    @SerializedName("memory_usage")
    val memoryUsage: Double?,
    @SerializedName("disk_total")
    val diskTotal: Long?,
    @SerializedName("disk_used")
    val diskUsed: Long?,
    @SerializedName("disk_free")
    val diskFree: Long?,
    @SerializedName("disk_usage")
    val diskUsage: Double?,
    val uptime: String?,
    val goroutines: Int?,
    @SerializedName("go_version")
    val goVersion: String?,
    val os: String?,
    val arch: String?,
    @SerializedName("num_cpu")
    val numCpu: Int?,
    @SerializedName("data_dir")
    val dataDir: String?,
    @SerializedName("net_rx_bytes")
    val netRxBytes: Long?,
    @SerializedName("net_tx_bytes")
    val netTxBytes: Long?,
    @SerializedName("net_rx_speed")
    val netRxSpeed: Long?,
    @SerializedName("net_tx_speed")
    val netTxSpeed: Long?
)

data class HealthResponse(
    val data: HealthData?
)

data class HealthData(
    val status: String
)

// Dependencies
data class DependencyListResponse(
    val data: List<Dependency>?,
    val total: Int
)

data class Dependency(
    val id: Int,
    val type: String,
    val name: String,
    val status: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
) {
    companion object {
        const val TYPE_NODEJS = "nodejs"
        const val TYPE_PYTHON = "python"
        const val TYPE_LINUX = "linux"
        
        const val STATUS_QUEUED = "queued"
        const val STATUS_INSTALLING = "installing"
        const val STATUS_INSTALLED = "installed"
        const val STATUS_FAILED = "failed"
        const val STATUS_REMOVING = "removing"
        const val STATUS_CANCELLED = "cancelled"
    }
    
    val statusText: String
        get() = when (status) {
            STATUS_QUEUED -> "排队中"
            STATUS_INSTALLING -> "安装中"
            STATUS_INSTALLED -> "已安装"
            STATUS_FAILED -> "安装失败"
            STATUS_REMOVING -> "卸载中"
            STATUS_CANCELLED -> "已取消"
            else -> "未知"
        }
    
    val typeText: String
        get() = when (type) {
            TYPE_NODEJS -> "Node.js"
            TYPE_PYTHON -> "Python"
            TYPE_LINUX -> "Linux"
            else -> type
        }
}

data class DependencyResponse(
    val message: String?,
    val data: Dependency?
)

data class CreateDepRequest(
    val type: String,
    val names: List<String>
)

// System Health
data class HealthCheckResponse(
    val items: List<HealthCheckItem>?,
    @SerializedName("last_checked_at")
    val lastCheckedAt: String?
)

data class HealthCheckItem(
    val name: String,
    val status: String,
    val message: String?
)

// Dashboard
data class DashboardResponse(
    val data: DashboardData?
)

data class DashboardData(
    @SerializedName("task_count")
    val taskCount: Int,
    @SerializedName("enabled_tasks")
    val enabledTasks: Int,
    @SerializedName("running_tasks")
    val runningTasks: Int,
    @SerializedName("today_logs")
    val todayLogs: Int,
    @SerializedName("success_logs")
    val successLogs: Int,
    @SerializedName("failed_logs")
    val failedLogs: Int,
    @SerializedName("env_count")
    val envCount: Int,
    @SerializedName("recent_logs")
    val recentLogs: List<TaskLog>?,
    @SerializedName("daily_stats")
    val dailyStats: List<DailyStat>?,
    @SerializedName("prev_task_count")
    val prevTaskCount: Int?,
    @SerializedName("range_days")
    val rangeDays: Int?,
    @SerializedName("sub_count")
    val subCount: Int?,
    @SerializedName("yesterday_logs")
    val yesterdayLogs: Int?,
    @SerializedName("yesterday_success")
    val yesterdaySuccess: Int?
)

data class DailyStat(
    val date: String,
    val success: Int,
    val failed: Int
)

// Stats
data class StatsResponse(
    val data: StatsData?
)

data class StatsData(
    val tasks: TaskStats?,
    val logs: LogStats?,
    val scripts: ScriptStats?
)

data class TaskStats(
    val total: Int,
    val enabled: Int,
    val disabled: Int,
    val running: Int
)

data class LogStats(
    val total: Int,
    val success: Int,
    val failed: Int,
    @SerializedName("success_rate")
    val successRate: Double
)

data class ScriptStats(
    val total: Int
)

// Panel Log
data class PanelLogResponse(
    val data: PanelLogData?
)

data class PanelLogData(
    val logs: List<String>?,
    val total: Int,
    val level: String?
)
