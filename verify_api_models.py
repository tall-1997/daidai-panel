#!/usr/bin/env python3
"""
API数据模型验证脚本
验证App数据模型与后端API返回格式是否完全匹配
"""

import json
import requests
import sys

BASE_URL = "http://127.0.0.1:5701"

def get_token():
    """获取认证Token"""
    resp = requests.post(f"{BASE_URL}/api/v1/auth/login", json={"username": "admin", "password": "admin123"})
    return resp.json()["access_token"]

def test_api(name, endpoint, token, method="GET", data=None):
    """测试API端点"""
    headers = {"Authorization": f"Bearer {token}"}
    if method == "GET":
        resp = requests.get(f"{BASE_URL}{endpoint}", headers=headers)
    else:
        resp = requests.post(f"{BASE_URL}{endpoint}", headers=headers, json=data)
    
    print(f"\n=== {name} ===")
    print(f"Status: {resp.status_code}")
    result = resp.json()
    print(json.dumps(result, indent=2, ensure_ascii=False))
    return result

def verify_tasks_api(token):
    """验证任务API返回格式"""
    result = test_api("任务列表API", "/api/v1/tasks?page=1&page_size=10", token)
    
    # 验证必需字段
    assert "data" in result, "缺少data字段"
    assert "page" in result, "缺少page字段"
    assert "page_size" in result, "缺少page_size字段"
    assert "total" in result, "缺少total字段"
    
    # 验证任务数据结构
    if result["data"]:
        task = result["data"][0]
        required_fields = ["id", "name", "command", "status", "created_at", "updated_at"]
        for field in required_fields:
            assert field in task, f"任务缺少{field}字段"
        
        # 验证可选字段
        optional_fields = [
            "cron_expression", "cron_expressions", "task_type", "is_pinned",
            "last_run_at", "last_run_status", "last_running_time", "next_run_at",
            "log_path", "allow_multiple_instances", "depends_on", "labels",
            "display_labels", "max_retries", "notification_channel_id",
            "notify_on_failure", "notify_on_success", "pid", "random_delay_seconds",
            "retry_interval", "sort_order", "stop_schedule", "task_after",
            "task_before", "timeout"
        ]
        for field in optional_fields:
            if field in task:
                print(f"  ✓ 任务包含{field}字段")
    
    print("✓ 任务API验证通过")
    return True

def verify_envs_api(token):
    """验证环境变量API返回格式"""
    result = test_api("环境变量列表API", "/api/v1/envs?page=1&page_size=10", token)
    
    # 验证必需字段
    assert "data" in result, "缺少data字段"
    assert "page" in result, "缺少page字段"
    assert "page_size" in result, "缺少page_size字段"
    assert "total" in result, "缺少total字段"
    
    # 验证环境变量数据结构
    if result["data"]:
        env = result["data"][0]
        required_fields = ["id", "name", "value", "created_at", "updated_at"]
        for field in required_fields:
            assert field in env, f"环境变量缺少{field}字段"
        
        # 验证可选字段
        optional_fields = ["enabled", "group", "groups", "position", "remarks", "sort_order"]
        for field in optional_fields:
            if field in env:
                print(f"  ✓ 环境变量包含{field}字段")
    
    print("✓ 环境变量API验证通过")
    return True

def verify_deps_api(token):
    """验证依赖API返回格式"""
    result = test_api("依赖列表API", "/api/v1/deps?type=nodejs", token)
    
    # 验证必需字段
    assert "data" in result, "缺少data字段"
    assert "total" in result, "缺少total字段"
    
    # 验证依赖数据结构
    if result["data"]:
        dep = result["data"][0]
        required_fields = ["id", "type", "name", "status", "created_at", "updated_at"]
        for field in required_fields:
            assert field in dep, f"依赖缺少{field}字段"
    
    print("✓ 依赖API验证通过")
    return True

def verify_scripts_api(token):
    """验证脚本API返回格式"""
    result = test_api("脚本列表API", "/api/v1/scripts", token)
    
    # 验证必需字段
    assert "data" in result, "缺少data字段"
    assert "total" in result, "缺少total字段"
    
    # 验证脚本数据结构
    if result["data"]:
        script = result["data"][0]
        required_fields = ["name", "path", "size"]
        for field in required_fields:
            assert field in script, f"脚本缺少{field}字段"
        
        # 验证可选字段
        if "mtime" in script:
            print("  ✓ 脚本包含mtime字段")
    
    print("✓ 脚本API验证通过")
    return True

