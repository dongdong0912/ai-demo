# Vue3 前端重构完成

## 📋 项目概览

本项目已成功从 **React + Ant Design** 重构为 **Vue3 + TypeScript + Vite + Pinia + Vue Router + Element Plus + Axios** 技术栈。

## 🚀 快速开始

### 1. 安装依赖

```bash
cd frontend
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:3000

### 3. 构建生产版本

```bash
npm run build
```

## 📁 项目结构

```
frontend/src/
├── api/                    # API 接口层
│   ├── auth.ts            # 认证相关 API
│   ├── user.ts            # 用户管理 API
│   └── teacher.ts         # 教师管理 API
├── assets/
│   └── styles/
│       └── global.css     # 全局样式
├── components/            # 公共组件
│   ├── MainLayout.vue     # 主布局（侧边栏 + 内容区）
│   ├── SideMenu.vue       # 侧边栏菜单
│   └── AppHeader.vue      # 顶部导航
├── router/
│   └── index.ts          # 路由配置 + 导航守卫
├── stores/               # Pinia 状态管理
│   ├── user.ts           # 用户状态（登录、用户信息）
│   └── app.ts            # 应用全局状态（折叠状态等）
├── types/
│   └── index.ts          # TypeScript 类型定义
├── utils/
│   ├── request.ts        # Axios 实例封装
│   └── auth.ts           # Token 存储/读取
├── views/                # 业务页面
│   ├── Login.vue         # 登录页
│   ├── UserManagement.vue # 用户管理
│   ├── TeacherManagement.vue # 教师管理
│   └── Profile.vue       # 个人中心
├── App.vue               # 根组件
└── main.ts               # 应用入口
```

## 🎯 核心功能

### ✅ 已实现功能

1. **用户认证**
   - JWT Token 认证
   - 自动登录状态管理
   - 路由守卫保护
   - 登出功能

2. **用户管理** (管理员)
   - 用户列表展示
   - 添加用户
   - 编辑用户
   - 删除用户

3. **教师管理**
   - 教师列表展示
   - 添加教师
   - 编辑教师
   - 删除教师

4. **个人中心**
   - 查看个人信息
   - 修改个人信息
   - 修改密码

## 🔧 技术栈详情

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4+ | 核心框架，使用 Composition API |
| TypeScript | 5.4+ | 类型安全 |
| Vite | 5.1+ | 构建工具 |
| Pinia | 2.1+ | 状态管理 |
| Vue Router | 4.3+ | 路由管理 |
| Element Plus | 2.6+ | UI 组件库 |
| Axios | 1.6+ | HTTP 客户端 |

## 📝 关键文件说明

### request.ts (Axios 封装)

- 自动添加 Authorization Token
- 统一错误处理
- 401 自动跳转登录
- Element Plus 消息提示

### stores/user.ts (用户状态)

- 登录/登出
- 用户信息管理
- 个人资料获取
- 密码修改

### router/index.ts (路由配置)

- 路由守卫
- 权限控制
- 自动跳转

## 🎨 组件说明

### MainLayout.vue
主布局容器，包含侧边栏、顶栏和内容区域。

### SideMenu.vue
侧边栏菜单组件，根据用户角色显示不同菜单项。

### AppHeader.vue
顶部导航栏，包含折叠按钮和用户信息。

## 🔐 认证流程

1. 用户在登录页输入用户名密码
2. 调用 `/api/auth/login` 接口
3. 后端返回 JWT Token
4. 前端存储 Token 到 localStorage
5. 后续请求自动携带 Token
6. Token 过期或无效时自动跳转登录页

## 📦 后端 API

后端 API 保持不变，位于 `backend/` 目录。

启动后端：
```bash
cd backend
mvn spring-boot:run
```

后端运行在 http://localhost:8080

## 🎉 注意事项

1. **确保后端已启动** - 前端代理会转发请求到后端
2. **Node 版本** - 推荐 Node.js 18+
3. **首次运行** - 需要执行 `npm install` 安装依赖

## 🔄 与 React 版本的差异

| React 版本 | Vue3 版本 | 说明 |
|------------|-----------|------|
| useState | ref/reactive | 响应式状态 |
| useContext + useReducer | Pinia Store | 状态管理 |
| React Router | Vue Router | 路由管理 |
| AuthGuard 组件 | 路由守卫 | 权限控制 |
| Ant Design | Element Plus | UI 组件库 |

## 📞 支持

如有问题，请查看：
- Element Plus 文档: https://element-plus.org/
- Vue3 文档: https://vuejs.org/
- Pinia 文档: https://pinia.vuejs.org/
