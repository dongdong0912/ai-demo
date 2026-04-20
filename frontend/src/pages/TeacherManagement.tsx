import { useState, useEffect } from 'react';
import { Table, Button, Space, Modal, Form, Input, Select, message, Popconfirm, Upload, Avatar } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, TeamOutlined, UploadOutlined } from '@ant-design/icons';
import { Teacher } from '../types/teacher';
import { getTeachers, deleteTeacher, createTeacher, updateTeacher, uploadImage } from '../api/teacher';

const { Column } = Table;

const TeacherManagement: React.FC = () => {
  const [teachers, setTeachers] = useState<Teacher[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingTeacher, setEditingTeacher] = useState<Teacher | null>(null);
  const [uploading, setUploading] = useState(false);
  const [form] = Form.useForm();

  const loadTeachers = async () => {
    setLoading(true);
    try {
      const data = await getTeachers();
      setTeachers(data);
    } catch (error) {
      message.error('加载老师信息失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadTeachers();
  }, []);

  const handleDelete = async (id: number) => {
    try {
      await deleteTeacher(id);
      message.success('删除成功');
      loadTeachers();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleEdit = (record: Teacher) => {
    setEditingTeacher(record);
    form.setFieldsValue({
      name: record.name,
      gender: record.gender,
      subject: record.subject,
      phone: record.phone,
      email: record.email,
      remark: record.remark,
      photo: record.photo,
      status: record.status,
    });
    setModalVisible(true);
  };

  const handleAdd = () => {
    setEditingTeacher(null);
    form.resetFields();
    setModalVisible(true);
  };

  const handleModalClose = () => {
    setModalVisible(false);
    setEditingTeacher(null);
    form.resetFields();
  };

  const handleUpload = async (file: File) => {
    setUploading(true);
    try {
      const res = await uploadImage(file);
      form.setFieldsValue({ photo: res.url });
      message.success('上传成功');
    } catch (error) {
      message.error('上传失败');
    } finally {
      setUploading(false);
    }
    return false; // 阻止默认上传
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (editingTeacher) {
        await updateTeacher(editingTeacher.id!, values);
        message.success('更新成功');
      } else {
        await createTeacher(values);
        message.success('创建成功');
      }
      handleModalClose();
      loadTeachers();
    } catch (error: unknown) {
      const err = error as { response?: { data?: { message?: string } } };
      message.error(err.response?.data?.message || '操作失败');
    }
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>
          <TeamOutlined className="icon" />
          老师管理
        </h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增老师
        </Button>
      </div>

      <Table
        dataSource={teachers}
        rowKey="id"
        loading={loading}
        bordered
        pagination={{ pageSize: 10 }}
      >
        <Column
          title="照片"
          dataIndex="photo"
          key="photo"
          width={80}
          render={(photo: string) => (
            <Avatar 
              size={50} 
              src={photo} 
              icon={<TeamOutlined />}
              style={{ backgroundColor: '#409EFF' }}
            />
          )}
        />
        <Column title="ID" dataIndex="id" key="id" width={80} />
        <Column title="姓名" dataIndex="name" key="name" />
        <Column title="性别" dataIndex="gender" key="gender" width={80} />
        <Column title="科目" dataIndex="subject" key="subject" />
        <Column title="手机号" dataIndex="phone" key="phone" />
        <Column
          title="状态"
          dataIndex="status"
          key="status"
          width={100}
          render={(status: number) => (
            <span className={status === 1 ? 'status-active' : 'status-inactive'}>
              {status === 1 ? '在职' : '离职'}
            </span>
          )}
        />
        <Column title="备注" dataIndex="remark" key="remark" ellipsis />
        <Column
          title="操作"
          key="action"
          width={180}
          render={(_: unknown, record: Teacher) => (
            <Space size="small">
              <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
                编辑
              </Button>
              <Popconfirm
                title="确认删除"
                description="确定要删除该老师吗？"
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
        title={editingTeacher ? '编辑老师' : '新增老师'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={handleModalClose}
        okText="确定"
        cancelText="取消"
        width={600}
      >
        <Form form={form} layout="vertical">
          <div style={{ textAlign: 'center', marginBottom: 24 }}>
            <Form.Item name="photo" valuePropName="photo" noStyle>
              <Avatar 
                size={100} 
                icon={<TeamOutlined />}
                src={form.getFieldValue('photo')}
                style={{ backgroundColor: '#409EFF', cursor: 'pointer' }}
              />
            </Form.Item>
            <div style={{ marginTop: 8 }}>
              <Upload
                showUploadList={false}
                beforeUpload={handleUpload}
                accept="image/*"
              >
                <Button icon={<UploadOutlined />} loading={uploading} size="small">
                  {uploading ? '上传中...' : '上传照片'}
                </Button>
              </Upload>
            </div>
          </div>
          <div style={{ display: 'flex', gap: 16 }}>
            <Form.Item
              name="name"
              label="姓名"
              rules={[{ required: true, message: '请输入姓名' }]}
              style={{ flex: 1 }}
            >
              <Input placeholder="请输入姓名" />
            </Form.Item>
            <Form.Item name="gender" label="性别" initialValue="男" style={{ width: 120 }}>
              <Select>
                <Select.Option value="男">男</Select.Option>
                <Select.Option value="女">女</Select.Option>
              </Select>
            </Form.Item>
          </div>
          <div style={{ display: 'flex', gap: 16 }}>
            <Form.Item name="subject" label="科目" style={{ flex: 1 }}>
              <Input placeholder="请输入所教科目" />
            </Form.Item>
            <Form.Item name="phone" label="手机号" style={{ flex: 1 }}>
              <Input placeholder="请输入手机号" />
            </Form.Item>
          </div>
          <Form.Item name="email" label="邮箱">
            <Input placeholder="请输入邮箱" />
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea placeholder="请输入备注" rows={3} />
          </Form.Item>
          <Form.Item name="status" label="状态" initialValue={1}>
            <Select>
              <Select.Option value={1}>在职</Select.Option>
              <Select.Option value={0}>离职</Select.Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default TeacherManagement;
