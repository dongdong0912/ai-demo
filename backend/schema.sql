-- =============================================
-- 教学管理系统数据库初始化脚本
-- 数据库: MySQL 8.x
-- 创建时间: 2026-04-17
-- 描述: 用户管理和老师管理模块的建表语句
-- =============================================

-- 如果数据库不存在则创建
CREATE DATABASE IF NOT EXISTS teaching_management
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

-- 使用数据库
USE teaching_management;

-- =============================================
-- 表1: 用户表 (t_user)
-- =============================================
DROP TABLE IF EXISTS t_user;

CREATE TABLE t_user (
    -- 主键ID，自增
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键自增',

    -- 用户名，唯一且不能为空
    username VARCHAR(50) NOT NULL COMMENT '用户名，唯一约束',

    -- 邮箱
    email VARCHAR(100) DEFAULT NULL COMMENT '用户邮箱',

    -- 手机号
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号码',

    -- 状态: 1-正常, 0-禁用
    status INT NOT NULL DEFAULT 1 COMMENT '用户状态: 1-正常, 0-禁用',

    -- 创建时间
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    -- 更新时间
    update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- 主键约束
    PRIMARY KEY (id),

    -- 用户名字段唯一索引
    UNIQUE INDEX idx_username (username),

    -- 状态字段索引（用于筛选）
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- =============================================
-- 表2: 老师表 (t_teacher)
-- =============================================
DROP TABLE IF EXISTS t_teacher;

CREATE TABLE t_teacher (
    -- 主键ID，自增
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '老师ID，主键自增',

    -- 姓名，不能为空
    name VARCHAR(50) NOT NULL COMMENT '老师姓名',

    -- 性别
    gender VARCHAR(20) DEFAULT NULL COMMENT '性别: 男/女',

    -- 科目/教授科目
    subject VARCHAR(50) DEFAULT NULL COMMENT '教授科目',

    -- 手机号
    phone VARCHAR(30) DEFAULT NULL COMMENT '联系电话',

    -- 邮箱
    email VARCHAR(100) DEFAULT NULL COMMENT '电子邮箱',

    -- 备注
    remark VARCHAR(200) DEFAULT NULL COMMENT '备注信息',

    -- 照片URL
    photo VARCHAR(500) DEFAULT NULL COMMENT '老师照片URL',

    -- 状态: 1-正常, 0-禁用
    status INT NOT NULL DEFAULT 1 COMMENT '状态: 1-在职, 0-离职',

    -- 创建时间
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    -- 更新时间
    update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- 主键约束
    PRIMARY KEY (id),

    -- 姓名索引（用于搜索）
    INDEX idx_name (name),

    -- 科目索引（用于筛选）
    INDEX idx_subject (subject),

    -- 状态索引（用于筛选）
    INDEX idx_status (status),

    -- 联合索引：状态+科目（常用查询优化）
    INDEX idx_status_subject (status, subject)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='老师表';


-- =============================================
-- 测试数据: 用户表
-- =============================================
INSERT INTO t_user (username, email, phone, status, create_time) VALUES
('admin', 'admin@example.com', '13800000000', 1, NOW()),
('testuser', 'test@example.com', '13800000001', 1, NOW());


-- =============================================
-- 测试数据: 老师表 (100条测试数据)
-- =============================================
INSERT INTO t_teacher (name, gender, subject, phone, email, remark, status, create_time) VALUES
('张伟', '男', '数学', '13900000001', 'zhangwei@example.com', '特级教师，10年教龄', 1, NOW()),
('李娜', '女', '语文', '13900000002', 'lina@example.com', '骨干教师，擅长写作教学', 1, NOW()),
('王强', '男', '英语', '13900000003', 'wangqiang@example.com', '海归硕士，发音标准', 1, NOW()),
('刘芳', '女', '数学', '13900000004', 'liufang@example.com', '备课认真，深受学生喜爱', 1, NOW()),
('陈明', '男', '物理', '13900000005', 'chenming@example.com', '实验教学专家', 1, NOW()),
('赵雪', '女', '化学', '13900000006', 'zhaoxue@example.com', '耐心细致，擅长答疑', 1, NOW()),
('孙浩', '男', '历史', '13900000007', 'sunhao@example.com', '知识渊博，课堂生动', 1, NOW()),
('周琳', '女', '地理', '13900000008', 'zhoulin@example.com', '带学生走遍中国', 1, NOW()),
('吴涛', '男', '生物', '13900000009', 'wutao@example.com', '注重实践教学', 1, NOW()),
('郑静', '女', '政治', '13900000010', 'zhengjing@example.com', '理论联系实际', 1, NOW()),
('王磊', '男', '数学', '13900000011', 'wanglei@example.com', '竞赛辅导经验', 1, NOW()),
('李娟', '女', '语文', '13900000012', 'lijuan@example.com', '文学功底深厚', 1, NOW()),
('张鹏', '男', '英语', '13900000013', 'zhangpeng@example.com', '雅思托福专家', 1, NOW()),
('刘洋', '女', '物理', '13900000014', 'liuyang@example.com', '高考物理满分导师', 1, NOW()),
('陈晨', '男', '化学', '13900000015', 'chenchen@example.com', '竞赛金牌教练', 1, NOW()),
('杨丽', '女', '数学', '13900000016', 'yangli@example.com', '教学成果显著', 1, NOW()),
('赵军', '男', '语文', '13900000017', 'zhaojun@example.com', '古文研究专家', 1, NOW()),
('孙颖', '女', '英语', '13900000018', 'sunying@example.com', '口语教学专家', 1, NOW()),
('周杰', '男', '物理', '13900000019', 'zhoujie@example.com', '实验室管理员', 1, NOW()),
('吴霞', '女', '化学', '13900000020', 'wuxia@example.com', '实验操作规范', 1, NOW()),
('郑强', '男', '历史', '13900000021', 'zhengqiang@example.com', '历史故事讲述者', 1, NOW()),
('王芳', '女', '地理', '13900000022', 'wangfang@example.com', '地图绘制专家', 1, NOW()),
('李勇', '男', '生物', '13900000023', 'liyong@example.com', '生态学研究', 1, NOW()),
('张敏', '女', '政治', '13900000024', 'zhangmin@example.com', '法律专家', 1, NOW()),
('刘刚', '男', '数学', '13900000025', 'liugang@example.com', '奥数教练', 1, NOW()),
('陈娟', '女', '语文', '13900000026', 'chenjuan@example.com', '阅读理解专家', 1, NOW()),
('赵磊', '男', '英语', '13900000027', 'zhaolei@example.com', '外教合作教师', 1, NOW()),
('孙燕', '女', '物理', '13900000028', 'sunyan@example.com', '电学专家', 1, NOW()),
('周伟', '男', '化学', '13900000029', 'zhouwei@example.com', '有机化学专家', 1, NOW()),
('吴娟', '女', '数学', '13900000030', 'wujuan@example.com', '几何教学专家', 1, NOW()),
('郑浩', '男', '语文', '13900000031', 'zhenghao@example.com', '作文指导专家', 1, NOW()),
('王丽', '女', '英语', '13900000032', 'wangli@example.com', '阅读理解教师', 1, NOW()),
('李强', '男', '物理', '13900000033', 'liqiang@example.com', '力学专家', 1, NOW()),
('张红', '女', '化学', '13900000034', 'zhanghong@example.com', '无机化学教师', 1, NOW()),
('刘峰', '男', '历史', '13900000035', 'liufeng@example.com', '中国古代史专家', 1, NOW()),
('陈霞', '女', '地理', '13900000036', 'chenxia@example.com', '气候研究专家', 1, NOW()),
('赵鹏', '男', '生物', '13900000037', 'zhaopeng@example.com', '遗传学专家', 1, NOW()),
('孙丽', '女', '政治', '13900000038', 'sunli@example.com', '经济常识教师', 1, NOW()),
('周军', '男', '数学', '13900000039', 'zhoujun@example.com', '代数教学专家', 1, NOW()),
('吴芳', '女', '语文', '13900000040', 'wufang@example.com', '文言文专家', 1, NOW()),
('郑磊', '男', '英语', '13900000041', 'zhenglei@example.com', '写作教学专家', 1, NOW()),
('王颖', '女', '物理', '13900000042', 'wangying@example.com', '光学实验专家', 1, NOW()),
('李峰', '男', '化学', '13900000043', 'lifeng@example.com', '分析化学专家', 1, NOW()),
('张霞', '女', '数学', '13900000044', 'zhangxia@example.com', '概率统计教师', 1, NOW()),
('刘杰', '男', '语文', '13900000045', 'liujie@example.com', '现代文学教师', 1, NOW()),
('陈丽', '女', '英语', '13900000046', 'chenli@example.com', '语法教学专家', 1, NOW()),
('赵强', '男', '物理', '13900000047', 'zhaoqiang@example.com', '电磁学专家', 1, NOW()),
('孙峰', '女', '化学', '13900000048', 'sunfeng@example.com', '化学实验教师', 1, NOW()),
('周娟', '男', '历史', '13900000049', 'zhoujuan@example.com', '世界历史专家', 1, NOW()),
('吴伟', '女', '地理', '13900000050', 'wuwei@example.com', '地质学专家', 1, NOW()),
('郑燕', '男', '生物', '13900000051', 'zhengyan@example.com', '植物学专家', 1, NOW()),
('王军', '女', '政治', '13900000052', 'wangjun@example.com', '哲学教师', 1, NOW()),
('李霞', '男', '数学', '13900000053', 'lixia@example.com', '数学竞赛教练', 1, NOW()),
('张磊', '女', '语文', '13900000054', 'zhanglei@example.com', '诗词鉴赏专家', 1, NOW()),
('刘燕', '男', '英语', '13900000055', 'liuyan@example.com', '翻译教学专家', 1, NOW()),
('陈伟', '女', '物理', '13900000056', 'chenwei@example.com', '物理竞赛教练', 1, NOW()),
('赵燕', '男', '化学', '13900000057', 'zhaoyan@example.com', '化学竞赛教练', 1, NOW()),
('孙磊', '女', '数学', '13900000058', 'sunlei@example.com', '高考数学专家', 1, NOW()),
('周霞', '男', '语文', '13900000059', 'zhouxia@example.com', '语文教研组长', 1, NOW()),
('吴强', '女', '英语', '13900000060', 'wuqiang@example.com', '英语教研组长', 1, NOW()),
('郑峰', '男', '物理', '13900000061', 'zhengfeng@example.com', '物理教研组长', 1, NOW()),
('王娟', '女', '化学', '13900000062', 'wangjuan@example.com', '化学教研组长', 1, NOW()),
('李军', '男', '历史', '13900000063', 'lijun@example.com', '历史教研组长', 1, NOW()),
('张燕', '女', '地理', '13900000064', 'zhangyan@example.com', '地理教研组长', 1, NOW()),
('刘霞', '男', '生物', '13900000065', 'liuxia@example.com', '生物教研组长', 1, NOW()),
('陈峰', '女', '政治', '13900000066', 'chenfeng@example.com', '政治教研组长', 1, NOW()),
('赵军2', '男', '数学', '13900000067', 'zhaojun2@example.com', '特级数学教师', 1, NOW()),
('孙燕2', '女', '语文', '13900000068', 'sunyan2@example.com', '特级语文教师', 1, NOW()),
('周磊', '男', '英语', '13900000069', 'zhoulei@example.com', '特级英语教师', 1, NOW()),
('吴燕', '女', '物理', '13900000070', 'wuyan@example.com', '特级物理教师', 1, NOW()),
('郑军', '男', '化学', '13900000071', 'zhengjun@example.com', '特级化学教师', 1, NOW()),
('王峰', '女', '数学', '13900000072', 'wangfeng@example.com', '高级教师', 1, NOW()),
('李燕', '男', '语文', '13900000073', 'liyan@example.com', '高级教师', 1, NOW()),
('张军', '女', '英语', '13900000074', 'zhangjun@example.com', '高级教师', 1, NOW()),
('刘峰2', '男', '物理', '13900000075', 'liufeng2@example.com', '高级教师', 1, NOW()),
('陈军', '女', '化学', '13900000076', 'chenjun@example.com', '高级教师', 1, NOW()),
('赵霞', '男', '历史', '13900000077', 'zhaoxia@example.com', '高级教师', 1, NOW()),
('孙军', '女', '地理', '13900000078', 'sunjun@example.com', '高级教师', 1, NOW()),
('周峰', '男', '生物', '13900000079', 'zhoufeng@example.com', '高级教师', 1, NOW()),
('吴军', '女', '政治', '13900000080', 'wujun@example.com', '高级教师', 1, NOW()),
('郑霞', '男', '数学', '13900000081', 'zhengxia@example.com', '一级教师', 1, NOW()),
('王军2', '女', '语文', '13900000082', 'wangjun2@example.com', '一级教师', 1, NOW()),
('李峰2', '男', '英语', '13900000083', 'lifeng2@example.com', '一级教师', 1, NOW()),
('张军2', '女', '物理', '13900000084', 'zhangjun2@example.com', '一级教师', 1, NOW()),
('刘军', '男', '化学', '13900000085', 'liujun@example.com', '一级教师', 1, NOW()),
('陈霞2', '女', '历史', '13900000086', 'chenxia2@example.com', '一级教师', 1, NOW()),
('赵峰', '男', '地理', '13900000087', 'zhaofeng@example.com', '一级教师', 1, NOW()),
('孙霞', '女', '生物', '13900000088', 'sunxia@example.com', '一级教师', 1, NOW()),
('周军2', '男', '政治', '13900000089', 'zhoujun2@example.com', '一级教师', 1, NOW()),
('吴峰', '女', '数学', '13900000090', 'wufeng@example.com', '二级教师', 1, NOW()),
('郑军2', '男', '语文', '13900000091', 'zhengjun2@example.com', '二级教师', 1, NOW()),
('王霞', '女', '英语', '13900000092', 'wangxia@example.com', '二级教师', 1, NOW()),
('李军2', '男', '物理', '13900000093', 'lijun2@example.com', '二级教师', 1, NOW()),
('张霞2', '女', '化学', '13900000094', 'zhangxia2@example.com', '二级教师', 1, NOW()),
('刘军2', '男', '历史', '13900000095', 'liujun2@example.com', '二级教师', 1, NOW()),
('陈峰2', '女', '地理', '13900000096', 'chenfeng2@example.com', '二级教师', 1, NOW()),
('赵军3', '男', '生物', '13900000097', 'zhaojun3@example.com', '二级教师', 1, NOW()),
('孙峰2', '女', '政治', '13900000098', 'sunfeng2@example.com', '二级教师', 1, NOW()),
('周军3', '男', '数学', '13900000099', 'zhoujun3@example.com', '三级教师', 1, NOW()),
('吴霞2', '女', '语文', '13900000100', 'wuxia2@example.com', '三级教师', 1, NOW());


-- =============================================
-- 验证数据
-- =============================================
SELECT '用户表数据验证' AS info, COUNT(*) AS count FROM t_user;
SELECT '老师表数据验证' AS info, COUNT(*) AS count FROM t_teacher;


-- =============================================
-- 常用查询示例
-- =============================================

-- 1. 查询所有在职老师（分页）
-- SELECT * FROM t_teacher WHERE status = 1 LIMIT 10 OFFSET 0;

-- 2. 按科目统计老师数量
-- SELECT subject, COUNT(*) as count FROM t_teacher WHERE status = 1 GROUP BY subject;

-- 3. 搜索老师（按姓名模糊查询）
-- SELECT * FROM t_teacher WHERE name LIKE '%张%' AND status = 1;

-- 4. 查询状态正常的用户
-- SELECT * FROM t_user WHERE status = 1;
