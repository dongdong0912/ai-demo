# 教学管理系统 (Teaching Management System)

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 2.7 + JPA + MySQL 8.x + EasyExcel 3.3.4 |
| 前端 | Vue 3 + TypeScript + Element Plus + Vite + Pinia + Vue Router |
| 数据库 | MySQL 8.x |
| 认证 | JWT（自定义拦截器） |
| Excel | Alibaba EasyExcel 3.3.4 |
| 连接池 | HikariCP |
| 构建 | Maven (后端) + npm (前端) |

---

## 功能模块

### 1. 用户管理
- [x] 用户列表展示
- [x] 用户新增
- [x] 用户编辑
- [x] 用户删除
- [x] Excel 导入/导出
- [x] Excel 模板下载

### 2. 教师管理
- [x] 教师列表展示
- [x] 教师新增
- [x] 教师编辑
- [x] 教师删除
- [x] Excel 导入/导出
- [x] Excel 模板下载

### 3. 学生管理
- [x] 学生列表展示
- [x] 学生新增
- [x] 学生编辑
- [x] 学生删除
- [x] Excel 导入/导出
- [x] Excel 模板下载

### 4. 班级管理
- [x] 班级列表展示
- [x] 班级新增
- [x] 班级编辑
- [x] 班级删除
- [x] Excel 导入/导出
- [x] Excel 模板下载

### 5. 课程管理
- [x] 课程列表展示
- [x] 课程新增
- [x] 课程编辑
- [x] 课程删除
- [x] Excel 导入/导出
- [x] Excel 模板下载

### 6. 成绩管理
- [x] 成绩列表展示
- [x] 成绩新增
- [x] 成绩编辑
- [x] 成绩删除
- [x] 按学生/课程筛选
- [x] Excel 导入/导出
- [x] Excel 模板下载

### 7. 登录认证
- [x] 用户名密码登录
- [x] JWT Token 认证
- [x] 路由守卫（未登录跳转登录页）
- [x] 退出登录

### 8. 个人中心
- [x] 基本信息展示
- [x] 头像上传/更换
- [x] 个人资料编辑（邮箱、手机号）
- [x] 密码修改
- [x] 登录统计（最后登录时间、登录次数）

### 10. 仪表盘
- [x] 数据统计展示
- [x] 快速操作入口

---

## 数据模型

### User（用户）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键，自增 |
| username | String | 用户名，唯一 |
| password | String | 密码，BCrypt加密 |
| realName | String | 真实姓名 |
| role | String | 角色：ADMIN/USER |
| email | String | 邮箱 |
| phone | String | 手机号 |
| department | String | 部门 |
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
| title | String | 职称 |
| idCard | String | 身份证号 |
| phone | String | 手机号 |
| email | String | 邮箱 |
| photo | String | 照片URL |
| status | Integer | 状态：1-启用，0-禁用 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

### Student（学生）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键，自增 |
| studentNo | String | 学号，唯一 |
| name | String | 姓名 |
| gender | String | 性别 |
| idCard | String | 身份证号 |
| classId | Long | 班级ID |
| phone | String | 手机号 |
| parentPhone | String | 家长电话 |
| parentName | String | 家长姓名 |
| nation | String | 民族 |
| nativePlace | String | 籍贯 |
| birthDate | LocalDate | 出生日期 |
| enrollmentDate | LocalDate | 入学日期 |
| status | Integer | 状态：1-启用，0-禁用 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

### GradeClass（班级）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键，自增 |
| className | String | 班级名称 |
| grade | String | 年级 |
| headTeacherId | Long | 班主任ID（教师） |
| maxStudents | Integer | 最大人数 |
| status | Integer | 状态：1-启用，0-禁用 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

### Course（课程）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键，自增 |
| courseName | String | 课程名称 |
| courseCode | String | 课程编码 |
| courseType | String | 类型：必修/选修 |
| credits | Double | 学分 |
| totalHours | Integer | 总课时 |
| category | String | 分类 |
| teacherId | Long | 授课教师ID |
| status | Integer | 状态：1-启用，0-禁用 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

