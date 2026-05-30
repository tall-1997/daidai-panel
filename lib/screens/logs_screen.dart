import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';

class LogsScreen extends StatefulWidget {
  const LogsScreen({super.key});

  @override
  State<LogsScreen> createState() => _LogsScreenState();
}

class _LogsScreenState extends State<LogsScreen> {
  List<Map<String, dynamic>> _logs = [];
  bool _isLoading = true;
  String? _error;
  int _currentPage = 1;
  int _totalLogs = 0;
  final int _pageSize = 50;

  @override
  void initState() {
    super.initState();
    _loadLogs();
  }

  Future<void> _loadLogs({bool loadMore = false}) async {
    if (loadMore) {
      _currentPage++;
    } else {
      _currentPage = 1;
      setState(() {
        _isLoading = true;
        _error = null;
      });
    }

    try {
      final authService = context.read<AuthService>();
      final result = await authService.apiService.getLogs(
        page: _currentPage,
        pageSize: _pageSize,
      );
      
      // API returns {data: [...], page: 1, page_size: 50, total: 9}
      if (result['data'] != null) {
        final newLogs = List<Map<String, dynamic>>.from(result['data'] ?? []);
        setState(() {
          if (loadMore) {
            _logs.addAll(newLogs);
          } else {
            _logs = newLogs;
          }
          _totalLogs = result['total'] ?? 0;
          _isLoading = false;
        });
      } else {
        setState(() {
          _error = result['message'] ?? '获取日志失败';
          _isLoading = false;
        });
      }
    } catch (e) {
      setState(() {
        _error = '网络错误: $e';
        _isLoading = false;
      });
    }
  }

  Future<void> _deleteLog(int id) async {
    try {
      final authService = context.read<AuthService>();
      await authService.apiService.deleteLog(id);
      _loadLogs();
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('日志已删除')),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('删除失败: $e'), backgroundColor: Colors.red),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('日志管理 ($_totalLogs)'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () => _loadLogs(),
          ),
        ],
      ),
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (_isLoading && _logs.isEmpty) {
      return const Center(child: CircularProgressIndicator());
    }

    if (_error != null && _logs.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.error_outline,
              size: 64,
              color: Theme.of(context).colorScheme.error,
            ),
            const SizedBox(height: 16),
            Text(_error!),
            const SizedBox(height: 16),
            FilledButton(
              onPressed: () => _loadLogs(),
              child: const Text('重试'),
            ),
          ],
        ),
      );
    }

    if (_logs.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.article,
              size: 64,
              color: Theme.of(context).colorScheme.onSurfaceVariant,
            ),
            const SizedBox(height: 16),
            const Text('暂无日志'),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: () => _loadLogs(),
      child: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _logs.length + (_logs.length < _totalLogs ? 1 : 0),
        itemBuilder: (context, index) {
          if (index == _logs.length) {
            return Center(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: FilledButton(
                  onPressed: () => _loadLogs(loadMore: true),
                  child: const Text('加载更多'),
                ),
              ),
            );
          }
          
          final log = _logs[index];
          return _LogCard(
            log: log,
            onDelete: () => _deleteLog(log['id']),
            onTap: () => _showLogDetail(log),
          );
        },
      ),
    );
  }

  void _showLogDetail(Map<String, dynamic> log) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (context) => DraggableScrollableSheet(
        initialChildSize: 0.7,
        minChildSize: 0.5,
        maxChildSize: 0.95,
        expand: false,
        builder: (context, scrollController) => _LogDetailSheet(
          log: log,
          scrollController: scrollController,
        ),
      ),
    );
  }
}

class _LogCard extends StatelessWidget {
  final Map<String, dynamic> log;
  final VoidCallback onDelete;
  final VoidCallback onTap;

