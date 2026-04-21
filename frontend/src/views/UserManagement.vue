<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search, CircleClose, User as UserIcon, Message, Phone, Key } from '@element-plus/icons-vue'
import { userApi } from '@/api/user'
import type { User } from '@/types'

const users = ref<User[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formData = ref<Partial<User>>({})
const formRef = ref()
const searchKeyword = ref('')

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确邮箱', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确手机号', trigger: 'blur' }]
}

const roleOptions = [{ label: '管理员', value: 'ADMIN' }, { label: '普通用户', value: 'USER' }]

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await userApi.getList()
    users.value = res.data || []
  } catch {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => ElMessage.info('搜索功能')
const handleRefresh = () => { fetchUsers(); ElMessage.success('刷新成功') }

const openAddDialog = () => {
  dialogTitle.value = '添加用户'
  formData.value = { username: '', email: '', phone: '', role: 'USER', status: 1 }
  dialogVisible.value = true
}

const openEditDialog = (row: User) => {
  dialogTitle.value = '编辑用户'
  formData.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (formData.value.id) {
          await userApi.update(formData.value as User)
        } else {
          await userApi.create(formData.value as User)
        }
        ElMessage.success('操作成功')
        dialogVisible.value = false
        fetchUsers()
      } catch {
        ElMessage.error('操作失败')
      }
    }
  })
}

