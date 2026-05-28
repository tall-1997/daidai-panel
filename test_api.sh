#!/bin/bash
# API测试脚本 - 验证App数据模型与后端API返回格式是否匹配

BASE_URL="http://127.0.0.1:5701"

# 测试函数
test_api() {
    local name=$1
    local endpoint=$2
    local method=${3:-GET}
    local data=$4
    
    # 每次都获取新Token
    local token=$(curl -s "$BASE_URL/api/v1/auth/login" -H 'Content-Type: application/json' -d '{"username":"admin","password":"admin123"}' | python3 -c "import sys, json; print(json.load(sys.stdin)['access_token'])" 2>/dev/null)
    
    echo -e "\n=== $name ==="
    if [ "$method" = "GET" ]; then
        curl -s "$BASE_URL$endpoint" -H "Authorization: Bearer $token" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL$endpoint" -H "Authorization: Bearer $token"
    else
        curl -s "$BASE_URL$endpoint" -X "$method" -H "Authorization: Bearer $token" -H 'Content-Type: application/json' -d "$data" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL$endpoint" -X "$method" -H "Authorization: Bearer $token" -H 'Content-Type: application/json' -d "$data"
    fi
}

# 测试所有API
echo "=========================================="
echo "开始测试所有API端点"
echo "=========================================="

# 1. 认证API
test_api "登录API" "/api/v1/auth/login" "POST" '{"username":"admin","password":"admin123"}'

# 2. 任务API
test_api "任务列表API" "/api/v1/tasks?page=1&page_size=10"

# 3. 环境变量API
test_api "环境变量列表API" "/api/v1/envs?page=1&page_size=10"

# 4. 依赖API
test_api "依赖列表API (nodejs)" "/api/v1/deps?type=nodejs"

# 5. 脚本API
test_api "脚本列表API" "/api/v1/scripts"
test_api "脚本内容API" "/api/v1/scripts/content?path=notify.py"

# 6. 日志API
test_api "日志列表API" "/api/v1/logs?page=1&page_size=10"

# 7. 系统API
test_api "系统信息API" "/api/v1/system/info"
test_api "系统健康检查API" "/api/v1/system/health-check"
test_api "系统仪表盘API" "/api/v1/system/dashboard"
test_api "系统统计API" "/api/v1/system/stats"
test_api "面板日志API" "/api/v1/system/panel-log?lines=10"

echo -e "\n=========================================="
echo "API测试完成"
echo "=========================================="
