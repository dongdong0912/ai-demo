# 用户管理系统

## 功能需求
- 用户列表展示
- 用户新增
- 用户编辑
- 用户删除

## 数据模型
### User
- id: Long (主键)
- username: String (用户名，唯一)
- email: String (邮箱)
- phone: String (手机号)
- status: Integer (状态: 1-启用, 0-禁用)
- createTime: LocalDateTime (创建时间)
- updateTime: LocalDateTime (更新时间)
