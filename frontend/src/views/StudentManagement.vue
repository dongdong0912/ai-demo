<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search, CircleClose, Edit, Delete, User, Male, Female, Phone, Document, Ticket, School, Postcard, Location, Calendar } from '@element-plus/icons-vue'
import { studentApi } from '@/api/student'
import { classApi } from '@/api/class'
import type { Student, GradeClass } from '@/types'

const students = ref<Student[]>([])
const classes = ref<GradeClass[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formData = ref<Partial<Student>>({})
const formRef = ref()
const searchKeyword = ref('')
const filterClassId = ref<number | undefined>()

const rules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

const genderOptions = [{ label: '男', value: '男' }, { label: '女', value: '女' }]
const nationOptions = ['汉族', '满族', '回族', '蒙古族', '藏族', '壮族', '维吾尔族', '土家族', '苗族', '彝族'].map(n => ({ label: n, value: n }))

const statusOptions = [
  { label: '在读', value: 1 },
  { label: '休学', value: 0 },
  { label: '毕业', value: 2 },
  { label: '退学', value: 3 }
]

const getStatusType = (status?: number) => {
  switch (status) {
    case 1: return 'success'
    case 0: return 'warning'
    case 2: return 'info'
    case 3: return 'danger'
    default: return 'info'
  }
}

const getStatusText = (status?: number) => {
  switch (status) {
    case 1: return '在读'
    case 0: return '休学'
    case 2: return '毕业'
    case 3: return '退学'
    default: return '-'
  }
}

const formatDate = (date?: string) => date ? date.split('T')[0] : '-'

const fetchStudents = async () => {
  loading.value = true
  try {
    const res = await studentApi.getList(searchKeyword.value)
    let data = res.data || []
    if (filterClassId.value) {
      data = data.filter(s => s.classId === filterClassId.value)
    }
    students.value = data
  } catch {
    ElMessage.error('获取学生列表失败')
  } finally {
    loading.value = false
  }
}

const fetchClasses = async () => {
  try {
    const res = await classApi.getList()
    classes.value = res.data || []
  } catch {
    console.error('获取班级列表失败')
  }
}

const getClassName = (classId?: number) => {
  if (!classId) return '-'
  const cls = classes.value.find(c => c.id === classId)
  return cls ? cls.className : '-'
}

const getGradeName = (classId?: number) => {
  if (!classId) return ''
  const cls = classes.value.find(c => c.id === classId)
  return cls ? cls.gradeName : ''
}

const handleSearch = () => { fetchStudents() }
const handleRefresh = () => { fetchStudents() }
const handleClassFilterChange = () => { fetchStudents() }

const openAddDialog = () => {
  dialogTitle.value = '添加学生'
  formData.value = { 
    studentNo: '', name: '', gender: '男', idCard: '', classId: undefined,
    phone: '', parentPhone: '', parentName: '', address: '',
    birthDate: '', nation: '汉族', nativePlace: '', enrollmentDate: '', remark: '', status: 1 
  }
  dialogVisible.value = true
}

const openEditDialog = (row: Student) => {
  dialogTitle.value = '编辑学生'
  formData.value = { 
    ...row,
    birthDate: row.birthDate ? row.birthDate.split('T')[0] : '',
    enrollmentDate: row.enrollmentDate ? row.enrollmentDate.split('T')[0] : ''
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const submitData = {
          ...formData.value,
          birthDate: formData.value.birthDate || undefined,
          enrollmentDate: formData.value.enrollmentDate || undefined
        }
        if (formData.value.id) {
          await studentApi.update(submitData as Student)
        } else {
          await studentApi.create(submitData as Student)
        }
        ElMessage.success('操作成功')
        dialogVisible.value = false
        fetchStudents()
      } catch {
        ElMessage.error('操作失败')
      }
    }
  })
}