### Score（成绩）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键，自增 |
| studentId | Long | 学生ID |
| courseId | Long | 课程ID |
| score | Double | 成绩分数 |
| examType | String | 考试类型 |
| examDate | LocalDate | 考试日期 |
| remark | String | 备注 |
| status | Integer | 状态：1-正常，0-作弊，2-缓考 |
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
| GET | /api/users/profile | 获取当前用户完整信息 | 是 |
| PUT | /api/users/profile | 更新个人资料 | 是 |
| PUT | /api/users/password | 修改密码 | 是 |

### 教师接口
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /api/teachers | 获取教师列表 | 是 |
| GET | /api/teachers/{id} | 获取教师详情 | 是 |
| POST | /api/teachers | 创建教师 | 是 |
| PUT | /api/teachers/{id} | 更新教师 | 是 |
| DELETE | /api/teachers/{id} | 删除教师 | 是 |

### 学生接口
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /api/students | 获取学生列表 | 是 |
| GET | /api/students/{id} | 获取学生详情 | 是 |
| POST | /api/students | 创建学生 | 是 |
| PUT | /api/students/{id} | 更新学生 | 是 |
| DELETE | /api/students/{id} | 删除学生 | 是 |

### 班级接口
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /api/classes | 获取班级列表 | 是 |
| GET | /api/classes/{id} | 获取班级详情 | 是 |
| POST | /api/classes | 创建班级 | 是 |
| PUT | /api/classes/{id} | 更新班级 | 是 |
| DELETE | /api/classes/{id} | 删除班级 | 是 |

### 课程接口
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /api/courses | 获取课程列表 | 是 |
| GET | /api/courses/{id} | 获取课程详情 | 是 |
| POST | /api/courses | 创建课程 | 是 |
| PUT | /api/courses/{id} | 更新课程 | 是 |
| DELETE | /api/courses/{id} | 删除课程 | 是 |

### 成绩接口
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /api/scores | 获取成绩列表（支持 keyword/courseId/studentId 筛选） | 是 |
| GET | /api/scores/{id} | 获取成绩详情 | 是 |
| GET | /api/scores/student/{studentId} | 获取学生成绩列表 | 是 |
| GET | /api/scores/course/{courseId} | 获取课程成绩列表 | 是 |
| POST | /api/scores | 创建成绩 | 是 |
| PUT | /api/scores/{id} | 更新成绩 | 是 |
| DELETE | /api/scores/{id} | 删除成绩 | 是 |

### Excel 导入导出接口
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /api/excel/export/{type} | 导出 Excel，type: user/teacher/student/class/course/score | 是 |
| GET | /api/excel/template/{type} | 下载导入模板，type: user/teacher/student/class/course/score | 是 |
| POST | /api/excel/import/{type} | 导入 Excel，type: user/teacher/student/class/course/score | 是 |

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

### 导入结果格式
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "successCount": 10,
    "errorCount": 2,
    "errors": [
      { "row": 1, "message": "用户名不能为空" },
      { "row": 3, "message": "邮箱格式错误" }
    ]
  }
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

## Excel 导入导出功能

### 支持的模块
- 用户 (User)
- 教师 (Teacher)
- 学生 (Student)
- 班级 (Class)
- 课程 (Course)

### 使用说明
1. **导出数据**：点击"导出"按钮，下载包含当前筛选条件下所有数据的 Excel 文件
2. **下载模板**：点击"导入"按钮后，可下载空白导入模板
3. **导入数据**：选择 Excel 文件后，系统会逐行校验并导入，显示成功/失败数量

