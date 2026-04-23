<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search, CircleClose, Edit, Delete, User, Male, Female, Phone, Message, Reading, Document, Postcard, Location, Calendar } from '@element-plus/icons-vue'
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
const subjectOptions = ['数学', '语文', '英语', '物理', '化学', '历史', '地理', '生物', '政治', '音乐', '体育', '美术', '信息技术'].map(s => ({ label: s, value: s }))
const titleOptions = ['初级教师', '二级教师', '一级教师', '高级教师', '正高级教师'].map(s => ({ label: s, value: s }))

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
  formData.value = { name: '', gender: '男', subject: '', title: '', idCard: '', email: '', phone: '', address: '', birthDate: '', entryDate: '', remark: '' }
  dialogVisible.value = true
}

const openEditDialog = (row: Teacher) => {
  dialogTitle.value = '编辑教师'
  formData.value = { 
    ...row,
    birthDate: row.birthDate ? row.birthDate.split('T')[0] : '',
    entryDate: row.entryDate ? row.entryDate.split('T')[0] : ''
  }
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

const formatDate = (date?: string) => date ? date.split('T')[0] : '-'

onMounted(() => fetchTeachers())
</script>

<template>
  <div class="page-container">
    <div class="action-bar">
      <div class="left-actions">
        <el-input v-model="searchKeyword" placeholder="按姓名或手机号搜索..." clearable class="search-input" @input="handleSearch" @keyup.enter="handleSearch">
          <template #append>
            <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
          </template>
        </el-input>
        <el-button class="refresh-btn" @click="handleRefresh"><el-icon><Refresh /></el-icon></el-button>
      </div>
      <div class="right-actions">
        <el-button type="primary" class="add-btn" @click="openAddDialog"><el-icon><Plus /></el-icon>添加教师</el-button>
      </div>
    </div>

    <div class="table-card">
      <el-table :data="teachers" v-loading="loading" stripe>
        <el-table-column prop="id" label="序号" width="60" align="center" />
        <el-table-column prop="name" label="姓名" min-width="100">
          <template #default="{ row }">
            <div class="teacher-cell">
              <el-avatar :size="28" style="background: linear-gradient(135deg, #67C23A 0%, #5daf34 100%);">{{ row.name?.charAt(0) }}</el-avatar>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" min-width="70" align="center">
          <template #default="{ row }"><el-tag :type="row.gender === '男' ? '' : 'warning'" size="small">{{ row.gender }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="subject" label="科目" min-width="90" align="center">
          <template #default="{ row }"><el-tag type="success" size="small">{{ row.subject }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="title" label="职称" min-width="100" align="center" />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="entryDate" label="入职日期" min-width="110" align="center">
          <template #default="{ row }">{{ formatDate(row.entryDate) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '在职' : '离职' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" width="560px" class="custom-dialog" :show-close="false">
      <template #header>
        <div class="dialog-header">
          <div class="dialog-title-section">
            <div class="dialog-icon add">
              <el-icon><Plus v-if="dialogTitle.includes('添加')" /><Edit v-else /></el-icon>
            </div>
            <div class="dialog-text">
              <h3>{{ dialogTitle }}</h3>
              <p>{{ dialogTitle.includes('添加') ? '填写教师基本信息' : '修改教师信息' }}</p>
            </div>
          </div>
          <el-icon class="close-btn" @click="dialogVisible = false"><CircleClose /></el-icon>
        </div>
      </template>
      
      <el-form ref="formRef" :model="formData" :rules="rules" class="dialog-form">
        <div class="form-row">
          <el-form-item prop="name" class="form-item-half">
            <label><el-icon><User /></el-icon>姓名</label>
            <el-input v-model="formData.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item prop="gender" class="form-item-half">
            <label><el-icon><Male v-if="formData.gender === '男'" /><Female v-else /></el-icon>性别</label>
            <el-select v-model="formData.gender" placeholder="请选择" style="width: 100%">
              <el-option v-for="item in genderOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item prop="subject" class="form-item-half">
            <label><el-icon><Reading /></el-icon>科目</label>
            <el-select v-model="formData.subject" placeholder="请选择" style="width: 100%">
              <el-option v-for="item in subjectOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item prop="title" class="form-item-half">
            <label><el-icon><Postcard /></el-icon>职称</label>
            <el-select v-model="formData.title" placeholder="请选择" style="width: 100%">
              <el-option v-for="item in titleOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item prop="idCard" class="form-item-half">
            <label><el-icon><Postcard /></el-icon>身份证号</label>
            <el-input v-model="formData.idCard" placeholder="请输入身份证号" />
          </el-form-item>
          <el-form-item prop="phone" class="form-item-half">
            <label><el-icon><Phone /></el-icon>手机</label>
            <el-input v-model="formData.phone" placeholder="请输入手机号" />
          </el-form-item>
        </div>
        <el-form-item prop="email" class="form-item">
          <label><el-icon><Message /></el-icon>邮箱</label>
          <el-input v-model="formData.email" placeholder="请输入邮箱地址" />
        </el-form-item>
        <el-form-item prop="address" class="form-item">
          <label><el-icon><Location /></el-icon>地址</label>
          <el-input v-model="formData.address" placeholder="请输入家庭住址" />
        </el-form-item>
        <div class="form-row">
          <el-form-item prop="birthDate" class="form-item-half">
            <label><el-icon><Calendar /></el-icon>出生日期</label>
            <el-date-picker v-model="formData.birthDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
          <el-form-item prop="entryDate" class="form-item-half">
            <label><el-icon><Calendar /></el-icon>入职日期</label>
            <el-date-picker v-model="formData.entryDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
        </div>
        <el-form-item class="form-item">
          <label><el-icon><Document /></el-icon>备注</label>
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
        <el-form-item v-if="formData.id" prop="status" class="form-item">
          <label>状态</label>
          <el-select v-model="formData.status" style="width: 100%">
            <el-option label="在职" :value="1" />
            <el-option label="离职" :value="0" />
          </el-select>
        </el-form-item>
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
.page-container { padding: 0; }
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
.left-actions, .right-actions { display: flex; gap: 12px; }
.search-input { width: 280px; }
.search-input :deep(.el-input__wrapper) { border-radius: 6px 0 0 6px; box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08); }
.search-input :deep(.el-input-group__append) { border-radius: 0 6px 6px 0; background: #667eea; border-color: #667eea; color: #fff; }
.search-input :deep(.el-input-group__append:hover) { background: #5a71d6; }
.refresh-btn { border-radius: 6px; padding: 8px 12px; }
.refresh-btn :deep(.el-icon) { font-size: 16px; }
.add-btn { border-radius: 6px; padding: 8px 16px; }
.table-card { background: #fff; border-radius: 12px; padding: 16px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04); }
.teacher-cell { display: flex; align-items: center; gap: 8px; }

::deep(.custom-dialog) { border-radius: 16px; overflow: hidden; }
::deep(.custom-dialog .el-dialog__header) { margin: 0; padding: 0; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
::deep(.custom-dialog .el-dialog__body) { padding: 28px 32px; max-height: 65vh; overflow-y: auto; }
::deep(.custom-dialog .el-dialog__footer) { padding: 0 32px 28px; }

.dialog-header { display: flex; justify-content: space-between; align-items: center; padding: 24px 32px; }
.dialog-title-section { display: flex; align-items: center; gap: 16px; }
.dialog-icon { width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center; background: rgba(255, 255, 255, 0.2); color: #fff; }
.dialog-text h3 { margin: 0; font-size: 18px; font-weight: 600; color: #fff; }
.dialog-text p { margin: 4px 0 0; font-size: 13px; color: rgba(255, 255, 255, 0.85); }
.close-btn { font-size: 20px; color: rgba(255, 255, 255, 0.8); cursor: pointer; transition: color 0.3s; }
.close-btn:hover { color: #fff; }

.dialog-form { margin-top: 8px; }
.form-row { display: flex; gap: 16px; }
.form-item { margin-bottom: 20px; }
.form-item-half { flex: 1; margin-bottom: 20px; }
.dialog-form label { display: flex; align-items: center; gap: 6px; font-size: 13px; color: #64748b; margin-bottom: 8px; font-weight: 500; }
.dialog-form label .el-icon { color: #667eea; }
.dialog-form :deep(.el-input__wrapper) { border-radius: 8px; box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08); }
.dialog-form :deep(.el-input__wrapper:hover) { box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15); }
.dialog-form :deep(.el-input__wrapper.is-focus) { box-shadow: 0 2px 8px rgba(102, 126, 234, 0.2); }
.dialog-form :deep(.el-textarea__inner) { border-radius: 8px; box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08); }

.dialog-footer { display: flex; justify-content: flex-end; gap: 12px; }
.cancel-btn { border-radius: 8px; padding: 10px 24px; }
.submit-btn { border-radius: 8px; padding: 10px 24px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none; }
.submit-btn:hover { opacity: 0.9; }
</style>