  const _LogCard({
    required this.log,
    required this.onDelete,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final taskName = log['task_name'] ?? '未知任务';
    final content = log['content'] ?? '';
    final status = log['status'] ?? 0;
    final createdAt = log['created_at'] ?? '';
    final duration = log['duration'] ?? 0;

    Color statusColor;
    String statusText;
    switch (status) {
      case 0:
        statusColor = Colors.green;
        statusText = '成功';
        break;
      case 1:
        statusColor = Colors.red;
        statusText = '失败';
        break;
      case 2:
        statusColor = Colors.orange;
        statusText = '运行中';
        break;
      default:
        statusColor = Colors.grey;
        statusText = '未知';
    }

    // Extract first line of content for preview
    final preview = content.split('\n').first;

    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Expanded(
                    child: Text(
                      taskName,
                      style: Theme.of(context).textTheme.titleMedium?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                    decoration: BoxDecoration(
                      color: statusColor.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(12),
                      border: Border.all(color: statusColor),
                    ),
                    child: Text(
                      statusText,
                      style: TextStyle(
                        color: statusColor,
                        fontSize: 12,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 8),
              Text(
                preview,
                style: Theme.of(context).textTheme.bodySmall,
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
              ),
              const SizedBox(height: 8),
              Row(
                children: [
                  Icon(
                    Icons.access_time,
                    size: 14,
                    color: Theme.of(context).colorScheme.onSurfaceVariant,
                  ),
                  const SizedBox(width: 4),
                  Text(
                    createdAt,
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: Colors.grey,
                    ),
                  ),
                  const Spacer(),
                  if (duration > 0)
                    Text(
                      '${duration}ms',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: Colors.grey,
                      ),
                    ),
                  IconButton(
                    icon: const Icon(Icons.delete, size: 18, color: Colors.red),
                    onPressed: onDelete,
                    padding: EdgeInsets.zero,
                    constraints: const BoxConstraints(),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _LogDetailSheet extends StatelessWidget {
  final Map<String, dynamic> log;
  final ScrollController scrollController;

  const _LogDetailSheet({
    required this.log,
    required this.scrollController,
  });

  @override
  Widget build(BuildContext context) {
    final taskName = log['task_name'] ?? '未知任务';
    final content = log['content'] ?? '';
    final status = log['status'] ?? 0;
    final createdAt = log['created_at'] ?? '';
    final startedAt = log['started_at'] ?? '';
    final endedAt = log['ended_at'] ?? '';
    final duration = log['duration'] ?? 0;
    final taskType = log['task_type'] ?? '';

    Color statusColor;
    String statusText;
    switch (status) {
      case 0:
        statusColor = Colors.green;
        statusText = '成功';
        break;
      case 1:
        statusColor = Colors.red;
        statusText = '失败';
        break;
      case 2:
        statusColor = Colors.orange;
        statusText = '运行中';
        break;
      default:
        statusColor = Colors.grey;
        statusText = '未知';
    }

    return Container(
      padding: const EdgeInsets.all(16),
      child: ListView(
        controller: scrollController,
        children: [
          Center(
            child: Container(
              width: 40,
              height: 4,
              decoration: BoxDecoration(
                color: Colors.grey[300],
                borderRadius: BorderRadius.circular(2),
              ),
            ),
          ),
          const SizedBox(height: 16),
          Row(
            children: [
              Expanded(
                child: Text(
                  taskName,
                  style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                decoration: BoxDecoration(
                  color: statusColor.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(color: statusColor),
                ),
                child: Text(
                  statusText,
                  style: TextStyle(color: statusColor, fontWeight: FontWeight.bold),
                ),
              ),
            ],
          ),
          const SizedBox(height: 24),
          _buildDetailRow('任务类型', taskType),
          _buildDetailRow('创建时间', createdAt),
          _buildDetailRow('开始时间', startedAt),
          _buildDetailRow('结束时间', endedAt),
          _buildDetailRow('执行耗时', '${duration}ms'),
          const Divider(height: 32),
          Text(
            '执行日志',
            style: Theme.of(context).textTheme.titleMedium?.copyWith(
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: Colors.grey[100],
              borderRadius: BorderRadius.circular(8),
            ),
            child: SelectableText(
              content,
              style: const TextStyle(fontFamily: 'monospace', fontSize: 12),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildDetailRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 80,
            child: Text(
              label,
              style: const TextStyle(
                fontWeight: FontWeight.bold,
                color: Colors.grey,
              ),
            ),
          ),
          Expanded(child: Text(value.isEmpty ? '无' : value)),
        ],
      ),
    );
  }
}
