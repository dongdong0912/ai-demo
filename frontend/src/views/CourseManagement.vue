<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search, CircleClose, Edit, Notebook, Ticket, Clock, User, Document, TrendCharts } from '@element-plus/icons-vue'
import { courseApi } from '@/api/course'
import { teacherApi } from '@/api/teacher'
import { courseExcelApi, type ImportResult } from '@/api/excel'
import type { Course, Teacher } from '@/types'

const courses = ref<Course[]>([])
const teachers = ref<Teacher[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formData = ref<Partial<Course>>({})
const formRef = ref()
const searchKeyword = ref('')
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
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  courseCode: [{ required: true, message: '请输入课程编码', trigger: 'blur' }]
}

const typeOptions = [
  { label: '必修', value: '必修' },
  { label: '选修', value: '选修' }
]

const categoryOptions = [
  '语言文学', '数理科学', '外语', '生命科学', '人文社科',
  '艺术', '体育', '信息技术', '综合实践'
].map(s => ({ label: s, value: s }))

const statusOptions = [
  { label: '启用', value: 1 },
  { label: '停用', value: 0 }
]

const getTeacherName = (teacherId?: number) => {
  if (!teacherId) return '-'
  const t = teachers.value.find(t => t.id === teacherId)
  return t ? t.name : '-'
}

const fetchCourses = async () => {
  loading.value = true
  try {
    const res: any = await courseApi.getList(searchKeyword.value)
    courses.value = res.data || []
    total.value = courses.value.length
  } catch {
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

const fetchTeachers = async () => {
  try {
    const res: any = await teacherApi.getList()
    teachers.value = (res.data || []).filter((t: Teacher) => t.status === 1)
  } catch {
    console.error('获取教师列表失败')
  }
}

const handleSearch = () => { currentPage.value = 1; fetchCourses() }
const handleRefresh = () => { currentPage.value = 1; fetchCourses() }
const handlePageChange = (page: number) => { currentPage.value = page }

// 计算当前页数据
const paginatedCourses = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return courses.value.slice(start, end)
})

const openAddDialog = () => {
  dialogTitle.value = '添加课程'
  formData.value = {
    courseName: '', courseCode: '', type: '必修', credit: undefined,
    totalHours: undefined, category: '', teacherId: undefined, description: '', status: 1
  }
  dialogVisible.value = true
}

const openEditDialog = (row: Course) => {
  dialogTitle.value = '编辑课程'
  formData.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        if (formData.value.id) {
          await courseApi.update(formData.value as Course)
        } else {
          await courseApi.create(formData.value as Course)
        }
        ElMessage.success('操作成功')
        dialogVisible.value = false
        fetchCourses()
      } catch {
        ElMessage.error('操作失败')
      }
    }
  })
}

const handleDelete = async (row: Course) => {
  try {
    await ElMessageBox.confirm(`确定要删除课程「${row.courseName}」吗？`, '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await courseApi.delete(row.id!)
    ElMessage.success('删除成功')
    fetchCourses()
  } catch {}
}

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
    const res = await courseExcelApi.importData(importFileList.value[0].raw)
    importResult.value = res.data
    if (res.data.errorCount === 0) ElMessage.success(`导入成功，共 ${res.data.successCount} 条`)
    fetchCourses()
  } catch { ElMessage.error('导入失败') }
  finally { importLoading.value = false }
}

onMounted(() => {
  fetchCourses()
  fetchTeachers()
})
</script>