const handleDelete = async (row: User) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户「${row.username}」吗？`, '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await userApi.delete(row.id!)
    ElMessage.success('删除成功')
    fetchUsers()
  } catch {}
}

const getStatusType = (status: number) => status === 1 ? 'success' : 'danger'
const getStatusLabel = (status: number) => status === 1 ? '正常' : '禁用'
const getRoleLabel = (role: string) => role === 'ADMIN' ? '管理员' : '普通用户'
const getRoleClass = (role: string) => role === 'ADMIN' ? 'role-admin' : 'role-user'
const formatDate = (date: string) => date ? date.split('T')[0] : '-'

onMounted(() => fetchUsers())
</script>

<template>
  <div class="page-wrapper">
    <!-- 操作工具栏 -->
    <div class="action-bar">
      <div class="left-actions">
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索用户名、邮箱或手机号..." 
          clearable 
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
        <el-button class="refresh-btn" @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
        </el-button>
      </div>
      <div class="right-actions">
        <el-button type="primary" class="add-btn" @click="openAddDialog">
          <el-icon><Plus /></el-icon>
          添加用户
        </el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-wrapper">
      <el-table 
        :data="users" 
        v-loading="loading" 
        stripe 
        class="modern-table"
        :header-cell-style="{ background: '#f8fafc', color: '#475569', fontWeight: '600' }"
      >
        <el-table-column prop="id" label="序号" width="60" align="center">
          <template #default="{ $index }">
            <span class="row-index">{{ $index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="32" class="user-avatar">
                {{ row.username?.charAt(0).toUpperCase() }}
              </el-avatar>
              <span class="username">{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="160" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="role" label="角色" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'" size="small">
              {{ getRoleLabel(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 添加/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" width="480px" class="custom-dialog" :show-close="false">
      <template #header>
        <div class="dialog-header">
          <div class="dialog-title-section">
            <div class="dialog-icon" :class="dialogTitle.includes('添加') ? 'add' : 'edit'">
              <el-icon><User v-if="dialogTitle.includes('添加')" /><Edit v-else /></el-icon>
            </div>
            <div class="dialog-text">
              <h3>{{ dialogTitle }}</h3>
              <p>{{ dialogTitle.includes('添加') ? '填写用户基本信息' : '修改用户信息' }}</p>
            </div>
          </div>
          <el-icon class="close-btn" @click="dialogVisible = false"><CircleClose /></el-icon>
        </div>
      </template>
      
      <el-form ref="formRef" :model="formData" :rules="rules" class="dialog-form">
        <el-form-item prop="username" class="form-item">
          <label><el-icon><UserIcon /></el-icon>用户名</label>
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item prop="email" class="form-item">
          <label><el-icon><Message /></el-icon>邮箱</label>
          <el-input v-model="formData.email" placeholder="请输入邮箱地址" />
        </el-form-item>
        <el-form-item prop="phone" class="form-item">
          <label><el-icon><Phone /></el-icon>手机号</label>
          <el-input v-model="formData.phone" placeholder="请输入手机号码" />
        </el-form-item>
        <div class="form-row">
          <el-form-item prop="role" class="form-item-half">
            <label><el-icon><Key /></el-icon>角色</label>
            <el-select v-model="formData.role" placeholder="选择角色" style="width: 100%">
              <el-option v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item prop="status" class="form-item-half">
            <label><el-icon><CircleCheck /></el-icon>状态</label>
            <el-radio-group v-model="formData.status" class="status-group">
              <el-radio :label="1">
                <span class="radio-option active">正常</span>
              </el-radio>
              <el-radio :label="0">
                <span class="radio-option inactive">禁用</span>
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </div>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button class="cancel-btn" @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" class="submit-btn" @click="handleSubmit">
            {{ dialogTitle.includes('添加') ? '确认添加' : '保存修改' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
/* 页面整体布局 */
.page-wrapper {
  padding: 0;
}

/* 操作工具栏 */
.action-bar {
  background: #fff;
  border-radius: 12px;
  padding: 16px 20px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.left-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.right-actions {
  display: flex;
  gap: 12px;
}

.search-input {
  width: 320px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 6px 0 0 6px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.search-input :deep(.el-input-group__append) {
  border-radius: 0 6px 6px 0;
  background: #667eea;
  border-color: #667eea;
  color: #fff;
}

.search-input :deep(.el-input-group__append:hover) {
  background: #5a71d6;
}

.refresh-btn {
  border-radius: 6px;
  padding: 8px 12px;
}

.refresh-btn :deep(.el-icon) {
  font-size: 16px;
}

.add-btn {
  border-radius: 6px;
  padding: 8px 16px;
}

.add-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.add-btn:hover {
  background: linear-gradient(135deg, #7c8ff5 0%, #8a5db5 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.35);
}

.refresh-btn {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #64748b;
}

.refresh-btn:hover {
  background: #f1f5f9;
  color: #475569;
}

/* 数据表格 */
.table-wrapper {
  background: #fff;
  border-radius: 12px;
  padding: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.modern-table {
  border-radius: 12px;
}

.modern-table :deep(.el-table__row) {
  transition: background 0.2s;
}

.modern-table :deep(.el-table__row:hover) {
  background: #f8fafc;
}

.row-index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background: #f1f5f9;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  color: #64748b;
}

/* 用户信息单元格 */
.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-size: 14px;
  font-weight: 600;
}

.username {
  font-weight: 600;
  color: #1e293b;
  font-size: 14px;
}

/* 操作按钮 */
.edit-btn, .delete-btn {
  border-radius: 6px;
  padding: 4px 8px;
}

/* 弹窗样式 */
:deep(.custom-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.custom-dialog .el-dialog__header) {
  margin: 0;
  padding: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

:deep(.custom-dialog .el-dialog__body) {
  padding: 28px 32px;
}

:deep(.custom-dialog .el-dialog__footer) {
  padding: 0 32px 28px;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
}

.dialog-title-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.dialog-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  font-size: 22px;
}

.dialog-text h3 {
  margin: 0;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.dialog-text p {
  margin: 4px 0 0 0;
  color: rgba(255, 255, 255, 0.8);
  font-size: 13px;
}

.close-btn {
  font-size: 24px;
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  transition: color 0.3s;
}

.close-btn:hover {
  color: #fff;
}

/* 表单样式 */
.dialog-form {
  margin-top: 8px;
}

.form-item {
  margin-bottom: 20px;
}

.form-item label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 500;
  color: #475569;
  margin-bottom: 8px;
}

.form-item label .el-icon {
  color: #667eea;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-item-half {
  margin-bottom: 20px;
}

.form-item-half label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 500;
  color: #475569;
  margin-bottom: 8px;
}

.dialog-form :deep(.el-input__wrapper),
.dialog-form :deep(.el-select__wrapper) {
  border-radius: 10px;
  padding: 14px 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid #e2e8f0;
  transition: all 0.3s;
}

.dialog-form :deep(.el-input__wrapper:hover),
.dialog-form :deep(.el-select__wrapper:hover) {
  border-color: #667eea;
}

.dialog-form :deep(.el-input__wrapper.is-focus),
.dialog-form :deep(.el-select__wrapper.is-focused) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.status-group {
  display: flex;
  gap: 20px;
  padding-top: 8px;
}

.radio-option {
  font-weight: 500;
}

.radio-option.active {
  color: #16a34a;
}

.radio-option.inactive {
  color: #dc2626;
}

/* 弹窗底部按钮 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn {
  padding: 12px 28px;
  border-radius: 10px;
  font-weight: 500;
}

.submit-btn {
  padding: 12px 32px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  font-weight: 500;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #7c8ff5 0%, #8a5db5 100%);
}
</style>
