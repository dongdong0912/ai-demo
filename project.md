# 教学管理系统 (Teaching Management System)

## 项目概述

基于 Spring Boot + React 的教学管理系统，提供用户管理、教师管理、个人中心等功能。

---

## 技术栈

### 后端
- **框架**: Spring Boot 2.7
- **ORM**: Spring Data JPA
- **数据库**: MySQL
- **认证**: JWT (jjwt)
- **安全**: Spring Security (仅用于密码加密 BCrypt)
- **构建**: Maven

### 前端
- **框架**: React 18
- **UI库**: Ant Design 5
- **路由**: React Router 6
- **构建**: Vite
- **HTTP**: Axios
- **语言**: TypeScript

---

## 架构设计

### 后端架构（三层架构）
```
┌─────────────────────────────────────────────┐
│              Controller 层                  │
│   (处理请求、参数校验、调用 Service)          │
├─────────────────────────────────────────────┤
│               Service 层                    │
│   (业务逻辑处理、事务管理)                   │
├─────────────────────────────────────────────┤
│                DAO 层                       │
│   (数据访问、JPA Repository)                 │
├─────────────────────────────────────────────┤
│              数据库 (MySQL)                 │
└─────────────────────────────────────────────┘
```

### 前端架构
```
┌─────────────────────────────────────────────┐
│              Pages (页面)                   │
│   Login / UserManagement / Profile 等       │
├─────────────────────────────────────────────┤
│            Components (组件)                │
│   MainLayout / SideMenu / AuthGuard 等     │
├─────────────────────────────────────────────┤
│               API (接口)                    │
│   axios 封装、统一拦截器、Token 管理         │
└─────────────────────────────────────────────┘
```

---

## 功能模块

| 模块 | 功能 | 状态 |
|------|------|------|
| 用户管理 | 列表、新增、编辑、删除 | ✅ 已完成 |
| 教师管理 | 列表、新增、编辑、删除 | ✅ 已完成 |
| 登录认证 | JWT Token 认证 | ✅ 已完成 |
| 个人中心 | 基本信息、头像、资料编辑、密码修改 | ✅ 已完成 |

---

## 代码规范

### 后端 (Java)
- 遵循 Spring Boot 最佳实践
- 使用 Lombok 简化代码
- 统一响应格式 `{ code, message, data }`
- 使用 `@Slf4j` 进行日志记录
- 参数校验和异常处理

### 前端 (TypeScript/React)
- 使用 TypeScript 类型定义
- 组件使用 Functional Component + Hooks
- API 统一封装在 `api/` 目录
- 样式使用 Ant Design + CSS Modules
- 状态管理使用 React Hooks (useState, useEffect)

### 命名规范
- Java: 驼峰命名法 (camelCase)
- TypeScript: 驼峰命名法 (camelCase)
- 文件名: 驼峰/PascalCase (组件)
- API: RESTful 风格

---

## 核心文件说明

### 后端核心

| 文件 | 说明 |
|------|------|
| `JwtUtil.java` | JWT Token 生成与解析 |
| `AuthInterceptor.java` | 请求拦截，验证 Token |
| `AuthService.java` | 登录逻辑、用户信息获取 |
| `UserController.java` | 用户 CRUD + 个人中心接口 |

### 前端核心

| 文件 | 说明 |
|------|------|
| `request.ts` | axios 实例，拦截器配置 |
| `auth.ts` | Token 存储与读取 |
| `AuthGuard.tsx` | 路由守卫组件 |
| `MainLayout.tsx` | 页面主布局 |
| `Profile.tsx` | 个人中心页面 |

---

## 开发指南

### 添加新页面

1. 在 `frontend/src/pages/` 创建页面组件
2. 在 `App.tsx` 添加路由
3. 在 `SideMenu.tsx` 添加菜单项（如需要）

### 添加新 API

1. 后端：在对应 Controller 添加接口
2. 前端：在 `api/` 目录添加请求方法
3. 使用 `request.ts` 的 axios 实例

### 添加新实体

1. 后端：创建 Entity 类
2. 后端：创建 Dao (Repository)
3. 后端：创建 Service 类
4. 后端：创建 Controller 类
5. 前端：添加类型定义
6. 前端：添加 API 方法

---

## 部署说明

### 开发环境
```bash
# 后端
cd backend && mvn spring-boot:run

# 前端
cd frontend && npm install && npm run dev
```

### 环境变量

**后端** (`application.yml`)
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/teaching_system
    username: root
    password: your_password

jwt:
  secret: your_jwt_secret_key
  expiration: 86400000
```

**前端** (`vite.config.ts`)
```typescript
export default defineConfig({
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

---

## 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 系统管理员 |
| teacher | 123456 | 普通用户 |

---

## 项目结构总览

```
ai-demo/
├── SPEC.md                    # 功能规格说明
├── project.md                 # 项目上下文（本文档）
├── README.md                  # 项目说明
├── backend/                   # 后端项目
│   ├── pom.xml
│   └── src/main/java/com/example/usermanagement/
│       ├── controller/        # 控制器
│       ├── service/           # 服务层
│       ├── dao/              # 数据访问层
│       ├── entity/           # 实体类
│       ├── dto/              # 数据传输对象
│       ├── config/          # 配置类
│       ├── interceptor/     # 拦截器
│       └── util/            # 工具类
└── frontend/                 # 前端项目
    ├── package.json
    ├── vite.config.ts
    └── src/
        ├── api/             # API 接口
        ├── components/       # 组件
        ├── pages/           # 页面
        ├── types/          # 类型定义
        ├── utils/          # 工具函数
        ├── App.tsx         # 路由配置
        └── main.tsx        # 入口文件
```