<template>
  <div class="page-container">
    <div class="action-bar">
      <div class="left-actions">
        <el-input v-model="searchKeyword" placeholder="按课程名称、编码或分类搜索..." clearable class="search-input" @keyup.enter="handleSearch">
          <template #append>
            <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
          </template>
        </el-input>
        <el-button class="refresh-btn" @click="handleRefresh"><el-icon><Refresh /></el-icon></el-button>
      </div>
      <div class="right-actions">
        <div class="excel-btn-group">
          <el-button plain size="default" class="excel-btn" @click="openImportDialog"><el-icon><Upload /></el-icon>导入</el-button>
          <el-button plain size="default" class="excel-btn" @click="courseExcelApi.exportData()"><el-icon><Download /></el-icon>导出</el-button>
          <el-button plain size="default" class="excel-btn" @click="courseExcelApi.downloadTemplate()">模板</el-button>
        </div>
        <el-button type="primary" class="add-btn" @click="openAddDialog"><el-icon><Plus /></el-icon>添加课程</el-button>
      </div>
    </div>

    <div class="table-card">
      <el-table :data="paginatedCourses" v-loading="loading" stripe>
        <el-table-column prop="id" label="序号" width="60" align="center" />
        <el-table-column prop="courseName" label="课程名称" min-width="120">
          <template #default="{ row }">
            <div class="course-cell">
              <el-icon color="#667eea" size="18"><Notebook /></el-icon>
              <span>{{ row.courseName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="courseCode" label="课程编码" min-width="100">
          <template #default="{ row }">
            <span class="course-code">{{ row.courseCode }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" min-width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === '必修' ? 'danger' : 'success'" size="small">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="credit" label="学分" min-width="70" align="center">
          <template #default="{ row }">{{ row.credit || '-' }}</template>
        </el-table-column>
        <el-table-column prop="totalHours" label="课时" min-width="70" align="center">
          <template #default="{ row }">{{ row.totalHours || '-' }}</template>
        </el-table-column>
        <el-table-column prop="category" label="分类" min-width="90" align="center">
          <template #default="{ row }"><el-tag type="info" size="small">{{ row.category }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="teacherId" label="授课教师" min-width="100">
          <template #default="{ row }">{{ getTeacherName(row.teacherId) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
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

    <el-dialog v-model="dialogVisible" width="580px" :show-close="false">
      <template #header>
        <div class="dialog-header">
          <span class="dialog-title">{{ dialogTitle }}</span>
          <span class="close-btn" @click="dialogVisible = false">&times;</span>
        </div>
      </template>

      <el-form ref="formRef" :model="formData" :rules="rules" class="dialog-form">
        <div class="form-row">
          <el-form-item prop="courseName" class="form-item-half">
            <label><el-icon><Notebook /></el-icon>课程名称</label>
            <el-input v-model="formData.courseName" placeholder="请输入课程名称" />
          </el-form-item>
          <el-form-item prop="courseCode" class="form-item-half">
            <label><el-icon><Ticket /></el-icon>课程编码</label>
            <el-input v-model="formData.courseCode" placeholder="如: YW101" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item prop="type" class="form-item-third">
            <label><el-icon><TrendCharts /></el-icon>类型</label>
            <el-select v-model="formData.type" placeholder="请选择" style="width: 100%">
              <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item prop="credit" class="form-item-third">
            <label><el-icon><TrendCharts /></el-icon>学分</label>
            <el-input-number v-model="formData.credit" :min="0" :max="20" :precision="1" :step="0.5" style="width: 100%" />
          </el-form-item>
          <el-form-item prop="totalHours" class="form-item-third">
            <label><el-icon><Clock /></el-icon>课时</label>
            <el-input-number v-model="formData.totalHours" :min="0" :max="500" :step="2" style="width: 100%" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item prop="category" class="form-item-half">
            <label>课程分类</label>
            <el-select v-model="formData.category" placeholder="请选择" style="width: 100%">
              <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item prop="teacherId" class="form-item-half">
            <label><el-icon><User /></el-icon>授课教师</label>
            <el-select v-model="formData.teacherId" placeholder="请选择" style="width: 100%" clearable filterable>
              <el-option v-for="t in teachers" :key="t.id" :label="`${t.name} (${t.subject})`" :value="t.id!" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item prop="description" class="form-item">
          <label><el-icon><Document /></el-icon>课程描述</label>
          <el-input v-model="formData.description" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
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

    <!-- 导入对话框 -->
    <el-dialog v-model="importDialogVisible" title="导入课程" width="520px">
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
  background: #fff; border-radius: 12px; padding: 16px 20px; margin-bottom: 20px;
  display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
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
.course-cell { display: flex; align-items: center; gap: 8px; }
.course-code { font-family: monospace; color: #667eea; font-size: 13px; }
.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: 16px; }


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