const handleDelete = async (row: Student) => {
  try {
    await ElMessageBox.confirm(`确定要删除学生「${row.name}」吗？`, '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await studentApi.delete(row.id!)
    ElMessage.success('删除成功')
    fetchStudents()
  } catch {}
}

onMounted(() => {
  fetchStudents()
  fetchClasses()
})
</script>

<template>
  <div class="page-container">
    <div class="action-bar">
      <div class="left-actions">
        <el-input v-model="searchKeyword" placeholder="按姓名、学号、手机号搜索..." clearable class="search-input" @keyup.enter="handleSearch">
          <template #append>
            <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
          </template>
        </el-input>
        <el-select v-model="filterClassId" placeholder="按班级筛选" clearable style="width: 160px" @change="handleClassFilterChange">
          <el-option v-for="cls in classes" :key="cls.id" :label="cls.className" :value="cls.id" />
        </el-select>
        <el-button class="refresh-btn" @click="handleRefresh"><el-icon><Refresh /></el-icon></el-button>
      </div>
      <div class="right-actions">
        <el-button type="primary" class="add-btn" @click="openAddDialog"><el-icon><Plus /></el-icon>添加学生</el-button>
      </div>
    </div>

    <div class="table-card">
      <el-table :data="students" v-loading="loading" stripe>
        <el-table-column prop="id" label="序号" width="60" align="center" />
        <el-table-column prop="studentNo" label="学号" min-width="120">
          <template #default="{ row }">
            <span class="student-no">{{ row.studentNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" min-width="100">
          <template #default="{ row }">
            <div class="student-cell">
              <el-avatar :size="28" style="background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);">{{ row.name?.charAt(0) }}</el-avatar>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" min-width="70" align="center">
          <template #default="{ row }"><el-tag :type="row.gender === '男' ? '' : 'warning'" size="small">{{ row.gender }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="classId" label="班级" min-width="140">
          <template #default="{ row }">
            <div class="class-info">
              <span class="class-name">{{ getClassName(row.classId) }}</span>
              <span class="grade-name">{{ getGradeName(row.classId) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="nation" label="民族" min-width="80" align="center" />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="parentName" label="家长" min-width="90" />
        <el-table-column prop="parentPhone" label="家长电话" min-width="130" />
        <el-table-column prop="status" label="状态" min-width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
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

    <el-dialog v-model="dialogVisible" width="580px" class="custom-dialog" :show-close="false">
      <template #header>
        <div class="dialog-header">
          <div class="dialog-title-section">
            <div class="dialog-icon add">
              <el-icon><Plus v-if="dialogTitle.includes('添加')" /><Edit v-else /></el-icon>
            </div>
            <div class="dialog-text">
              <h3>{{ dialogTitle }}</h3>
              <p>{{ dialogTitle.includes('添加') ? '填写学生基本信息' : '修改学生信息' }}</p>
            </div>
          </div>
          <el-icon class="close-btn" @click="dialogVisible = false"><CircleClose /></el-icon>
        </div>
      </template>
      
      <el-form ref="formRef" :model="formData" :rules="rules" class="dialog-form">
        <div class="form-row">
          <el-form-item prop="studentNo" class="form-item-half">
            <label><el-icon><Ticket /></el-icon>学号</label>
            <el-input v-model="formData.studentNo" placeholder="请输入学号" />
          </el-form-item>
          <el-form-item prop="name" class="form-item-half">
            <label><el-icon><User /></el-icon>姓名</label>
            <el-input v-model="formData.name" placeholder="请输入姓名" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item prop="gender" class="form-item-third">
            <label><el-icon><Male v-if="formData.gender === '男'" /><Female v-else /></el-icon>性别</label>
            <el-select v-model="formData.gender" placeholder="请选择" style="width: 100%">
              <el-option v-for="item in genderOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item prop="nation" class="form-item-third">
            <label>民族</label>
            <el-select v-model="formData.nation" placeholder="请选择" style="width: 100%">
              <el-option v-for="item in nationOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item prop="classId" class="form-item-third">
            <label><el-icon><School /></el-icon>班级</label>
            <el-select v-model="formData.classId" placeholder="请选择" style="width: 100%" clearable>
              <el-option v-for="cls in classes" :key="cls.id" :label="cls.className" :value="cls.id" />
            </el-select>
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item prop="idCard" class="form-item-half">
            <label><el-icon><Postcard /></el-icon>身份证号</label>
            <el-input v-model="formData.idCard" placeholder="请输入身份证号" />
          </el-form-item>
          <el-form-item prop="nativePlace" class="form-item-half">
            <label>籍贯</label>
            <el-input v-model="formData.nativePlace" placeholder="请输入籍贯" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item prop="phone" class="form-item-half">
            <label><el-icon><Phone /></el-icon>手机</label>
            <el-input v-model="formData.phone" placeholder="学生手机号" />
          </el-form-item>
          <el-form-item prop="birthDate" class="form-item-half">
            <label><el-icon><Calendar /></el-icon>出生日期</label>
            <el-date-picker v-model="formData.birthDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item prop="parentName" class="form-item-half">
            <label><el-icon><User /></el-icon>家长姓名</label>
            <el-input v-model="formData.parentName" placeholder="家长姓名" />
          </el-form-item>
          <el-form-item prop="parentPhone" class="form-item-half">
            <label><el-icon><Phone /></el-icon>家长电话</label>
            <el-input v-model="formData.parentPhone" placeholder="家长联系方式" />
          </el-form-item>
        </div>
        <el-form-item prop="address" class="form-item">
          <label><el-icon><Location /></el-icon>家庭住址</label>
          <el-input v-model="formData.address" placeholder="请输入家庭住址" />
        </el-form-item>
        <div class="form-row">
          <el-form-item prop="enrollmentDate" class="form-item-half">
            <label><el-icon><Calendar /></el-icon>入学日期</label>
            <el-date-picker v-model="formData.enrollmentDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
          <el-form-item prop="status" class="form-item-half">
            <label>状态</label>
            <el-select v-model="formData.status" style="width: 100%">
              <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item class="form-item">
          <label><el-icon><Document /></el-icon>备注</label>
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="选填" />
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
.search-input { width: 260px; }
.search-input :deep(.el-input__wrapper) { border-radius: 6px 0 0 6px; box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08); }
.search-input :deep(.el-input-group__append) { border-radius: 0 6px 6px 0; background: #667eea; border-color: #667eea; color: #fff; }
.search-input :deep(.el-input-group__append:hover) { background: #5a71d6; }
.refresh-btn { border-radius: 6px; padding: 8px 12px; }
.refresh-btn :deep(.el-icon) { font-size: 16px; }
.add-btn { border-radius: 6px; padding: 8px 16px; }
.table-card { background: #fff; border-radius: 12px; padding: 16px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04); }
.student-cell { display: flex; align-items: center; gap: 8px; }
.student-no { font-family: monospace; color: #409EFF; font-size: 13px; }
.class-info { display: flex; flex-direction: column; gap: 2px; }
.class-name { font-size: 13px; }
.grade-name { font-size: 11px; color: #909399; }

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
.form-item-third { flex: 1; margin-bottom: 20px; }
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
