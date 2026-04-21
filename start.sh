#!/bin/bash

# 用户管理系统启动脚本
# 前端: http://localhost:3000
# 后端: http://localhost:8080

echo "=================================="
echo "  用户管理系统启动脚本"
echo "=================================="
echo ""

# 检查Node.js
if ! command -v node &> /dev/null; then
    echo "❌ Node.js 未安装，请先安装 Node.js 18+"
    exit 1
fi

# 检查Java
if ! command -v java &> /dev/null; then
    echo "⚠️  Java 未安装，后端服务将无法启动"
fi

# 安装前端依赖
echo "📦 正在安装前端依赖..."
cd frontend
if [ ! -d "node_modules" ]; then
    npm install
else
    echo "✅ 前端依赖已安装"
fi

# 启动前端
echo "🚀 启动前端开发服务器..."
echo "   前端地址: http://localhost:3000"
echo "   后端代理: http://localhost:8080"
echo ""
npm run dev
