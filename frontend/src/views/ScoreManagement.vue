<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search, Edit, Delete, Upload, Download, Trophy, Files, User, Notebook } from '@element-plus/icons-vue'
import { scoreApi, type Score } from '@/api/score'
import { studentApi } from '@/api/student'
import { courseApi } from '@/api/course'
import type { Student, Course } from '@/types'
import { scoreExcelApi, type ImportResult } from '@/api/excel'

const scores = ref<Score[]>([])
const students = ref<Student[]>([])
const courses = ref<Course[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formData = ref<Partial<Score>>({})
const formRef = ref()
// 搜索相关
const searchName = ref('')
const searchStudentNo = ref('')
const filterCourseId = ref<number | undefined>()
const filterStudentId = ref<number | undefined>()

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
  studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  score: [{ required: true, message: '请输入成绩', trigger: 'blur' }]
}

const examTypeOptions = [
  { label: '期中考试', value: '期中考试' },
  { label: '期末考试', value: '期末考试' },
  { label: '月考', value: '月考' },
  { label: '周考', value: '周考' },
  { label: '随堂测试', value: '随堂测试' },
  { label: '补考', value: '补考' }
]

const statusOptions = [
  { label: '正常', value: 1 },
  { label: '作弊', value: 0 },
  { label: '缓考', value: 2 }
]

const getScoreType = (score?: number) => {
  if (score === undefined || score === null) return 'info'
  if (score >= 90) return 'success'
  if (score >= 80) return 'primary'
  if (score >= 60) return 'warning'
  return 'danger'
}

const getScoreText = (score?: number) => {
  if (score === undefined || score === null) return '-'
  if (score >= 90) return '优秀'
  if (score >= 80) return '良好'
  if (score >= 60) return '及格'
  return '不及格'
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
    const res = await scoreExcelApi.importData(importFileList.value[0].raw)
    importResult.value = res.data
    if (res.data.errorCount === 0) ElMessage.success(`导入成功，共 ${res.data.successCount} 条`)
    fetchScores()
  } catch { ElMessage.error('导入失败') }
  finally { importLoading.value = false }
}

const fetchScores = async () => {
  loading.value = true
  try {
    // 组合姓名和学号为关键字进行搜索
    const keyword = [searchName.value, searchStudentNo.value].filter(v => v.trim()).join(' ').trim()
    const res = await scoreApi.getList({
      keyword: keyword || undefined,
      courseId: filterCourseId.value,
      studentId: filterStudentId.value
    })
    scores.value = res.data || []
    total.value = scores.value.length
  } catch {
    ElMessage.error('获取成绩列表失败')
  } finally {
    loading.value = false
  }
}

const fetchStudents = async () => {
  try {
    const res = await studentApi.getList()
    students.value = res.data || []
  } catch {
    console.error('获取学生列表失败')
  }
}

const fetchCourses = async () => {
  try {
    const res = await courseApi.getList()
    courses.value = res.data || []
  } catch {
    console.error('获取课程列表失败')
  }
}

const getStudentName = (studentId?: number) => {
  if (!studentId) return '-'
  const student = students.value.find(s => s.id === studentId)
  return student ? `${student.name} (${student.studentNo})` : '-'
}

const getCourseName = (courseId?: number) => {
  if (!courseId) return '-'
  const course = courses.value.find(c => c.id === courseId)
  return course ? `${course.courseName} (${course.courseCode})` : '-'
}

const handleSearch = () => { currentPage.value = 1; fetchScores() }
const handleRefresh = () => { currentPage.value = 1; fetchScores() }
const handlePageChange = (page: number) => { currentPage.value = page }
const handleFilterChange = () => { currentPage.value = 1; fetchScores() }

// 计算当前页数据
const paginatedScores = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return scores.value.slice(start, end)
})

const openAddDialog = () => {
  dialogTitle.value = '添加成绩'
  formData.value = {
    studentId: undefined,
    courseId: undefined,
    score: undefined,
    examType: '期末考试',
    examDate: '',
    remark: '',
    status: 1
  }
  dialogVisible.value = true
}

const openEditDialog = (row: Score) => {
  dialogTitle.value = '编辑成绩'
  formData.value = {
    ...row,
    examDate: row.examDate ? row.examDate.split('T')[0] : ''
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
          examDate: formData.value.examDate || undefined
        }
        if (formData.value.id) {
          await scoreApi.update(submitData as Score)
        } else {
          await scoreApi.create(submitData as Score)
        }
        ElMessage.success('操作成功')
        dialogVisible.value = false
        fetchScores()
      } catch {
        ElMessage.error('操作失败')
      }
    }
  })
}

