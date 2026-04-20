# 教学管理系统 (Teaching Management System)

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 2.7 + JPA + MySQL 8.x |
| 前端 | React + Ant Design + React Router + Vite |
| 数据库 | MySQL 8.x |
| 认证 | JWT（自定义拦截器） |
| 连接池 | HikariCP |
| 构建 | Maven (后端) + npm (前端) |

---

## 功能模块

### 1. 用户管理
- [x] 用户列表展示
- [x] 用户新增
- [x] 用户编辑
- [x] 用户删除

### 2. 教师管理
- [x] 教师列表展示
- [x] 教师新增
- [x] 教师编辑
- [x] 教师删除

### 3. 登录认证
- [x] 用户名密码登录
- [x] JWT Token 认证
- [x] 路由守卫（未登录跳转登录页）
- [x] 退出登录

### 4. 个人中心
- [x] 基本信息展示
- [x] 头像上传/更换
- [x] 个人资料编辑（邮箱、手机号）
- [x] 密码修改
- [x] 登录统计（最后登录时间、登录次数）

---

## 数据模型

### User（用户）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键，自增 |
| username | String | 用户名，唯一 |
| password | String | 密码，BCrypt加密 |
| role | String | 角色：ADMIN/USER |
| email | String | 邮箱 |
| phone | String | 手机号 |
| avatar | String | 头像URL（Base64） |
| status | Integer | 状态：1-启用，0-禁用 |
| lastLoginTime | LocalDateTime | 最后登录时间 |
| loginCount | Integer | 登录次数 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

### Teacher（教师）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键，自增 |
| name | String | 姓名 |
| gender | String | 性别 |
| subject | String | 科目 |
| phone | String | 电话 |
| email | String | 邮箱 |
| photo | String | 照片URL |
| remark | String | 备注 |
| status | Integer | 状态：1-启用，0-禁用 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

---

## API 设计

### 认证接口
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /api/auth/login | 用户登录 | 否 |
| POST | /api/auth/register | 用户注册 | 否 |
| GET | /api/auth/userinfo | 获取当前用户信息 | 是 |

### 用户接口
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /api/users | 获取用户列表 | 是 |
| GET | /api/users/{id} | 获取用户详情 | 是 |
| POST | /api/users | 创建用户 | 是 |
| PUT | /api/users/{id} | 更新用户 | 是 |
| DELETE | /api/users/{id} | 删除用户 | 是 |
| **GET** | **/api/users/profile** | **获取当前用户完整信息** | **是** |
| **PUT** | **/api/users/profile** | **更新个人资料** | **是** |
| **PUT** | **/api/users/password** | **修改密码** | **是** |

### 教师接口
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /api/teachers | 获取教师列表 | 是 |
| GET | /api/teachers/{id} | 获取教师详情 | 是 |
| POST | /api/teachers | 创建教师 | 是 |
| PUT | /api/teachers/{id} | 更新教师 | 是 |
| DELETE | /api/teachers/{id} | 删除教师 | 是 |

### 文件上传接口
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /api/upload/image | 上传图片 | 是 |

**上传说明**：
- 支持格式：JPG, PNG, GIF, WEBP
- 文件大小限制：5MB
- 返回文件访问 URL

### API 响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

---

## 认证流程

```
┌─────────────────────────────────────────────────────────────┐
│                         认证流程                             │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. 访问系统 → 检查 localStorage 是否有 Token                │
│         ↓                                                   │
│  2. 无 Token → 跳转登录页 → 输入用户名密码                    │
│         ↓                                                   │
│  3. 登录成功 → 后端返回 JWT Token → 前端存储到 localStorage   │
│         ↓                                                   │
│  4. 后续请求 → axios 拦截器自动添加 Authorization Header     │
│         ↓                                                   │
│  5. 后端拦截器验证 Token → 有效放行，无效返回 401              │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | ADMIN (管理员) |
| teacher | 123456 | USER (教师) |

---

## 文件结构

### 后端
```
backend/src/main/java/com/example/usermanagement/
├── UserManagementApplication.java    # 应用入口
├── config/
│   ├── DataInitializer.java           # 初始数据初始化
│   └── WebConfig.java                 # Web配置(CORS/拦截器)
├── controller/
│   ├── AuthController.java            # 认证控制器
│   ├── UserController.java            # 用户控制器(含个人中心)
│   ├── TeacherController.java          # 教师控制器
│   └── FileController.java            # 文件上传控制器
├── dao/
│   ├── UserDao.java
│   └── TeacherDao.java
├── dto/
│   ├── LoginRequest.java              # 登录请求DTO
│   └── ApiResponse.java               # 统一响应格式
├── entity/
│   ├── User.java                      # 用户实体
│   └── Teacher.java                   # 教师实体
├── interceptor/
│   └── AuthInterceptor.java           # 认证拦截器
├── service/
│   ├── AuthService.java               # 认证服务
│   ├── UserService.java
│   └── TeacherService.java
└── util/
    └── JwtUtil.java                   # JWT工具类
```

### 前端
```
frontend/src/
├── api/
│   ├── auth.ts                        # 认证API
│   ├── user.ts                        # 用户API(含个人中心)
│   └── teacher.ts                     # 教师API
├── components/
│   ├── AuthGuard.tsx                  # 路由守卫
│   ├── MainLayout.tsx                 # 主布局
│   └── SideMenu.tsx                   # 侧边栏菜单
├── pages/
│   ├── Login.tsx                      # 登录页面
│   ├── Login.css
│   ├── UserManagement.tsx             # 用户管理页面
│   ├── TeacherManagement.tsx          # 教师管理页面
│   ├── Profile.tsx                   # 个人中心页面
│   └── Profile.css                   # 个人中心样式
├── types/
│   └── user.ts                        # 用户类型定义
├── utils/
│   ├── auth.ts                        # Token管理工具
│   └── request.ts                     # axios实例配置
├── App.tsx                            # 路由配置
├── main.tsx                           # 应用入口
└── index.css                          # 全局样式
```

---

## 页面路由

| 路径 | 页面 | 说明 |
|------|------|------|
| /login | Login | 登录页 |
| /users | UserManagement | 用户管理 |
| /teachers | TeacherManagement | 教师管理 |
| /profile | Profile | 个人中心 |

---

## 启动说明

### 后端启动
```bash
cd backend
mvn spring-boot:run
# 访问 http://localhost:8080
```

### 前端启动
```bash
cd frontend
npm install
npm run dev
# 访问 http://localhost:5173
```

---

## 更新日志

### 2026-04-20
- 新增个人中心功能
- 新增 User 实体字段：avatar, lastLoginTime, loginCount
- 新增 API：/api/users/profile, /api/users/password
- 新增前端页面：Profile.tsx
- 登录时自动更新 lastLoginTime 和 loginCount
