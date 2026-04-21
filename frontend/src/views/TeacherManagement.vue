<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search, CircleClose } from '@element-plus/icons-vue'
import { teacherApi } from '@/api/teacher'
import type { Teacher } from '@/types'

const teachers = ref<Teacher[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formData = ref<Partial<Teacher>>({})
const formRef = ref()
const searchKeyword = ref('')

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确邮箱', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确手机号', trigger: 'blur' }]
}

const genderOptions = [{ label: '男', value: '男' }, { label: '女', value: '女' }]
const subjectOptions = ['数学', '语文', '英语', '物理', '化学', '历史', '地理', '生物', '政治'].map(s => ({ label: s, value: s }))

const fetchTeachers = async () => {
  loading.value = true
  try {
    const res = await teacherApi.getList(searchKeyword.value)
    teachers.value = res.data || []
  } catch {
    ElMessage.error('获取教师列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { fetchTeachers() }
const handleRefresh = () => { fetchTeachers() }

const openAddDialog = () => {
  dialogTitle.value = '添加教师'
  formData.value = { name: '', gender: '男', subject: '', email: '', phone: '', remark: '' }
  dialogVisible.value = true
}

const openEditDialog = (row: Teacher) => {
  dialogTitle.value = '编辑教师'
  formData.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (formData.value.id) {
          await teacherApi.update(formData.value as Teacher)
        } else {
          await teacherApi.create(formData.value as Teacher)
        }
        ElMessage.success('操作成功')
        dialogVisible.value = false
        fetchTeachers()
      } catch {
        ElMessage.error('操作失败')
      }
    }
  })
}

const handleDelete = async (row: Teacher) => {
  try {
    await ElMessageBox.confirm(`确定要删除教师「${row.name}」吗？`, '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await teacherApi.delete(row.id!)
    ElMessage.success('删除成功')
    fetchTeachers()
  } catch {}
}

onMounted(() => fetchTeachers())
</script>

<template>
  <div class="page-container">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="searchKeyword" placeholder="按姓名或手机号搜索..." clearable class="search-input" @input="handleSearch" @keyup.enter="handleSearch">
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
      <el-table :data="teachers" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="name" label="姓名" min-width="100">
          <template #default="{ row }">
            <div class="teacher-cell">
              <el-avatar :size="28" style="background: linear-gradient(135deg, #67C23A 0%, #5daf34 100%);">{{ row.name?.charAt(0) }}</el-avatar>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" width="70" align="center">
          <template #default="{ row }"><el-tag :type="row.gender === '男' ? 'primary' : 'warning'" size="small">{{ row.gender }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="subject" label="科目" width="90" align="center">
          <template #default="{ row }"><el-tag type="success" size="small">{{ row.subject }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="150" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="remark" label="备注" min-width="140" show-overflow-tooltip>
          <template #default="{ row }"><span class="remark-text">{{ row.remark || '-' }}</span></template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="420px" :show-close="false">
      <template #header>
        <span class="custom-header">
          <span>{{ dialogTitle }}</span>
          <el-icon class="close-icon" @click="dialogVisible = false"><CircleClose /></el-icon>
        </span>
      </template>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="60px" class="simple-form">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="formData.gender" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in genderOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="科目" prop="subject">
          <el-select v-model="formData.subject" placeholder="请选择科目" style="width: 100%">
            <el-option v-for="item in subjectOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="选填" />
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
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; gap: 12px; }
.toolbar-left, .toolbar-right { display: flex; gap: 8px; }
.search-input { width: 200px; }
.table-card { background: #fff; border-radius: 8px; padding: 12px; }
.teacher-cell { display: flex; align-items: center; gap: 8px; }
.remark-text { font-size: 12px; color: #909399; }

.simple-form :deep(.el-form-item) {
  margin-bottom: 16px;
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