def verify_logs_api(token):
    """验证日志API返回格式"""
    result = test_api("日志列表API", "/api/v1/logs?page=1&page_size=10", token)
    
    # 验证必需字段
    assert "data" in result, "缺少data字段"
    assert "page" in result, "缺少page字段"
    assert "page_size" in result, "缺少page_size字段"
    assert "total" in result, "缺少total字段"
    
    # 验证日志数据结构
    if result["data"]:
        log = result["data"][0]
        required_fields = ["id", "task_id", "task_name", "status", "started_at"]
        for field in required_fields:
            assert field in log, f"日志缺少{field}字段"
        
        # 验证可选字段
        optional_fields = ["task_type", "labels", "task", "created_at", "updated_at"]
        for field in optional_fields:
            if field in log:
                print(f"  ✓ 日志包含{field}字段")
    
    print("✓ 日志API验证通过")
    return True

def verify_system_info_api(token):
    """验证系统信息API返回格式"""
    result = test_api("系统信息API", "/api/v1/system/info", token)
    
    # 验证必需字段
    assert "data" in result, "缺少data字段"
    
    # 验证系统信息数据结构
    info = result["data"]
    required_fields = [
        "hostname", "machine_code", "cpu_usage", "memory_total", "memory_used",
        "memory_free", "memory_usage", "disk_total", "disk_used", "disk_free",
        "disk_usage", "uptime", "goroutines", "go_version", "os", "arch",
        "num_cpu", "data_dir", "net_rx_bytes", "net_tx_bytes", "net_rx_speed",
        "net_tx_speed"
    ]
    for field in required_fields:
        assert field in info, f"系统信息缺少{field}字段"
        print(f"  ✓ 系统信息包含{field}字段")
    
    print("✓ 系统信息API验证通过")
    return True

def verify_health_check_api(token):
    """验证健康检查API返回格式"""
    result = test_api("健康检查API", "/api/v1/system/health-check", token)
    
    # 验证必需字段
    assert "items" in result, "缺少items字段"
    assert "last_checked_at" in result, "缺少last_checked_at字段"
    
    print("✓ 健康检查API验证通过")
    return True

def verify_dashboard_api(token):
    """验证仪表盘API返回格式"""
    result = test_api("仪表盘API", "/api/v1/system/dashboard", token)
    
    # 验证必需字段
    assert "data" in result, "缺少data字段"
    
    # 验证仪表盘数据结构
    dashboard = result["data"]
    required_fields = [
        "task_count", "enabled_tasks", "running_tasks", "today_logs",
        "success_logs", "failed_logs", "env_count", "recent_logs", "daily_stats"
    ]
    for field in required_fields:
        assert field in dashboard, f"仪表盘缺少{field}字段"
    
    # 验证可选字段
    optional_fields = ["prev_task_count", "range_days", "sub_count", "yesterday_logs", "yesterday_success"]
    for field in optional_fields:
        if field in dashboard:
            print(f"  ✓ 仪表盘包含{field}字段")
    
    print("✓ 仪表盘API验证通过")
    return True

def verify_stats_api(token):
    """验证统计API返回格式"""
    result = test_api("统计API", "/api/v1/system/stats", token)
    
    # 验证必需字段
    assert "data" in result, "缺少data字段"
    
    # 验证统计数据结构
    stats = result["data"]
    assert "tasks" in stats, "统计缺少tasks字段"
    assert "logs" in stats, "统计缺少logs字段"
    assert "scripts" in stats, "统计缺少scripts字段"
    
    print("✓ 统计API验证通过")
    return True

def verify_panel_log_api(token):
    """验证面板日志API返回格式"""
    result = test_api("面板日志API", "/api/v1/system/panel-log?lines=10", token)
    
    # 验证必需字段
    assert "data" in result, "缺少data字段"
    
    # 验证面板日志数据结构
    log_data = result["data"]
    assert "logs" in log_data, "面板日志缺少logs字段"
    assert "total" in log_data, "面板日志缺少total字段"
    
    print("✓ 面板日志API验证通过")
    return True

def main():
    """主测试函数"""
    print("=" * 60)
    print("开始验证App数据模型与后端API返回格式")
    print("=" * 60)
    
    # 获取Token
    token = get_token()
    print(f"Token: {token[:30]}...")
    
    # 测试所有API
    tests = [
        verify_tasks_api,
        verify_envs_api,
        verify_deps_api,
        verify_scripts_api,
        verify_logs_api,
        verify_system_info_api,
        verify_health_check_api,
        verify_dashboard_api,
        verify_stats_api,
        verify_panel_log_api,
    ]
    
    passed = 0
    failed = 0
    
    for test in tests:
        try:
            if test(token):
                passed += 1
        except Exception as e:
            print(f"✗ {test.__name__} 失败: {e}")
            failed += 1
    
    print("\n" + "=" * 60)
    print(f"测试结果: {passed} 通过, {failed} 失败")
    print("=" * 60)
    
    return failed == 0

if __name__ == "__main__":
    success = main()
    sys.exit(0 if success else 1)
