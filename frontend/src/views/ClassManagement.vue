<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
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

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

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
    total.value = classes.value.length
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

const handleSearch = () => { currentPage.value = 1; fetchClasses() }
const handleRefresh = () => { currentPage.value = 1; fetchClasses() }
const handlePageChange = (page: number) => { currentPage.value = page }

// 计算当前页数据
const paginatedClasses = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return classes.value.slice(start, end)
})

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
      <el-table :data="paginatedClasses" v-loading="loading" stripe>
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
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next, jumper"
          background
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" width="520px" :show-close="false">
      <template #header>
        <div class="dialog-header">
          <span class="dialog-title">{{ dialogTitle }}</span>
          <span class="close-btn" @click="dialogVisible = false">&times;</span>
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
.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-right: 8px;
}
.dialog-title { font-size: 16px; font-weight: 600; color: #303133; }
.close-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #f0f2f5;
  color: #666;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.2s;
  line-height: 1;
  user-select: none;
}
.close-btn:hover {
  background: #e4e7ed;
  color: #303133;
}

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
.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: 16px; }


.dialog-form { padding: 24px 24px 0; }
.form-row { display: flex; gap: 16px; }
.form-item { margin-bottom: 20px; }
.form-item-half { flex: 1; margin-bottom: 20px; }
.dialog-form label { display: flex; align-items: center; gap: 6px; font-size: 13px; color: #666; margin-bottom: 8px; }
.dialog-form label .el-icon { color: #999; }
.dialog-form :deep(.el-input__wrapper) { border-radius: 6px; border: 1px solid #e0e0e0; box-shadow: none !important; }
.dialog-form :deep(.el-input__wrapper:hover) { border-color: #aaa; }
.dialog-form :deep(.el-input__wrapper.is-focus) { border-color: #667eea; }

.dialog-footer { display: flex; justify-content: flex-end; gap: 12px; }
.cancel-btn { border-radius: 6px; border: 1px solid #ddd; color: #666; }
.cancel-btn:hover { border-color: #999; color: #333; }
.submit-btn { border-radius: 6px; background: #667eea; border: none; }
.submit-btn:hover { background: #5a71d6; }
</style>
