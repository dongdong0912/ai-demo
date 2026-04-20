import { useState, useEffect } from 'react';
import {
  Card,
  Avatar,
  Descriptions,
  Button,
  Modal,
  Form,
  Input,
  message,
  Upload,
  Space,
  Row,
  Col,
} from 'antd';
import './Profile.css';
import {
  UserOutlined,
  EditOutlined,
  LockOutlined,
  MailOutlined,
  PhoneOutlined,
  UploadOutlined,
  CalendarOutlined,
  LoginOutlined,
} from '@ant-design/icons';
import { User } from '../types/user';
import { getProfile, updateProfile, changePassword } from '../api/user';

const Profile: React.FC = () => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(false);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [passwordModalVisible, setPasswordModalVisible] = useState(false);
  const [editForm] = Form.useForm();
  const [passwordForm] = Form.useForm();

  const loadProfile = async () => {
    setLoading(true);
    try {
      const data = await getProfile();
      setUser(data);
    } catch (error: unknown) {
      const err = error as Error;
      message.error(err.message || '加载个人资料失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProfile();
  }, []);

  const handleEditProfile = () => {
    editForm.setFieldsValue({
      email: user?.email,
      phone: user?.phone,
    });
    setEditModalVisible(true);
  };

  const handleEditSubmit = async () => {
    try {
      const values = await editForm.validateFields();
      await updateProfile(values);
      message.success('个人资料更新成功');
      setEditModalVisible(false);
      loadProfile();
    } catch (error: unknown) {
      const err = error as { message?: string };
      message.error(err.message || '更新失败');
    }
  };

  const handlePasswordSubmit = async () => {
    try {
      const values = await passwordForm.validateFields();
      if (values.newPassword !== values.confirmPassword) {
        message.error('两次输入的密码不一致');
        return;
      }
      await changePassword(values.oldPassword, values.newPassword);
      message.success('密码修改成功');
      setPasswordModalVisible(false);
      passwordForm.resetFields();
    } catch (error: unknown) {
      const err = error as { message?: string };
      message.error(err.message || '修改失败');
    }
  };

  const handleAvatarChange = async (file: File) => {
    // 将文件转换为 Base64
    const reader = new FileReader();
    reader.onload = async (e) => {
      const base64 = e.target?.result as string;
      try {
        await updateProfile({ avatar: base64 });
        message.success('头像上传成功');
        loadProfile();
      } catch (error: unknown) {
        const err = error as { message?: string };
        message.error(err.message || '头像上传失败');
      }
    };
    reader.readAsDataURL(file);
    return false; // 阻止默认上传
  };

  const formatDate = (dateStr?: string) => {
    if (!dateStr) return '-';
    const date = new Date(dateStr);
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const getRoleName = (role?: string) => {
    const roleMap: Record<string, string> = {
      ADMIN: '系统管理员',
      USER: '普通用户',
      TEACHER: '教师',
    };
    return roleMap[role || ''] || role || '-';
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>
          <UserOutlined className="icon" />
          个人中心
        </h2>
      </div>

      <Row gutter={24}>
        {/* 左侧个人信息卡片 */}
        <Col xs={24} lg={8}>
          <Card
            className="profile-card profile-avatar-card"
            bordered={false}
          >
            <div className="profile-avatar-section">
              <Upload
                showUploadList={false}
                beforeUpload={handleAvatarChange}
                accept="image/*"
              >
                <div className="avatar-wrapper">
                  <Avatar
                    size={120}
                    src={user?.avatar}
                    icon={<UserOutlined />}
                    className="profile-avatar"
                  />
                  <div className="avatar-overlay">
                    <UploadOutlined />
                    <span>更换头像</span>
                  </div>
                </div>
              </Upload>
              <h3 className="profile-username">{user?.username || '-'}</h3>
              <p className="profile-role">{getRoleName(user?.role)}</p>
            </div>
          </Card>

          <Card
            className="profile-card profile-actions-card"
            bordered={false}
          >
            <Space direction="vertical" style={{ width: '100%' }} size="middle">
              <Button
                block
                icon={<EditOutlined />}
                onClick={handleEditProfile}
                className="action-btn"
              >
                编辑资料
              </Button>
              <Button
                block
                icon={<LockOutlined />}
                onClick={() => setPasswordModalVisible(true)}
                className="action-btn"
              >
                修改密码
              </Button>
            </Space>
          </Card>
        </Col>

        {/* 右侧详情卡片 */}
        <Col xs={24} lg={16}>
          <Card
            title={
              <span className="card-title">
                <UserOutlined /> 基本信息
              </span>
            }
            className="profile-card"
            bordered={false}
          >
            <Descriptions column={{ xs: 1, sm: 2 }} labelStyle={{ fontWeight: 500 }}>
              <Descriptions.Item label="用户名">{user?.username || '-'}</Descriptions.Item>
              <Descriptions.Item label="角色">{getRoleName(user?.role)}</Descriptions.Item>
              <Descriptions.Item label="注册时间">
                {user?.createTime ? user.createTime.split('T')[0] : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="账号状态">
                <span className={user?.status === 1 ? 'status-active' : 'status-inactive'}>
                  {user?.status === 1 ? '正常' : '已禁用'}
                </span>
              </Descriptions.Item>
            </Descriptions>
          </Card>

          <Card
            title={
              <span className="card-title">
                <MailOutlined /> 联系方式
              </span>
            }
            className="profile-card"
            bordered={false}
            style={{ marginTop: 16 }}
          >
            <Descriptions column={{ xs: 1, sm: 2 }} labelStyle={{ fontWeight: 500 }}>
              <Descriptions.Item label="邮箱">
                {user?.email || <span className="text-muted">未设置</span>}
              </Descriptions.Item>
              <Descriptions.Item label="手机号">
                {user?.phone || <span className="text-muted">未设置</span>}
              </Descriptions.Item>
            </Descriptions>
          </Card>

          <Card
            title={
              <span className="card-title">
                <CalendarOutlined /> 登录统计
              </span>
            }
            className="profile-card"
            bordered={false}
            style={{ marginTop: 16 }}
          >
            <Descriptions column={{ xs: 1, sm: 2 }} labelStyle={{ fontWeight: 500 }}>
              <Descriptions.Item label="最后登录时间">
                {formatDate(user?.lastLoginTime)}
              </Descriptions.Item>
              <Descriptions.Item label="登录次数">
                {user?.loginCount || 0} 次
              </Descriptions.Item>
            </Descriptions>
          </Card>
        </Col>
      </Row>

      {/* 编辑资料弹窗 */}
      <Modal
        title="编辑个人资料"
        open={editModalVisible}
        onOk={handleEditSubmit}
        onCancel={() => setEditModalVisible(false)}
        okText="保存"
        cancelText="取消"
      >
        <Form form={editForm} layout="vertical">
          <Form.Item name="email" label="邮箱">
            <Input prefix={<MailOutlined />} placeholder="请输入邮箱" />
          </Form.Item>
          <Form.Item name="phone" label="手机号">
            <Input prefix={<PhoneOutlined />} placeholder="请输入手机号" />
          </Form.Item>
        </Form>
      </Modal>

      {/* 修改密码弹窗 */}
      <Modal
        title="修改密码"
        open={passwordModalVisible}
        onOk={handlePasswordSubmit}
        onCancel={() => {
          setPasswordModalVisible(false);
          passwordForm.resetFields();
        }}
        okText="确认修改"
        cancelText="取消"
      >
        <Form form={passwordForm} layout="vertical">
          <Form.Item
            name="oldPassword"
            label="原密码"
            rules={[{ required: true, message: '请输入原密码' }]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="请输入原密码" />
          </Form.Item>
          <Form.Item
            name="newPassword"
            label="新密码"
            rules={[
              { required: true, message: '请输入新密码' },
              { min: 6, message: '密码至少6位' },
            ]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="请输入新密码" />
          </Form.Item>
          <Form.Item
            name="confirmPassword"
            label="确认新密码"
            rules={[{ required: true, message: '请确认新密码' }]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="请再次输入新密码" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default Profile;
