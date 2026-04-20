import React, { useState } from 'react';
import { Layout, Button, Avatar, Dropdown, Space, message } from 'antd';
import { MenuFoldOutlined, MenuUnfoldOutlined, UserOutlined, SettingOutlined, LogoutOutlined } from '@ant-design/icons';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import SideMenu from './SideMenu';
import { auth } from '../utils/auth';

const { Header, Content, Footer } = Layout;

const MainLayout: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();

  // 获取当前用户信息
  const currentUser = auth.getUser();
  const username = currentUser?.username || 'Admin';
  const roleLabel = currentUser?.role === 'ADMIN' ? '管理员' : '用户';

  // 根据路径获取页面标题
  const getPageTitle = () => {
    const pathMap: Record<string, string> = {
      '/users': '用户管理',
      '/teachers': '老师管理',
      '/courses': '课程管理',
      '/students': '学生管理',
      '/profile': '个人中心',
    };
    return pathMap[location.pathname] || '首页';
  };

  // 用户下拉菜单
  const userMenuItems = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '个人中心',
      onClick: () => navigate('/profile'),
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: '系统设置',
      disabled: true, // 预留功能
    },
    {
      type: 'divider' as const,
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      danger: true,
      onClick: () => {
        auth.logout();
        message.success('已退出登录');
        navigate('/login');
      },
    },
  ];

  return (
    <Layout className="main-layout">
      <SideMenu collapsed={collapsed} />
      <Layout>
        <Header className="header">
          <Button
            type="text"
            icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            onClick={() => setCollapsed(!collapsed)}
            className="collapse-btn"
          />
          <span className="header-title">{getPageTitle()}</span>
          
          <div className="header-avatar">
            <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
              <Space style={{ cursor: 'pointer' }}>
                <Avatar size="small" style={{ backgroundColor: '#409EFF' }}>
                  {username.charAt(0).toUpperCase()}
                </Avatar>
                <span>{username}</span>
                <span style={{ fontSize: 12, color: '#888' }}>({roleLabel})</span>
              </Space>
            </Dropdown>
          </div>
        </Header>
        <Content className="content">
          <Outlet />
        </Content>
        <Footer className="app-footer">
          Teaching Management System v1.0.0 ©{new Date().getFullYear()} - Powered by React + Ant Design
        </Footer>
      </Layout>
    </Layout>
  );
};

export default MainLayout;
