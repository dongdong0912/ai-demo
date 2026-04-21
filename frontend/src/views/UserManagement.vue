<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search, CircleClose } from '@element-plus/icons-vue'
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
const formatDate = (date: string) => date ? date.split('T')[0] : '-'

onMounted(() => fetchUsers())
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
    </div>

    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="searchKeyword" placeholder="搜索..." clearable class="search-input" @keyup.enter="handleSearch">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
      <div class="toolbar-right">
        <el-button @click="handleRefresh"><el-icon><Refresh /></el-icon>刷新</el-button>
        <el-button type="primary" @click="openAddDialog"><el-icon><Plus /></el-icon>添加</el-button>
      </div>
    </div>

    <div class="table-card">
      <el-table :data="users" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="username" label="用户名" min-width="120">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="28">{{ row.username?.charAt(0).toUpperCase() }}</el-avatar>
              <span>{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="160" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="role" label="角色" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'" size="small">{{ getRoleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="110" align="center">
          <template #default="{ row }"><span class="date-text">{{ formatDate(row.createTime) }}</span></template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="400px" :show-close="false">
      <template #header>
        <span class="custom-header">
          <span>{{ dialogTitle }}</span>
          <el-icon class="close-icon" @click="dialogVisible = false"><CircleClose /></el-icon>
        </span>
      </template>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="60px" class="simple-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="formData.role" style="width: 100%">
            <el-option v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-container { padding: 0; }
.page-header { margin-bottom: 12px; }
.page-title { font-size: 18px; font-weight: 600; color: #303133; margin: 0; }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; gap: 12px; }
.toolbar-left, .toolbar-right { display: flex; gap: 8px; }
.search-input { width: 200px; }
.table-card { background: #fff; border-radius: 8px; padding: 12px; }
.user-cell { display: flex; align-items: center; gap: 8px; }
.date-text { font-size: 12px; color: #909399; }

.simple-form :deep(.el-form-item) {
  margin-bottom: 14px;
}

.custom-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  font-weight: 600;
}

.close-icon {
  font-size: 18px;
  color: #909399;
  cursor: pointer;
  transition: color 0.3s;
}

.close-icon:hover {
  color: #F56C6C;
}
</style>
