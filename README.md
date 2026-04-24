# 教学管理系统

基于 Spring Boot + Vue 3 的教学管理前后端分离项目

## 技术栈

### 后端
- Spring Boot 2.7.18
- Java 8
- Spring Data JPA
- MySQL 8.x
- HikariCP 连接池
- JWT 认证
- Alibaba EasyExcel 3.3.4
- Lombok + Logback 日志

### 前端
- Vue 3
- TypeScript
- Vite 4.x
- Vue Router 4 (路由导航)
- Element Plus (UI 组件库)
- Pinia (状态管理)
- Axios

## 项目结构

```
ai-demo/
├── backend/                    # 后端项目
│   ├── pom.xml
│   └── src/main/java/com/example/usermanagement/
│       ├── UserManagementApplication.java
│       ├── config/             # 配置类
│       │   ├── DataInitializer.java
│       │   └── WebConfig.java
│       ├── controller/         # 控制器
│       │   ├── AuthController.java     # 认证
│       │   ├── UserController.java      # 用户+个人中心
│       │   ├── TeacherController.java   # 教师
│       │   ├── StudentController.java   # 学生
│       │   ├── ClassController.java     # 班级
│       │   ├── CourseController.java    # 课程
│       │   ├── FileController.java      # 文件上传
│       │   └── ExcelController.java     # Excel导入导出
│       ├── service/            # 服务层
│       │   ├── AuthService.java
│       │   ├── UserService.java
│       │   ├── TeacherService.java
│       │   ├── StudentService.java
│       │   ├── ClassService.java
│       │   └── CourseService.java
│       ├── dao/                # 数据访问层
│       │   ├── UserDao.java
│       │   ├── TeacherDao.java
│       │   ├── StudentDao.java
│       │   ├── ClassDao.java
│       │   └── CourseDao.java
│       ├── entity/             # 实体类
│       │   ├── User.java
│       │   ├── Teacher.java
│       │   ├── Student.java
│       │   ├── GradeClass.java
│       │   └── Course.java
│       ├── dto/                # 数据传输对象
│       │   ├── LoginRequest.java
│       │   ├── ApiResponse.java
│       │   ├── ImportResult.java
│       │   └── *.ExcelDTO.java
│       ├── excel/              # Excel 功能模块
│       │   ├── dto/            # Excel DTO
│       │   ├── listener/       # 读取监听器
│       │   └── service/        # Excel 服务
│       ├── interceptor/        # 拦截器
│       │   └── AuthInterceptor.java
│       └── util/               # 工具类
│           └── JwtUtil.java
├── frontend/                   # 前端项目
│   ├── package.json
│   ├── vite.config.ts
│   └── src/
│       ├── App.vue
│       ├── main.ts
│       ├── api/                # API 接口
│       │   ├── auth.ts
│       │   ├── user.ts
│       │   ├── teacher.ts
│       │   ├── student.ts
│       │   ├── class.ts
│       │   ├── course.ts
│       │   └── excel.ts
│       ├── components/          # 组件
│       │   └── MainLayout.vue
│       ├── router/             # 路由配置
│       │   └── index.ts
│       ├── stores/              # 状态管理
│       │   └── user.ts
│       ├── types/              # 类型定义
│       │   └── user.ts
│       ├── utils/               # 工具函数
│       │   ├── auth.ts
│       │   └── request.ts
│       └── views/               # 页面视图
│           ├── Login.vue
│           ├── Dashboard.vue
│           ├── UserManagement.vue
│           ├── TeacherManagement.vue
│           ├── StudentManagement.vue
│           ├── ClassManagement.vue
│           ├── CourseManagement.vue
│           └── Profile.vue
├── project.md                   # 项目规范文档
└── SPEC.md                     # 功能规格文档
```

## 功能模块

### 当前模块
- **用户管理**：用户列表、新增、编辑、删除、Excel导入导出
- **教师管理**：教师列表、新增、编辑、删除、Excel导入导出
- **学生管理**：学生列表、新增、编辑、删除、Excel导入导出
- **班级管理**：班级列表、新增、编辑、删除、Excel导入导出
- **课程管理**：课程列表、新增、编辑、删除、Excel导入导出
- **登录认证**：JWT Token 认证、路由守卫
- **个人中心**：基本信息、头像、资料编辑、密码修改
- **仪表盘**：数据统计、快速操作

### Excel 导入导出
支持以下模块的 Excel 导入/导出/模板下载：
- 用户 (User)
- 教师 (Teacher)
- 学生 (Student)
- 班级 (Class)
- 课程 (Course)

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

### 教师接口
| 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|
| GET | /api/teachers | 获取老师列表 | 是 |
| GET | /api/teachers/{id} | 获取单个老师 | 是 |
| POST | /api/teachers | 创建老师 | 是 |
| PUT | /api/teachers/{id} | 更新老师 | 是 |
| DELETE | /api/teachers/{id} | 删除老师 | 是 |

### 学生接口
| 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|
| GET | /api/students | 获取学生列表 | 是 |
| GET | /api/students/{id} | 获取单个学生 | 是 |
| POST | /api/students | 创建学生 | 是 |
| PUT | /api/students/{id} | 更新学生 | 是 |
| DELETE | /api/students/{id} | 删除学生 | 是 |

### 班级接口
| 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|
| GET | /api/classes | 获取班级列表 | 是 |
| GET | /api/classes/{id} | 获取单个班级 | 是 |
| POST | /api/classes | 创建班级 | 是 |
| PUT | /api/classes/{id} | 更新班级 | 是 |
| DELETE | /api/classes/{id} | 删除班级 | 是 |

### 课程接口
| 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|
| GET | /api/courses | 获取课程列表 | 是 |
| GET | /api/courses/{id} | 获取单个课程 | 是 |
| POST | /api/courses | 创建课程 | 是 |
| PUT | /api/courses/{id} | 更新课程 | 是 |
| DELETE | /api/courses/{id} | 删除课程 | 是 |

### Excel 接口
| 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|
| GET | /api/excel/export/{type} | 导出数据 | 是 |
| GET | /api/excel/template/{type} | 下载模板 | 是 |
| POST | /api/excel/import/{type} | 导入数据 | 是 |

### 文件上传接口
| 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|
| POST | /api/upload/image | 上传图片 | 是 |
