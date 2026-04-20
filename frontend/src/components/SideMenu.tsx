import React from 'react';
import { Layout, Menu } from 'antd';
import { UserOutlined, TeamOutlined, BookOutlined, ReadOutlined } from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';

const { Sider } = Layout;

// 可扩展的菜单配置
const menuItems = [
  {
    key: '/users',
    icon: <UserOutlined />,
    label: '用户管理',
  },
  {
    key: '/teachers',
    icon: <TeamOutlined />,
    label: '老师管理',
  },
  {
    key: '/courses',
    icon: <BookOutlined />,
    label: '课程管理',
    disabled: true, // 预留功能
  },
  {
    key: '/students',
    icon: <ReadOutlined />,
    label: '学生管理',
    disabled: true, // 预留功能
  },
];

interface SideMenuProps {
  collapsed: boolean;
}

const SideMenu: React.FC<SideMenuProps> = ({ collapsed }) => {
  const navigate = useNavigate();
  const location = useLocation();

  const handleMenuClick = ({ key }: { key: string }) => {
    navigate(key);
  };

  return (
    <Sider trigger={null} collapsible collapsed={collapsed} width={220}>
      {/* Logo 区域 */}
      <div className="logo-container">
        {collapsed ? (
          <span className="logo-collapsed">
            <BookOutlined />
          </span>
        ) : (
          <>
            <BookOutlined className="logo-icon" />
            <span className="logo-text">教学管理系统</span>
          </>
        )}
      </div>
      
      {/* 菜单 */}
      <Menu
        theme="dark"
        mode="inline"
        selectedKeys={[location.pathname]}
        items={menuItems}
        onClick={handleMenuClick}
        style={{ borderRight: 0 }}
      />
    </Sider>
  );
};

export default SideMenu;
