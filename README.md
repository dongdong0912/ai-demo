# 教学管理系统

基于 Spring Boot + React 的教学管理前后端分离项目

## 技术栈

### 后端
- Spring Boot 2.7.18
- Java 8
- Spring Data JPA
- MySQL 8.x
- HikariCP 连接池
- JWT 认证
- Lombok + Logback 日志

### 前端
- React 18
- TypeScript
- Vite 4.x
- React Router 6 (路由导航)
- Ant Design 5
- Axios

## 项目结构

```
ai-demo/
├── backend/                    # 后端项目
│   ├── pom.xml
│   └── src/main/java/com/example/usermanagement/
│       ├── UserManagementApplication.java
│       ├── config/            # 配置类
│       │   ├── DataInitializer.java
│       │   └── WebConfig.java
│       ├── controller/
│       │   ├── AuthController.java    # 认证
│       │   ├── UserController.java     # 用户+个人中心
│       │   ├── TeacherController.java  # 教师
│       │   └── FileController.java     # 文件上传
│       ├── service/
│       │   ├── AuthService.java
│       │   ├── UserService.java
│       │   └── TeacherService.java
│       ├── dao/
│       │   ├── UserDao.java
│       │   └── TeacherDao.java
│       ├── entity/
│       │   ├── User.java
│       │   └── Teacher.java
│       ├── dto/
│       │   └── LoginRequest.java
│       ├── interceptor/
│       │   └── AuthInterceptor.java
│       └── util/
│           └── JwtUtil.java
├── frontend/                   # 前端项目
│   ├── package.json
│   ├── vite.config.ts
│   └── src/
│       ├── App.tsx
│       ├── main.tsx
│       ├── api/
│       │   ├── auth.ts
│       │   ├── user.ts
│       │   └── teacher.ts
│       ├── components/
│       │   ├── AuthGuard.tsx
│       │   ├── MainLayout.tsx
│       │   └── SideMenu.tsx
│       ├── pages/
│       │   ├── Login.tsx
│       │   ├── UserManagement.tsx
│       │   ├── TeacherManagement.tsx
│       │   └── Profile.tsx            # 个人中心
│       ├── types/
│       │   └── user.ts
│       └── utils/
│           ├── auth.ts
│           └── request.ts
├── project.md                  # 项目规范文档
└── SPEC.md                    # 功能规格文档
```

## 功能模块

### 当前模块
- **用户管理**：用户列表、新增、编辑、删除
- **老师管理**：老师列表、新增、编辑、删除
- **登录认证**：JWT Token 认证、路由守卫
- **个人中心**：基本信息、头像、资料编辑、密码修改

### 未来可扩展模块
- 学生管理
- 课程管理
- 班级管理
- 成绩管理
- ...

## 快速启动

### 前置条件
- JDK 8+
- Maven 3.6+
- Node.js 16+
- MySQL 8.x

### 1. 创建数据库
```sql
CREATE DATABASE teaching_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 后端启动
```bash
cd backend
mvn spring-boot:run
```
后端服务将在 http://localhost:8080 启动

### 3. 前端启动
```bash
cd frontend
npm install
npm run dev
```
前端服务将在 http://localhost:5173 启动

## 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 系统管理员 |
| teacher | 123456 | 普通用户 |

## API 接口

### 认证接口
| 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|
| POST | /api/auth/login | 用户登录 | 否 |
| POST | /api/auth/register | 用户注册 | 否 |
| GET | /api/auth/userinfo | 获取用户信息 | 是 |

### 用户接口
| 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|
| GET | /api/users | 获取用户列表 | 是 |
| GET | /api/users/{id} | 获取单个用户 | 是 |
| POST | /api/users | 创建用户 | 是 |
| PUT | /api/users/{id} | 更新用户 | 是 |
| DELETE | /api/users/{id} | 删除用户 | 是 |
| GET | /api/users/profile | 获取个人资料 | 是 |
| PUT | /api/users/profile | 更新个人资料 | 是 |
| PUT | /api/users/password | 修改密码 | 是 |

### 老师接口
| 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|
| GET | /api/teachers | 获取老师列表 | 是 |
| GET | /api/teachers/{id} | 获取单个老师 | 是 |
| POST | /api/teachers | 创建老师 | 是 |
| PUT | /api/teachers/{id} | 更新老师 | 是 |
| DELETE | /api/teachers/{id} | 删除老师 | 是 |

### 文件上传接口
| 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|
| POST | /api/upload/image | 上传图片 | 是 |
