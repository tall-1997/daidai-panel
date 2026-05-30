package com.daidai.app.ui.screen.task

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daidai.app.data.remote.model.Task
import com.daidai.app.data.remote.model.TaskStatsDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Int,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("任务详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(taskId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "编辑")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.task != null) {
                TaskDetailContent(
                    task = uiState.task!!,
                    stats = uiState.stats,
                    latestLog = uiState.latestLog,
                    onRun = { viewModel.runTask(taskId) },
                    onStop = { viewModel.stopTask(taskId) },
                    onEnable = { viewModel.enableTask(taskId) },
                    onDisable = { viewModel.disableTask(taskId) },
                    onPin = { viewModel.pinTask(taskId) },
                    onUnpin = { viewModel.unpinTask(taskId) },
                    onCopy = { viewModel.copyTask(taskId) },
                    onDelete = {
                        viewModel.deleteTask(taskId)
                        onBack()
                    }
                )
            } else {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.error ?: "加载失败",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadTask(taskId) }) {
                        Text("重试")
                    }
                }
            }
        }
    }
}

@Composable
fun TaskDetailContent(
    task: Task,
    stats: TaskStatsDetail?,
    latestLog: String?,
    onRun: () -> Unit,
    onStop: () -> Unit,
    onEnable: () -> Unit,
    onDisable: () -> Unit,
    onPin: () -> Unit,
    onUnpin: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 基本信息卡片
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    StatusChip(status = task.status)
                }

                Spacer(modifier = Modifier.height(16.dp))

                DetailItem("任务ID", "${task.id}")
                DetailItem("任务类型", task.taskType ?: "cron")
                DetailItem("调度表达式", task.schedule.ifBlank { "未设置" })
                DetailItem("状态", task.statusText)
                if (task.isPinned) {
                    DetailItem("置顶", "是")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 命令卡片
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "执行命令",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = task.command,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 执行信息卡片
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "执行信息",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                DetailItem("上次执行时间", task.lastRunAt ?: "未执行")
                DetailItem("上次执行状态", when (task.lastRunStatus) {
                    0 -> "成功"
                    1 -> "失败"
                    else -> "未执行"
                })
                DetailItem("下次执行时间", task.nextRunAt ?: "未安排")
                DetailItem("创建时间", task.createdAt)
                DetailItem("更新时间", task.updatedAt)
            }
        }

        // 统计信息卡片
        if (stats != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "统计信息",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailItem("总执行次数", "${stats.totalRuns ?: 0}")
                    DetailItem("成功次数", "${stats.successRuns ?: 0}")
                    DetailItem("失败次数", "${stats.failedRuns ?: 0}")
                    if (stats.avgDuration != null) {
                        DetailItem("平均耗时", String.format("%.2f秒", stats.avgDuration))
                    }
                }
            }
        }

        // 最新日志卡片
        if (latestLog != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "最新日志",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = latestLog,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 高级设置卡片
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "高级设置",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                DetailItem("超时时间", "${task.timeout ?: 86400}秒")
                DetailItem("最大重试次数", "${task.maxRetries ?: 0}")
                DetailItem("重试间隔", "${task.retryInterval ?: 60}秒")
                DetailItem("允许多实例", if (task.allowMultipleInstances == true) "是" else "否")
                DetailItem("失败通知", if (task.notifyOnFailure == true) "是" else "否")
                DetailItem("成功通知", if (task.notifyOnSuccess == true) "是" else "否")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 操作按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (task.isRunning) {
                Button(
                    onClick = onStop,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("停止")
                }
            } else {
                Button(
                    onClick = onRun,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("执行")
                }
            }

            OutlinedButton(
                onClick = if (task.isEnabled) onDisable else onEnable,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    if (task.isEnabled) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (task.isEnabled) "禁用" else "启用")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = if (task.isPinned) onUnpin else onPin,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    if (task.isPinned) Icons.Default.PushPin else Icons.Default.PushPin,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (task.isPinned) "取消置顶" else "置顶")
            }

            OutlinedButton(
                onClick = onCopy,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("复制")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { showDeleteDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(Icons.Default.Delete, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("删除任务")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除任务「${task.name}」吗？此操作不可恢复。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun StatusChip(status: Double) {
    val (text, color) = when (status) {
        Task.STATUS_DISABLED -> "已禁用" to MaterialTheme.colorScheme.onSurfaceVariant
        Task.STATUS_QUEUED -> "排队中" to MaterialTheme.colorScheme.secondary
        Task.STATUS_ENABLED -> "已启用" to MaterialTheme.colorScheme.tertiary
        Task.STATUS_RUNNING -> "运行中" to MaterialTheme.colorScheme.primary
        else -> "未知" to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