### 导入校验规则
- 必填字段不能为空
- 格式校验（邮箱、手机号、身份证号等）
- 唯一性校验（用户名、学号、课程编码等）
- 外键关联校验（如班级ID、教师ID等）

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
│   ├── StudentController.java          # 学生控制器
│   ├── ClassController.java            # 班级控制器
│   ├── CourseController.java           # 课程控制器
│   ├── ScoreController.java            # 成绩控制器
│   ├── FileController.java             # 文件上传控制器
│   └── ExcelController.java            # Excel导入导出控制器
├── dao/
│   ├── UserDao.java
│   ├── TeacherDao.java
│   ├── StudentDao.java
│   ├── ClassDao.java
│   ├── CourseDao.java
│   └── ScoreDao.java
├── dto/
│   ├── LoginRequest.java              # 登录请求DTO
│   ├── ApiResponse.java               # 统一响应格式
│   ├── UserExcelDTO.java               # 用户Excel DTO
│   ├── TeacherExcelDTO.java            # 教师Excel DTO
│   ├── StudentExcelDTO.java            # 学生Excel DTO
│   ├── ClassExcelDTO.java             # 班级Excel DTO
│   ├── CourseExcelDTO.java             # 课程Excel DTO
│   └── ImportResult.java               # 导入结果DTO
├── entity/
│   ├── User.java                      # 用户实体
│   ├── Teacher.java                   # 教师实体
│   ├── Student.java                   # 学生实体
│   ├── GradeClass.java                # 班级实体
│   └── Course.java                    # 课程实体
├── excel/
│   ├── dto/                           # Excel 数据传输对象
│   │   ├── UserExcelDTO.java
│   │   ├── TeacherExcelDTO.java
│   │   ├── StudentExcelDTO.java
│   │   ├── ClassExcelDTO.java
│   │   ├── CourseExcelDTO.java
│   │   └── ScoreExcelDTO.java
│   ├── listener/                      # Excel 读取监听器
│   │   ├── UserImportListener.java
│   │   ├── TeacherImportListener.java
│   │   ├── StudentImportListener.java
│   │   ├── ClassImportListener.java
│   │   ├── CourseImportListener.java
│   │   └── ScoreImportListener.java
│   └── service/
│       └── ExcelService.java          # Excel 服务
├── interceptor/
│   └── AuthInterceptor.java           # 认证拦截器
├── service/
│   ├── AuthService.java               # 认证服务
│   ├── UserService.java
│   ├── TeacherService.java
│   ├── StudentService.java
│   ├── ClassService.java
│   ├── CourseService.java
│   └── ScoreService.java
├── entity/
│   ├── User.java
│   ├── Teacher.java
│   ├── Student.java
│   ├── GradeClass.java
│   ├── Course.java
│   └── Score.java
└── util/
    └── JwtUtil.java                   # JWT工具类
```

### 前端
```
frontend/src/
├── api/
│   ├── auth.ts                        # 认证API
│   ├── user.ts                        # 用户API
│   ├── teacher.ts                     # 教师API
│   ├── student.ts                     # 学生API
│   ├── class.ts                       # 班级API
│   ├── course.ts                      # 课程API
│   └── excel.ts                       # Excel导入导出API
├── assets/
│   └── logo.png
├── components/
│   └── MainLayout.vue                 # 主布局组件
├── router/
│   └── index.ts                       # 路由配置
├── stores/
│   └── user.ts                        # 用户状态管理
├── types/
│   └── user.ts                        # 类型定义
├── utils/
│   ├── auth.ts                        # Token管理工具
│   └── request.ts                     # axios实例配置
├── views/
│   ├── Login.vue                      # 登录页面
│   ├── Dashboard.vue                  # 仪表盘
│   ├── UserManagement.vue             # 用户管理页面
│   ├── TeacherManagement.vue          # 教师管理页面
│   ├── StudentManagement.vue          # 学生管理页面
│   ├── ClassManagement.vue            # 班级管理页面
│   ├── CourseManagement.vue           # 课程管理页面
│   ├── ScoreManagement.vue           # 成绩管理页面
│   └── Profile.vue                    # 个人中心页面
├── App.vue                            # 根组件
├── main.ts                            # 应用入口
└── index.css                          # 全局样式
```

---

## 页面路由

| 路径 | 页面 | 说明 |
|------|------|------|
| /login | Login | 登录页 |
| / | Dashboard | 仪表盘 |
| /users | UserManagement | 用户管理 |
| /teachers | TeacherManagement | 教师管理 |
| /students | StudentManagement | 学生管理 |
| /classes | ClassManagement | 班级管理 |
| /courses | CourseManagement | 课程管理 |
| /scores | ScoreManagement | 成绩管理 |
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

### 2026-04-24
- 新增 EasyExcel 导入导出功能
- 新增用户、教师、学生、班级、课程模块的 Excel 导入/导出/模板下载
- 新增 `ExcelController.java`、`ExcelService.java`
- 新增 Excel DTO 和 ImportListener 类

### 2026-04-20
- 新增个人中心功能
- 新增 User 实体字段：avatar, lastLoginTime, loginCount
- 新增 API：/api/users/profile, /api/users/password
- 新增前端页面：Profile.vue
- 登录时自动更新 lastLoginTime 和 loginCount
