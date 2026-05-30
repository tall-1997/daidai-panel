import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';
import 'dart:convert';

class TerminalScreen extends StatefulWidget {
  const TerminalScreen({super.key});

  @override
  State<TerminalScreen> createState() => _TerminalScreenState();
}

class _TerminalScreenState extends State<TerminalScreen> {
  final _commandController = TextEditingController();
  final _scrollController = ScrollController();
  final List<Map<String, dynamic>> _output = [];
  bool _isExecuting = false;

  @override
  void dispose() {
    _commandController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  void _scrollToBottom() {
    if (_scrollController.hasClients) {
      _scrollController.animateTo(
        _scrollController.position.maxScrollExtent,
        duration: const Duration(milliseconds: 100),
        curve: Curves.easeOut,
      );
    }
  }

  Future<void> _executeCommand() async {
    final command = _commandController.text.trim();
    if (command.isEmpty) return;

    setState(() {
      _output.add({
        'command': command,
        'status': 'running',
        'output': '',
        'timestamp': DateTime.now().toString(),
      });
      _isExecuting = true;
      _commandController.clear();
    });

    _scrollToBottom();

    try {
      final authService = context.read<AuthService>();
      final api = authService.apiService;

      // Execute command via API
      final response = await api.post('/system/execute', body: {'command': command});

      if (mounted) {
        setState(() {
          if (response.statusCode == 200) {
            final data = jsonDecode(response.body);
            _output.last['output'] = data['output'] ?? data['data']?['output'] ?? '命令已执行';
            _output.last['status'] = 'success';
          } else {
            _output.last['output'] = '执行失败: HTTP ${response.statusCode}';
            _output.last['status'] = 'error';
          }
          _isExecuting = false;
        });
        _scrollToBottom();
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _output.last['output'] = '执行错误: $e';
          _output.last['status'] = 'error';
          _isExecuting = false;
        });
        _scrollToBottom();
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('在线终端'),
        actions: [
          IconButton(
            icon: const Icon(Icons.delete_sweep),
            onPressed: () => setState(() => _output.clear()),
            tooltip: '清空输出',
          ),
        ],
      ),
      body: Column(
        children: [
          // Quick commands
          Container(
            padding: const EdgeInsets.all(8),
            child: Wrap(
              spacing: 8,
              runSpacing: 4,
              children: [
                _buildQuickCommand('ls', 'ls -la'),
                _buildQuickCommand('pwd', 'pwd'),
                _buildQuickCommand('date', 'date'),
                _buildQuickCommand('uname', 'uname -a'),
                _buildQuickCommand('df', 'df -h'),
                _buildQuickCommand('free', 'free -h'),
                _buildQuickCommand('ps', 'ps aux | head -20'),
                _buildQuickCommand('top', 'top -bn1 | head -20'),
              ],
            ),
          ),
          const Divider(height: 1),
          // Output
          Expanded(
            child: Container(
              color: Colors.black87,
              child: ListView.builder(
                controller: _scrollController,
                padding: const EdgeInsets.all(8),
                itemCount: _output.length,
                itemBuilder: (context, index) {
                  final item = _output[index];
                  return Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      // Command
                      Row(
                        children: [
                          const Text('\$ ', style: TextStyle(color: Colors.green, fontFamily: 'monospace')),
                          Expanded(
                            child: Text(
                              item['command'],
                              style: const TextStyle(color: Colors.white, fontFamily: 'monospace'),
                            ),
                          ),
                          if (item['status'] == 'running')
                            const SizedBox(
                              width: 12,
                              height: 12,
                              child: CircularProgressIndicator(strokeWidth: 2, color: Colors.orange),
                            ),
                        ],
                      ),
                      // Output
                      if (item['output'].isNotEmpty)
                        Container(
                          padding: const EdgeInsets.only(left: 16, top: 4, bottom: 8),
                          child: Text(
                            item['output'],
                            style: TextStyle(
                              color: item['status'] == 'error' ? Colors.red : Colors.white70,
                              fontFamily: 'monospace',
                              fontSize: 13,
                            ),
                          ),
                        ),
                    ],
                  );
                },
              ),
            ),
          ),
          // Input
          Container(
            padding: const EdgeInsets.all(8),
            color: Colors.grey[900],
            child: Row(
              children: [
                const Text('\$ ', style: TextStyle(color: Colors.green, fontFamily: 'monospace')),
                Expanded(
                  child: TextField(
                    controller: _commandController,
                    style: const TextStyle(color: Colors.white, fontFamily: 'monospace'),
                    decoration: const InputDecoration(
                      hintText: '输入命令...',
                      hintStyle: TextStyle(color: Colors.grey),
                      border: InputBorder.none,
                      isDense: true,
                      contentPadding: EdgeInsets.symmetric(vertical: 8),
                    ),
                    onSubmitted: (_) => _executeCommand(),
                    enabled: !_isExecuting,
                  ),
                ),
                IconButton(
                  icon: Icon(_isExecuting ? Icons.hourglass_empty : Icons.send),
                  color: Colors.green,
                  onPressed: _isExecuting ? null : _executeCommand,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildQuickCommand(String label, String command) {
    return ActionChip(
      label: Text(label, style: const TextStyle(fontSize: 12)),
      onPressed: () {
        _commandController.text = command;
        _executeCommand();
      },
    );
  }
}
