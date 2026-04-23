<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search, CircleClose, Edit, Delete, User, Male, Female, Phone, Document, Ticket, School, Postcard, Location, Calendar, Upload, Download } from '@element-plus/icons-vue'
import { studentApi } from '@/api/student'
import { classApi } from '@/api/class'
import { studentExcelApi, type ImportResult } from '@/api/excel'
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
// 导入导出相关
const importDialogVisible = ref(false)
const importFileList = ref<any[]>([])
const importLoading = ref(false)
const importResult = ref<ImportResult | null>(null)

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

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

// 导入操作
const openImportDialog = () => {
  importFileList.value = []
  importResult.value = null
  importDialogVisible.value = true
}
const handleImport = async () => {
  if (importFileList.value.length === 0) return
  importLoading.value = true
  try {
    const res = await studentExcelApi.importData(importFileList.value[0].raw)
    importResult.value = res.data
    if (res.data.errorCount === 0) ElMessage.success(`导入成功，共 ${res.data.successCount} 条`)
    fetchStudents()
  } catch { ElMessage.error('导入失败') }
  finally { importLoading.value = false }
}

const fetchStudents = async () => {
  loading.value = true
  try {
    const res = await studentApi.getList(searchKeyword.value)
    let data = res.data || []
    if (filterClassId.value) {
      data = data.filter(s => s.classId === filterClassId.value)
    }
    students.value = data
    total.value = data.length
  } catch {
    ElMessage.error('获取学生列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { currentPage.value = 1; fetchStudents() }
const handleRefresh = () => { currentPage.value = 1; fetchStudents() }
const handlePageChange = (page: number) => { currentPage.value = page }

// 计算当前页数据
const paginatedStudents = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return students.value.slice(start, end)
})

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

const handleClassFilterChange = () => { currentPage.value = 1; fetchStudents() }

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
        <div class="excel-btn-group">
          <el-button plain size="default" class="excel-btn" @click="openImportDialog"><el-icon><Upload /></el-icon>导入</el-button>
          <el-button plain size="default" class="excel-btn" @click="studentExcelApi.exportData()"><el-icon><Download /></el-icon>导出</el-button>
          <el-button plain size="default" class="excel-btn" @click="studentExcelApi.downloadTemplate()">模板</el-button>
        </div>
        <el-button type="primary" class="add-btn" @click="openAddDialog"><el-icon><Plus /></el-icon>添加学生</el-button>
      </div>
    </div>

    <div class="table-card">
      <el-table :data="paginatedStudents" v-loading="loading" stripe>
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
          <template #default="{ row }"><el-tag :type="row.gender === '男' ? undefined : 'warning'" size="small">{{ row.gender }}</el-tag></template>
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

    <el-dialog v-model="dialogVisible" width="600px" :show-close="false">
      <template #header>
        <div class="dialog-header">
          <span class="dialog-title">{{ dialogTitle }}</span>
          <span class="close-btn" @click="dialogVisible = false">&times;</span>
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

    <!-- 导入对话框 -->
    <el-dialog v-model="importDialogVisible" title="导入学生" width="520px">
      <div class="import-content">
        <el-upload drag accept=".xlsx,.xls" :auto-upload="false" :limit="1"
          :on-change="(file: any, fl: any[]) => { importFileList = fl }"
          :on-remove="(_: any, fl: any[]) => { importFileList = fl }">
          <el-icon class="el-icon--upload"><Upload /></el-icon>
          <div class="el-upload__text">将Excel文件拖到此处，或<em>点击上传</em></div>
          <template #tip><div class="el-upload__tip">仅支持 .xlsx/.xls 文件，大小不超过10MB</div></template>
        </el-upload>
        <div v-if="importResult" class="import-result">
          <el-alert :title="`导入完成: 成功 ${importResult.successCount} 条，失败 ${importResult.errorCount} 条`"
                    :type="importResult.errorCount === 0 ? 'success' : 'warning'" show-icon :closable="false" />
          <el-collapse v-if="importResult.errors.length > 0" class="error-collapse">
            <el-collapse-item title="查看错误详情">
              <div v-for="(err, idx) in importResult.errors" :key="idx" class="error-item">{{ err }}</div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="importDialogVisible = false">关闭</el-button>
          <el-button type="primary" :loading="importLoading" @click="handleImport">开始导入</el-button>
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
.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: 16px; }
.class-name { font-size: 13px; }
.grade-name { font-size: 11px; color: #909399; }


.dialog-form { padding: 24px 24px 0; }
.form-row { display: flex; gap: 16px; }
.form-item { margin-bottom: 20px; }
.form-item-half { flex: 1; margin-bottom: 20px; }
.form-item-third { flex: 1; margin-bottom: 20px; }
.dialog-form label { display: flex; align-items: center; gap: 6px; font-size: 13px; color: #666; margin-bottom: 8px; }
.dialog-form label .el-icon { color: #999; }
.dialog-form :deep(.el-input__wrapper) { border-radius: 6px; border: 1px solid #e0e0e0; box-shadow: none !important; }
.dialog-form :deep(.el-input__wrapper:hover) { border-color: #aaa; }
.dialog-form :deep(.el-input__wrapper.is-focus) { border-color: #667eea; }
.dialog-form :deep(.el-textarea__inner) { border-radius: 6px; border: 1px solid #e0e0e0; box-shadow: none !important; }

.dialog-footer { display: flex; justify-content: flex-end; gap: 12px; }
.cancel-btn { border-radius: 6px; border: 1px solid #ddd; color: #666; }
.cancel-btn:hover { border-color: #999; color: #333; }
.submit-btn { border-radius: 6px; background: #667eea; border: none; }
.submit-btn:hover { background: #5a71d6; }

/* Excel 导入导出 */
.excel-btn-group { display: flex; gap: 6px; }
.excel-btn { border-radius: 6px !important; font-size: 13px; }
.import-content { padding: 10px 0; }
.import-result { margin-top: 16px; }
.error-collapse { margin-top: 10px; }
.error-item { padding: 4px 0; color: #f56c6c; font-size: 12px; }
</style>
