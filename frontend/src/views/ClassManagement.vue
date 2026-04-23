<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search, CircleClose, Edit, Delete, User, School, Reading } from '@element-plus/icons-vue'
import { classApi, type GradeClassWithCount } from '@/api/class'
import { teacherApi } from '@/api/teacher'
import type { GradeClass, Teacher } from '@/types'

const classes = ref<GradeClassWithCount[]>([])
const teachers = ref<Teacher[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formData = ref<Partial<GradeClass>>({})
const formRef = ref()
const searchKeyword = ref('')

const rules = {
  className: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
  gradeName: [{ required: true, message: '请输入年级名称', trigger: 'blur' }]
}

const gradeNameOptions = [
  { label: '2024级高一', value: '2024级高一' },
  { label: '2023级高二', value: '2023级高二' },
  { label: '2022级高三', value: '2022级高三' }
]

const statusOptions = [
  { label: '正常', value: 1 },
  { label: '已解散', value: 0 }
]

const fetchClasses = async () => {
  loading.value = true
  try {
    const res = await classApi.getListWithCount()
    classes.value = res.data || []
  } catch {
    ElMessage.error('获取班级列表失败')
  } finally {
    loading.value = false
  }
}

const fetchTeachers = async () => {
  try {
    const res = await teacherApi.getList()
    teachers.value = res.data || []
  } catch {
    console.error('获取教师列表失败')
  }
}

const getTeacherName = (id?: number) => {
  if (!id) return '-'
  const teacher = teachers.value.find(t => t.id === id)
  return teacher ? teacher.name : '-'
}

const handleSearch = () => { fetchClasses() }
const handleRefresh = () => { fetchClasses() }

const openAddDialog = () => {
  dialogTitle.value = '添加班级'
  formData.value = { className: '', gradeName: '', headTeacherId: undefined, maxStudents: 50, status: 1 }
  dialogVisible.value = true
}

const openEditDialog = (row: GradeClass) => {
  dialogTitle.value = '编辑班级'
  formData.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (formData.value.id) {
          await classApi.update(formData.value as GradeClass)
        } else {
          await classApi.create(formData.value as GradeClass)
        }
        ElMessage.success('操作成功')
        dialogVisible.value = false
        fetchClasses()
      } catch {
        ElMessage.error('操作失败')
      }
    }
  })
}

const handleDelete = async (row: GradeClass) => {
  if (row.studentCount && row.studentCount > 0) {
    ElMessage.warning('该班级有学生，无法删除')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要删除班级「${row.className}」吗？`, '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await classApi.delete(row.id!)
    ElMessage.success('删除成功')
    fetchClasses()
  } catch {}
}

onMounted(() => {
  fetchClasses()
  fetchTeachers()
})
</script>

<template>
  <div class="page-container">
    <div class="action-bar">
      <div class="left-actions">
        <el-input v-model="searchKeyword" placeholder="搜索班级..." clearable class="search-input">
          <template #append>
            <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
          </template>
        </el-input>
        <el-button class="refresh-btn" @click="handleRefresh"><el-icon><Refresh /></el-icon></el-button>
      </div>
      <div class="right-actions">
        <el-button type="primary" class="add-btn" @click="openAddDialog"><el-icon><Plus /></el-icon>添加班级</el-button>
      </div>
    </div>

    <div class="table-card">
      <el-table :data="classes" v-loading="loading" stripe>
        <el-table-column prop="id" label="序号" width="60" align="center" />
        <el-table-column prop="className" label="班级名称">
          <template #default="{ row }">
            <div class="class-cell">
              <el-icon class="class-icon"><School /></el-icon>
              <span>{{ row.className }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="gradeName" label="年级" min-width="130" align="center">
          <template #default="{ row }">
            <el-tag type="primary" size="small">{{ row.gradeName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="headTeacherId" label="班主任" min-width="100" align="center">
          <template #default="{ row }">{{ getTeacherName(row.headTeacherId) }}</template>
        </el-table-column>
        <el-table-column label="学生人数" min-width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="row.studentCount > 0 ? 'success' : 'info'" size="small">{{ row.studentCount || 0 }} / {{ row.maxStudents || 50 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '正常' : '已解散' }}</el-tag>
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

    <el-dialog v-model="dialogVisible" width="480px" class="custom-dialog" :show-close="false">
      <template #header>
        <div class="dialog-header">
          <div class="dialog-title-section">
            <div class="dialog-icon add">
              <el-icon><Plus v-if="dialogTitle.includes('添加')" /><Edit v-else /></el-icon>
            </div>
            <div class="dialog-text">
              <h3>{{ dialogTitle }}</h3>
              <p>{{ dialogTitle.includes('添加') ? '填写班级基本信息' : '修改班级信息' }}</p>
            </div>
          </div>
          <el-icon class="close-btn" @click="dialogVisible = false"><CircleClose /></el-icon>
        </div>
      </template>
      
      <el-form ref="formRef" :model="formData" :rules="rules" class="dialog-form">
        <div class="form-row">
          <el-form-item prop="gradeName" class="form-item-half">
            <label><el-icon><Reading /></el-icon>年级</label>
            <el-select v-model="formData.gradeName" placeholder="请选择年级" style="width: 100%">
              <el-option v-for="item in gradeNameOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item prop="className" class="form-item-half">
            <label><el-icon><School /></el-icon>班级名称</label>
            <el-input v-model="formData.className" placeholder="如: 高一(1)班" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item prop="headTeacherId" class="form-item-half">
            <label><el-icon><User /></el-icon>班主任</label>
            <el-select v-model="formData.headTeacherId" placeholder="请选择班主任" style="width: 100%" clearable>
              <el-option v-for="item in teachers" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item prop="maxStudents" class="form-item-half">
            <label><el-icon><User /></el-icon>最大人数</label>
            <el-input-number v-model="formData.maxStudents" :min="1" :max="100" style="width: 100%" />
          </el-form-item>
        </div>
        <el-form-item v-if="formData.id" prop="status" class="form-item">
          <label>状态</label>
          <el-select v-model="formData.status" style="width: 100%">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
.class-cell { display: flex; align-items: center; gap: 8px; }
.class-icon { color: #667eea; font-size: 18px; }

/* 弹窗样式 */
::deep(.custom-dialog) { border-radius: 16px; overflow: hidden; }
::deep(.custom-dialog .el-dialog__header) { margin: 0; padding: 0; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
::deep(.custom-dialog .el-dialog__body) { padding: 28px 32px; }
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

.dialog-footer { display: flex; justify-content: flex-end; gap: 12px; }
.cancel-btn { border-radius: 8px; padding: 10px 24px; }
.submit-btn { border-radius: 8px; padding: 10px 24px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none; }
.submit-btn:hover { opacity: 0.9; }
</style>
