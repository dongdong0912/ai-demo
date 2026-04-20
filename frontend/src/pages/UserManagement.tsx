import { useState, useEffect } from 'react';
import { Table, Button, Space, Modal, Form, Input, Select, message, Popconfirm } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, UserOutlined } from '@ant-design/icons';
import { User } from '../types/user';
import { getUsers, deleteUser, createUser, updateUser } from '../api/user';

const { Column } = Table;

const UserManagement: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [form] = Form.useForm();

  const loadUsers = async () => {
    setLoading(true);
    try {
      const data = await getUsers();
      setUsers(data);
    } catch (error) {
      message.error('加载用户失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadUsers();
  }, []);

  const handleDelete = async (id: number) => {
    try {
      await deleteUser(id);
      message.success('删除成功');
      loadUsers();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleEdit = (record: User) => {
    setEditingUser(record);
    form.setFieldsValue({
      username: record.username,
      email: record.email,
      phone: record.phone,
      status: record.status,
    });
    setModalVisible(true);
  };

  const handleAdd = () => {
    setEditingUser(null);
    form.resetFields();
    setModalVisible(true);
  };

  const handleModalClose = () => {
    setModalVisible(false);
    setEditingUser(null);
    form.resetFields();
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (editingUser) {
        await updateUser(editingUser.id!, values);
        message.success('更新成功');
      } else {
        await createUser(values);
        message.success('创建成功');
      }
      handleModalClose();
      loadUsers();
    } catch (error: unknown) {
      const err = error as { response?: { data?: { message?: string } } };
      message.error(err.response?.data?.message || '操作失败');
    }
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>
          <UserOutlined className="icon" />
          用户管理
        </h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增用户
        </Button>
      </div>

      <Table
        dataSource={users}
        rowKey="id"
        loading={loading}
        bordered
      >
        <Column title="ID" dataIndex="id" key="id" width={80} />
        <Column title="用户名" dataIndex="username" key="username" />
        <Column title="邮箱" dataIndex="email" key="email" />
        <Column title="手机号" dataIndex="phone" key="phone" />
        <Column
          title="状态"
          dataIndex="status"
          key="status"
          width={100}
          render={(status: number) => (
            <span className={status === 1 ? 'status-active' : 'status-inactive'}>
              {status === 1 ? '启用' : '禁用'}
            </span>
          )}
        />
        <Column
          title="创建时间"
          dataIndex="createTime"
          key="createTime"
          render={(text: string) => text?.split('T')[0] || '-'}
        />
        <Column
          title="操作"
          key="action"
          width={180}
          render={(_: unknown, record: User) => (
            <Space size="small">
              <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
                编辑
              </Button>
              <Popconfirm
                title="确认删除"
                description="确定要删除该用户吗？"
                onConfirm={() => handleDelete(record.id!)}
                okText="确定"
                cancelText="取消"
              >
                <Button type="link" danger icon={<DeleteOutlined />}>
                  删除
                </Button>
              </Popconfirm>
            </Space>
          )}
        />
      </Table>

      <Modal
        title={editingUser ? '编辑用户' : '新增用户'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={handleModalClose}
        okText="确定"
        cancelText="取消"
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="username"
            label="用户名"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input placeholder="请输入用户名" />
          </Form.Item>
          <Form.Item name="email" label="邮箱">
            <Input placeholder="请输入邮箱" />
          </Form.Item>
          <Form.Item name="phone" label="手机号">
            <Input placeholder="请输入手机号" />
          </Form.Item>
          <Form.Item name="status" label="状态" initialValue={1}>
            <Select>
              <Select.Option value={1}>启用</Select.Option>
              <Select.Option value={0}>禁用</Select.Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default UserManagement;
