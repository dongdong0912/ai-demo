import React, { useState } from 'react';
import { Form, Input, Button, Checkbox, message, Divider } from 'antd';
import { UserOutlined, LockOutlined, SafetyCertificateOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { authApi } from '../api/auth';
import './Login.css';

const Login: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [remember, setRemember] = useState(true);
  const navigate = useNavigate();

  const onFinish = async (values: { username: string; password: string }) => {
    setLoading(true);
    try {
      const res = await authApi.login(values.username, values.password);
      if (res.code === 200) {
        message.success('登录成功，即将跳转...');
        // 记住用户名
        if (remember) {
          localStorage.setItem('rememberedUsername', values.username);
        } else {
          localStorage.removeItem('rememberedUsername');
        }
        setTimeout(() => navigate('/'), 500);
      } else {
        message.error(res.message);
      }
    } catch (error: any) {
      message.error(error?.response?.data?.message || '登录失败，请检查用户名和密码');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      {/* 左侧品牌区域 */}
      <div className="login-brand">
        <div className="brand-content">
          <h1 className="brand-title">教务管理系统</h1>
          <p className="brand-subtitle">Teaching Management System</p>
          <Divider style={{ borderColor: 'rgba(255,255,255,0.3)' }} />
          <div className="brand-features">
            <div className="feature-item">
              <span className="feature-dot"></span>
              <span>用户权限管理</span>
            </div>
            <div className="feature-item">
              <span className="feature-dot"></span>
              <span>教师信息管理</span>
            </div>
            <div className="feature-item">
              <span className="feature-dot"></span>
              <span>课程教学管理</span>
            </div>
          </div>
        </div>
        <div className="brand-footer">
          <span>© 2024 TMS All Rights Reserved</span>
        </div>
      </div>

      {/* 右侧登录区域 */}
      <div className="login-form-wrapper">
        <div className="login-form-container">
          <div className="form-header">
            <h2>欢迎登录</h2>
            <p>请输入您的账号信息</p>
          </div>

          <Form
            name="login"
            onFinish={onFinish}
            autoComplete="off"
            size="large"
            initialValues={{
              username: localStorage.getItem('rememberedUsername') || '',
              password: '',
            }}
          >
            <Form.Item
              name="username"
              rules={[
                { required: true, message: '请输入用户名' },
                { min: 3, message: '用户名至少3个字符' }
              ]}
            >
              <Input
                prefix={<UserOutlined className="input-icon" />}
                placeholder="请输入用户名"
                className="custom-input"
              />
            </Form.Item>

            <Form.Item
              name="password"
              rules={[
                { required: true, message: '请输入密码' },
                { min: 6, message: '密码至少6个字符' }
              ]}
            >
              <Input.Password
                prefix={<LockOutlined className="input-icon" />}
                placeholder="请输入密码"
                className="custom-input"
              />
            </Form.Item>

            <div className="form-options">
              <Checkbox checked={remember} onChange={(e) => setRemember(e.target.checked)}>
                <span className="remember-text">记住用户名</span>
              </Checkbox>
              <a href="#" className="forgot-link">忘记密码？</a>
            </div>

            <Form.Item>
              <Button
                type="primary"
                htmlType="submit"
                block
                loading={loading}
                className="login-btn"
              >
                {loading ? '登录中...' : '登 录'}
              </Button>
            </Form.Item>
          </Form>

          <div className="form-footer">
            <Divider>测试账号</Divider>
            <div className="test-accounts">
              <div className="account-item">
                <span className="account-role admin">管理员</span>
                <span className="account-info">admin / 123456</span>
              </div>
              <div className="account-item">
                <span className="account-role teacher">教师</span>
                <span className="account-info">teacher / 123456</span>
              </div>
              <div className="account-item">
                <span className="account-role student">学生</span>
                <span className="account-info">student / 123456</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
