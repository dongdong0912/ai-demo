# 教务管理系统

## 技术栈
- 后端：Spring Boot 2.7 + JPA + MySQL
- 前端：React + Ant Design + React Router
- 认证：JWT（自定义拦截器，无 Spring Security）

## 功能需求

### 用户管理
- 用户列表展示
- 用户新增
- 用户编辑
- 用户删除

### 登录认证
- 用户名密码登录
- JWT Token 认证
- 路由守卫（未登录跳转登录页）
- 退出登录

## 数据模型

### User
- id: Long (主键)
- username: String (用户名，唯一)
- password: String (密码，BCrypt加密)
- role: String (角色: ADMIN/USER)
- email: String (邮箱)
- phone: String (手机号)
- status: Integer (状态: 1-启用, 0-禁用)
- createTime: LocalDateTime (创建时间)
- updateTime: LocalDateTime (更新时间)

### Teacher
- id: Long (主键)
- name: String (姓名)
- gender: String (性别)
- subject: String (科目)
- phone: String (电话)
- email: String (邮箱)
- photo: String (照片URL)
- remark: String (备注)
- status: Integer (状态: 1-启用, 0-禁用)
- createTime: LocalDateTime (创建时间)
- updateTime: LocalDateTime (更新时间)

## API 设计

### 认证接口
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /api/auth/login | 用户登录 | 否 |
| POST | /api/auth/register | 用户注册 | 否 |
| GET | /api/auth/userinfo | 获取用户信息 | 是 |

### 用户接口
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /api/users | 获取用户列表 | 是 |
| GET | /api/users/{id} | 获取用户详情 | 是 |
| POST | /api/users | 创建用户 | 是 |
| PUT | /api/users/{id} | 更新用户 | 是 |
| DELETE | /api/users/{id} | 删除用户 | 是 |

### 教师接口
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /api/teachers | 获取教师列表 | 是 |
| GET | /api/teachers/{id} | 获取教师详情 | 是 |
| POST | /api/teachers | 创建教师 | 是 |
| PUT | /api/teachers/{id} | 更新教师 | 是 |
| DELETE | /api/teachers/{id} | 删除教师 | 是 |

## 认证流程

1. 用户访问页面 → 检查 localStorage 是否有 Token
2. 无 Token → 跳转登录页 → 输入用户名密码
3. 登录成功 → 后端返回 JWT Token → 前端存储到 localStorage
4. 后续请求 → axios 拦截器自动添加 Authorization Header
5. 后端拦截器验证 Token → 有效则放行，无效则返回 401

## 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | ADMIN (管理员) |
| teacher | 123456 | USER (教师) |
| student | 123456 | USER (学生) |

## 文件结构

### 后端
```
backend/src/main/java/com/example/usermanagement/
├── config/
│   ├── DataInitializer.java    # 数据初始化
│   └── WebConfig.java         # Web配置(拦截器/CORS)
├── controller/
│   ├── AuthController.java    # 认证控制器
│   ├── UserController.java    # 用户控制器
│   └── TeacherController.java # 教师控制器
├── dao/
│   ├── UserDao.java
│   └── TeacherDao.java
├── dto/
│   ├── LoginRequest.java      # 登录请求DTO
│   └── ApiResponse.java       # 统一响应格式
├── entity/
│   ├── User.java
│   └── Teacher.java
├── interceptor/
│   └── AuthInterceptor.java   # 认证拦截器
├── service/
│   ├── AuthService.java       # 认证服务
│   ├── UserService.java
│   └── TeacherService.java
└── util/
    └── JwtUtil.java           # JWT工具类
```

### 前端
```
frontend/src/
├── api/
│   ├── auth.ts                # 认证API
│   ├── user.ts
│   └── teacher.ts
├── components/
│   ├── AuthGuard.tsx          # 路由守卫
│   ├── MainLayout.tsx
│   └── SideMenu.tsx
├── pages/
│   ├── Login.tsx              # 登录页面
│   ├── UserManagement.tsx
│   └── TeacherManagement.tsx
├── utils/
│   ├── auth.ts                # Token管理
│   └── request.ts             # axios实例
├── App.tsx
└── main.tsx
```
