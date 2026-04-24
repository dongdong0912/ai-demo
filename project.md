# 教学管理系统 (Teaching Management System)

## 项目概述

基于 Spring Boot + Vue 3 的教学管理系统，提供用户管理、教师管理、学生管理、班级管理、课程管理、个人中心、Excel导入导出等功能。

---

## 技术栈

### 后端
- **框架**: Spring Boot 2.7
- **ORM**: Spring Data JPA
- **数据库**: MySQL 8.x
- **连接池**: HikariCP
- **认证**: JWT (自定义拦截器)
- **Excel**: Alibaba EasyExcel 3.3.4
- **构建**: Maven
- **语言**: Java 8

### 前端
- **框架**: Vue 3
- **UI库**: Element Plus
- **路由**: Vue Router 4
- **状态管理**: Pinia
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
│              Views (页面)                   │
│   Login / Dashboard / UserManagement 等     │
├─────────────────────────────────────────────┤
│            Components (组件)                │
│   MainLayout 等                             │
├─────────────────────────────────────────────┤
│               API (接口)                    │
│   axios 封装、统一拦截器、Token 管理        │
├─────────────────────────────────────────────┤
│           Store (状态管理)                  │
│   Pinia 状态管理                            │
└─────────────────────────────────────────────┘
```

---

## 功能模块

| 模块 | 功能 | 状态 |
|------|------|------|
| 用户管理 | 列表、新增、编辑、删除、Excel导入导出 | ✅ 已完成 |
| 教师管理 | 列表、新增、编辑、删除、Excel导入导出 | ✅ 已完成 |
| 学生管理 | 列表、新增、编辑、删除、Excel导入导出 | ✅ 已完成 |
| 班级管理 | 列表、新增、编辑、删除、Excel导入导出 | ✅ 已完成 |
| 课程管理 | 列表、新增、编辑、删除、Excel导入导出 | ✅ 已完成 |
| 登录认证 | JWT Token 认证、路由守卫 | ✅ 已完成 |
| 个人中心 | 基本信息、头像、资料编辑、密码修改 | ✅ 已完成 |
| 仪表盘 | 数据统计、快速操作入口 | ✅ 已完成 |

### Excel 导入导出功能

| 模块 | DTO 文件 | Listener 文件 |
|------|----------|---------------|
| 用户 | UserExcelDTO.java | UserImportListener.java |
| 教师 | TeacherExcelDTO.java | TeacherImportListener.java |
| 学生 | StudentExcelDTO.java | StudentImportListener.java |
| 班级 | ClassExcelDTO.java | ClassImportListener.java |
| 课程 | CourseExcelDTO.java | CourseImportListener.java |

---

## 代码规范

### 后端 (Java)
- 遵循 Spring Boot 最佳实践
- 使用 Lombok 简化代码
- 统一响应格式 `{ code, message, data }`
- 使用 `@Slf4j` 进行日志记录
- 参数校验和异常处理

### 前端 (TypeScript/Vue)
- 使用 TypeScript 类型定义
- 组件使用 Composition API (`<script setup>`)
- API 统一封装在 `api/` 目录
- 样式使用 Element Plus 组件 + Scoped CSS
- 状态管理使用 Pinia

### 命名规范
- Java: 驼峰命名法 (camelCase)
- TypeScript: 驼峰命名法 (camelCase)
- Vue 组件: PascalCase
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
| `ExcelController.java` | Excel 导入导出接口 |
| `ExcelService.java` | Excel 导入导出服务 |
| `*ImportListener.java` | Excel 读取监听器 |

### 前端核心

| 文件 | 说明 |
|------|------|
| `utils/request.ts` | axios 实例，拦截器配置 |
| `utils/auth.ts` | Token 存储与读取 |
| `router/index.ts` | Vue Router 路由配置 |
| `stores/user.ts` | 用户状态管理 |
| `components/MainLayout.vue` | 页面主布局 |
| `views/*.vue` | 各管理页面 |
| `api/excel.ts` | Excel 导入导出 API |

---

## 开发指南

### 添加新页面

1. 前端：在 `views/` 创建 Vue 组件
2. 在 `router/index.ts` 添加路由
3. 在 `MainLayout.vue` 添加菜单项（如需要）

### 添加新 API

1. 后端：在对应 Controller 添加接口
2. 前端：在 `api/` 目录添加请求方法
3. 使用 `utils/request.ts` 的 axios 实例

### 添加新实体

1. 后端：创建 Entity 类
2. 后端：创建 Dao (Repository)
3. 后端：创建 Service 类
4. 后端：创建 Controller 类
5. 前端：添加类型定义
6. 前端：添加 API 方法

### 添加 Excel 导入导出

1. 后端：创建 ExcelDTO 类
2. 后端：创建 ImportListener 类
3. 后端：在 ExcelService 中添加方法
4. 后端：在 ExcelController 中添加接口
5. 前端：在 `api/excel.ts` 添加方法
6. 前端：在管理页面添加导入导出按钮和对话框

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
    url: jdbc:mysql://localhost:3306/teaching_management?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect

jwt:
  secret: your_jwt_secret_key_must_be_at_least_32_characters
  expiration: 86400000  # 24小时
```

**注意**: 使用前需先创建数据库 `teaching_management`:
```sql
CREATE DATABASE teaching_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
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
│       │   ├── AuthController.java
│       │   ├── UserController.java
│       │   ├── TeacherController.java
│       │   ├── StudentController.java
│       │   ├── ClassController.java
│       │   ├── CourseController.java
│       │   ├── FileController.java
│       │   └── ExcelController.java
│       ├── service/          # 服务层
│       ├── dao/              # 数据访问层
│       ├── entity/           # 实体类
│       │   ├── User.java
│       │   ├── Teacher.java
│       │   ├── Student.java
│       │   ├── GradeClass.java
│       │   └── Course.java
│       ├── dto/              # 数据传输对象
│       ├── excel/            # Excel 功能模块
│       │   ├── dto/          # Excel DTO
│       │   ├── listener/     # 读取监听器
│       │   └── service/     # Excel 服务
│       ├── config/           # 配置类
│       ├── interceptor/      # 拦截器
│       └── util/             # 工具类
└── frontend/                 # 前端项目
    ├── package.json
    ├── vite.config.ts
    └── src/
        ├── api/              # API 接口
        ├── components/       # 组件
        ├── views/            # 页面视图
        ├── router/           # 路由配置
        ├── stores/           # 状态管理
        ├── types/            # 类型定义
        ├── utils/            # 工具函数
        ├── App.vue           # 根组件
        └── main.ts           # 入口文件
```
