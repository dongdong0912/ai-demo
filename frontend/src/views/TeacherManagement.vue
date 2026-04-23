<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search, CircleClose, Edit, Delete, User, Male, Female, Phone, Message, Reading, Document, Postcard, Location, Calendar, Upload, Download } from '@element-plus/icons-vue'
import { teacherApi } from '@/api/teacher'
import { teacherExcelApi, type ImportResult } from '@/api/excel'
import type { Teacher } from '@/types'

const teachers = ref<Teacher[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formData = ref<Partial<Teacher>>({})
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
    total.value = teachers.value.length
  } catch {
    ElMessage.error('获取教师列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { currentPage.value = 1; fetchTeachers() }
const handleRefresh = () => { currentPage.value = 1; fetchTeachers() }
const handlePageChange = (page: number) => { currentPage.value = page }

// 计算当前页数据
const paginatedTeachers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return teachers.value.slice(start, end)
})

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
    const res = await teacherExcelApi.importData(importFileList.value[0].raw)
    importResult.value = res.data
    if (res.data.errorCount === 0) ElMessage.success(`导入成功，共 ${res.data.successCount} 条`)
    fetchTeachers()
  } catch { ElMessage.error('导入失败') }
  finally { importLoading.value = false }
}

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
        <div class="excel-btn-group">
          <el-button plain size="default" class="excel-btn" @click="openImportDialog"><el-icon><Upload /></el-icon>导入</el-button>
          <el-button plain size="default" class="excel-btn" @click="teacherExcelApi.exportData()"><el-icon><Download /></el-icon>导出</el-button>
          <el-button plain size="default" class="excel-btn" @click="teacherExcelApi.downloadTemplate()">模板</el-button>
        </div>
        <el-button type="primary" class="add-btn" @click="openAddDialog"><el-icon><Plus /></el-icon>添加教师</el-button>
      </div>
    </div>

    <div class="table-card">
      <el-table :data="paginatedTeachers" v-loading="loading" stripe>
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
          <template #default="{ row }"><el-tag :type="row.gender === '男' ? undefined : 'warning'" size="small">{{ row.gender }}</el-tag></template>
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

    <!-- 导入对话框 -->
    <el-dialog v-model="importDialogVisible" title="导入教师" width="520px">
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
.search-input { width: 280px; }
.search-input :deep(.el-input__wrapper) { border-radius: 6px 0 0 6px; box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08); }
.search-input :deep(.el-input-group__append) { border-radius: 0 6px 6px 0; background: #667eea; border-color: #667eea; color: #fff; }
.search-input :deep(.el-input-group__append:hover) { background: #5a71d6; }
.refresh-btn { border-radius: 6px; padding: 8px 12px; }
.refresh-btn :deep(.el-icon) { font-size: 16px; }
.add-btn { border-radius: 6px; padding: 8px 16px; }
.table-card { background: #fff; border-radius: 12px; padding: 16px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04); }
.teacher-cell { display: flex; align-items: center; gap: 8px; }
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
