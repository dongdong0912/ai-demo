# 用户管理系统

基于 Spring Boot + React 的用户管理前后端分离项目

## 技术栈

### 后端
- Spring Boot 2.7.18
- Java 8
- Spring Data JPA
- H2 Database (内存数据库)
- Lombok + Logback 日志

### 前端
- React 18
- TypeScript
- Vite
- Ant Design 5 (Element UI 风格)
- Axios

## 项目结构

```
ai-demo/
├── backend/                    # 后端项目
│   ├── pom.xml
│   └── src/main/java/com/example/usermanagement/
│       ├── UserManagementApplication.java
│       ├── controller/
│       │   └── UserController.java
│       ├── service/
│       │   └── UserService.java
│       ├── dao/
│       │   └── UserDao.java
│       └── entity/
│           └── User.java
├── frontend/                   # 前端项目
│   ├── package.json
│   ├── vite.config.ts
│   └── src/
│       ├── App.tsx
│       ├── main.tsx
│       ├── api/
│       │   └── user.ts
│       ├── components/
│       │   └── UserModal.tsx
│       └── types/
│           └── user.ts
├── project.md                  # 项目规范文档
└── SPEC.md                     # 功能规格文档
```

## 快速启动

### 前置条件
- JDK 8+
- Maven 3.6+
- Node.js 16+

### 后端启动

启动后端（使用 H2 内存数据库，无需安装）：
```bash
cd backend
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端服务将在 http://localhost:3000 启动

## API 接口

| 方法   | 路径          | 描述     |
|--------|---------------|----------|
| GET    | /api/users    | 获取用户列表 |
| GET    | /api/users/{id} | 获取单个用户 |
| POST   | /api/users    | 创建用户   |
| PUT    | /api/users/{id} | 更新用户   |
| DELETE | /api/users/{id} | 删除用户   |