const handleDelete = async (row: Score) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除该成绩记录吗？\n学生: ${row.studentName}\n课程: ${row.courseName}\n成绩: ${row.score}`,
      '提示',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    await scoreApi.delete(row.id!)
    ElMessage.success('删除成功')
    fetchScores()
  } catch {}
}

onMounted(() => {
  fetchScores()
  fetchStudents()
  fetchCourses()
})
</script>

<template>
  <div class="page-container">
    <div class="action-bar">
      <div class="left-actions">
        <div class="search-group">
          <el-input v-model="searchName" placeholder="学生姓名" clearable class="search-field" />
          <el-input v-model="searchStudentNo" placeholder="学号" clearable class="search-field" style="width: 120px" />
          <el-select v-model="filterCourseId" placeholder="课程" clearable style="width: 160px" @change="handleFilterChange">
            <el-option v-for="course in courses" :key="course.id" :label="course.courseName" :value="course.id" />
          </el-select>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
        </div>
      </div>
      <div class="right-actions">
        <div class="excel-btn-group">
          <el-button plain size="default" class="excel-btn" @click="openImportDialog">
            <el-icon><Upload /></el-icon>导入
          </el-button>
          <el-button plain size="default" class="excel-btn" @click="scoreExcelApi.exportData()">
            <el-icon><Download /></el-icon>导出
          </el-button>
          <el-button plain size="default" class="excel-btn" @click="scoreExcelApi.downloadTemplate()">
            <el-icon><Files /></el-icon>模板
          </el-button>
        </div>
        <el-button type="primary" class="add-btn" @click="openAddDialog">
          <el-icon><Plus /></el-icon>添加成绩
        </el-button>
      </div>
    </div>

    <div class="table-card">
      <el-table :data="paginatedScores" v-loading="loading" stripe>
        <el-table-column prop="id" label="序号" width="60" align="center" />
        <el-table-column prop="studentNo" label="学号" min-width="100">
          <template #default="{ row }">
            <span class="student-no">{{ row.studentNo || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="studentName" label="学生姓名" min-width="120">
          <template #default="{ row }">
            <div class="student-cell">
              <el-avatar :size="28" style="background: linear-gradient(135deg, #67C23A 0%, #529b2e 100%);">
                {{ row.studentName?.charAt(0) || '-' }}
              </el-avatar>
              <span>{{ row.studentName || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="courseCode" label="课程编码" min-width="100">
          <template #default="{ row }">
            <span class="course-code">{{ row.courseCode || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="courseName" label="课程名称" min-width="140">
          <template #default="{ row }">
            <el-tag size="small" type="info">{{ row.courseName || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="成绩" min-width="100" align="center">
          <template #default="{ row }">
            <div class="score-cell">
              <el-tag :type="getScoreType(row.score)" size="default" effect="dark">
                {{ row.score ?? '-' }}
              </el-tag>
              <span class="score-level">{{ getScoreText(row.score) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="examType" label="考试类型" min-width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ row.examType || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="examDate" label="考试日期" min-width="110">
          <template #default="{ row }">
            <span class="date-text">{{ formatDate(row.examDate) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="100" show-overflow-tooltip />
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

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" width="600px" :show-close="false">
      <template #header>
        <div class="dialog-header">
          <span class="dialog-title">{{ dialogTitle }}</span>
          <span class="close-btn" @click="dialogVisible = false">&times;</span>
        </div>
      </template>

      <el-form ref="formRef" :model="formData" :rules="rules" class="dialog-form">
        <div class="form-row">
          <el-form-item prop="studentId" class="form-item-half">
            <label><el-icon><User /></el-icon>学生</label>
            <el-select v-model="formData.studentId" placeholder="请选择学生" style="width: 100%" filterable>
              <el-option v-for="student in students" :key="student.id" :label="`${student.name} (${student.studentNo})`" :value="student.id" />
            </el-select>
          </el-form-item>
          <el-form-item prop="courseId" class="form-item-half">
            <label><el-icon><Notebook /></el-icon>课程</label>
            <el-select v-model="formData.courseId" placeholder="请选择课程" style="width: 100%" filterable>
              <el-option v-for="course in courses" :key="course.id" :label="`${course.courseName} (${course.courseCode})`" :value="course.id" />
            </el-select>
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item prop="score" class="form-item-half">
            <label><el-icon><Trophy /></el-icon>成绩</label>
            <el-input-number v-model="formData.score" :min="0" :max="100" :precision="1" style="width: 100%" placeholder="0-100" />
          </el-form-item>
          <el-form-item prop="examType" class="form-item-half">
            <label>考试类型</label>
            <el-select v-model="formData.examType" placeholder="请选择" style="width: 100%">
              <el-option v-for="item in examTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item prop="examDate" class="form-item-half">
            <label>考试日期</label>
            <el-date-picker v-model="formData.examDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
          <el-form-item prop="status" class="form-item-half">
            <label>状态</label>
            <el-select v-model="formData.status" style="width: 100%">
              <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item class="form-item">
          <label>备注</label>
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
    <el-dialog v-model="importDialogVisible" title="导入成绩" width="520px">
      <div class="import-content">
        <el-upload drag accept=".xlsx,.xls" :auto-upload="false" :limit="1"
          :on-change="(file: any, fl: any[]) => { importFileList = fl }"
          :on-remove="(_: any, fl: any[]) => { importFileList = fl }">
          <el-icon class="el-icon--upload"><Upload /></el-icon>
          <div class="el-upload__text">将Excel文件拖到此处，或<em>点击上传</em></div>
          <template #tip>
            <div class="el-upload__tip">仅支持 .xlsx/.xls 文件，大小不超过10MB</div>
          </template>
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
.search-group { display: flex; gap: 10px; align-items: center; }
.search-field { width: 140px; }
.search-group :deep(.el-input__wrapper) { border-radius: 6px; box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08); }
.search-group :deep(.el-button) { border-radius: 6px; background: #667eea; border-color: #667eea; color: #fff; }
.search-group :deep(.el-button:hover) { background: #5a71d6; border-color: #5a71d6; }
.refresh-btn { border-radius: 6px; padding: 8px 12px; }
.refresh-btn :deep(.el-icon) { font-size: 16px; }
.add-btn { border-radius: 6px; padding: 8px 16px; }
.table-card { background: #fff; border-radius: 12px; padding: 16px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04); }
.student-cell { display: flex; align-items: center; gap: 8px; }
.student-no { font-family: monospace; color: #409EFF; font-size: 13px; }
.course-code { font-family: monospace; color: #909399; font-size: 13px; }
.score-cell { display: flex; align-items: center; gap: 8px; justify-content: center; }
.score-level { font-size: 12px; color: #909399; }
.date-text { font-size: 13px; color: #606266; }
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
