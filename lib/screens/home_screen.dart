import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';
import 'tasks_screen.dart';
import 'envs_screen.dart';
import 'dependencies_screen.dart';
import 'scripts_screen.dart';
import 'logs_screen.dart';
import 'notifications_screen.dart';
import 'system_screen.dart';
import 'settings_screen.dart';
import 'quick_actions_screen.dart';
import 'stats_screen.dart';
import 'config_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  int _selectedIndex = 0;
  bool _isSidebarExpanded = false;

  final List<_NavigationItem> _navigationItems = [
    _NavigationItem(Icons.list_alt, '任务'),
    _NavigationItem(Icons.settings_ethernet, '环境变量'),
    _NavigationItem(Icons.extension, '依赖管理'),
    _NavigationItem(Icons.code, '脚本'),
    _NavigationItem(Icons.article, '日志'),
    _NavigationItem(Icons.notifications, '通知'),
    _NavigationItem(Icons.computer, '系统'),
    _NavigationItem(Icons.flash_on, '快捷操作'),
    _NavigationItem(Icons.bar_chart, '统计'),
    _NavigationItem(Icons.settings, '配置'),
    _NavigationItem(Icons.tune, '设置'),
  ];

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
      _isSidebarExpanded = false; // 点击选项后自动收起侧边栏
    });
  }

  @override
  Widget build(BuildContext context) {
    final authService = context.watch<AuthService>();
    
    return Scaffold(
      appBar: AppBar(
        title: Text(_navigationItems[_selectedIndex].title),
        leading: IconButton(
          icon: Icon(_isSidebarExpanded ? Icons.menu_open : Icons.menu),
          onPressed: () {
            setState(() {
              _isSidebarExpanded = !_isSidebarExpanded;
            });
          },
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.search),
            onPressed: () {
              // TODO: Implement search
            },
          ),
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () {
              // TODO: Implement refresh
            },
          ),
          PopupMenuButton(
            itemBuilder: (context) => [
              PopupMenuItem(
                child: ListTile(
                  leading: const Icon(Icons.person),
                  title: Text(authService.username ?? '用户'),
                  contentPadding: EdgeInsets.zero,
                ),
              ),
              const PopupMenuItem(
                child: ListTile(
                  leading: const Icon(Icons.logout),
                  title: Text('退出登录'),
                  contentPadding: EdgeInsets.zero,
                ),
              ),
            ],
            onSelected: (value) {
              if (value == 1) {
                authService.logout();
              }
            },
          ),
        ],
      ),
      body: Row(
        children: [
          // 侧边栏
          AnimatedContainer(
            duration: const Duration(milliseconds: 200),
            width: _isSidebarExpanded ? 200 : 60,
            decoration: BoxDecoration(
              color: Theme.of(context).colorScheme.surface,
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withOpacity(0.1),
                  blurRadius: 4,
                  offset: const Offset(2, 0),
                ),
              ],
            ),
            child: Column(
              children: [
                // 顶部用户信息
                if (_isSidebarExpanded)
                  Container(
                    padding: const EdgeInsets.all(16),
                    child: Row(
                      children: [
                        CircleAvatar(
                          backgroundColor: Theme.of(context).colorScheme.primary,
                          child: Text(
                            (authService.username ?? 'U')[0].toUpperCase(),
                            style: TextStyle(color: Theme.of(context).colorScheme.onPrimary),
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: Text(
                            authService.username ?? '用户',
                            style: const TextStyle(fontWeight: FontWeight.bold),
                            overflow: TextOverflow.ellipsis,
                          ),
                        ),
                      ],
                    ),
                  ),
                if (!_isSidebarExpanded)
                  const SizedBox(height: 16),
                // 导航选项
                Expanded(
                  child: ListView.builder(
                    itemCount: _navigationItems.length,
                    itemBuilder: (context, index) {
                      final item = _navigationItems[index];
                      final isSelected = _selectedIndex == index;
                      
                      return InkWell(
                        onTap: () => _onItemTapped(index),
                        child: Container(
                          padding: EdgeInsets.symmetric(
                            horizontal: _isSidebarExpanded ? 16 : 0,
                            vertical: 12,
                          ),
                          child: Row(
                            mainAxisAlignment: _isSidebarExpanded 
                                ? MainAxisAlignment.start 
                                : MainAxisAlignment.center,
                            children: [
                              Icon(
                                item.icon,
                                color: isSelected 
                                    ? Theme.of(context).colorScheme.primary
                                    : Theme.of(context).colorScheme.onSurface.withOpacity(0.7),
                                size: 24,
                              ),
                              if (_isSidebarExpanded) ...[
                                const SizedBox(width: 12),
                                Expanded(
                                  child: Text(
                                    item.title,
                                    style: TextStyle(
                                      color: isSelected 
                                          ? Theme.of(context).colorScheme.primary
                                          : Theme.of(context).colorScheme.onSurface,
                                      fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                                    ),
                                    overflow: TextOverflow.ellipsis,
                                  ),
                                ),
                              ],
                            ],
                          ),
                        ),
                      );
                    },
                  ),
                ),
                // 底部展开/收起按钮
                IconButton(
                  icon: Icon(_isSidebarExpanded ? Icons.chevron_left : Icons.chevron_right),
                  onPressed: () {
                    setState(() {
                      _isSidebarExpanded = !_isSidebarExpanded;
                    });
                  },
                ),
                const SizedBox(height: 8),
              ],
            ),
          ),
          // 主内容区域
          Expanded(
            child: _getSelectedScreen(),
          ),
        ],
      ),
    );
  }

  Widget _getSelectedScreen() {
    switch (_selectedIndex) {
      case 0:
        return const TasksScreen();
      case 1:
        return const EnvsScreen();
      case 2:
        return const DependenciesScreen();
      case 3:
        return const ScriptsScreen();
      case 4:
        return const LogsScreen();
      case 5:
        return const NotificationsScreen();
      case 6:
        return const SystemScreen();
      case 7:
        return const QuickActionsScreen();
      case 8:
        return const StatsScreen();
      case 9:
        return const ConfigScreen();
      case 10:
        return const SettingsScreen();
      default:
        return const TasksScreen();
    }
  }
}

class _NavigationItem {
  final IconData icon;
  final String title;

  _NavigationItem(this.icon, this.title);
}
