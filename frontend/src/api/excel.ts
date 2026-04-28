import request from '@/utils/request'

export interface ImportResult {
  totalCount: number
  successCount: number
  errorCount: number
  errors: string[]
}

export interface ScoreExportParams {
  keyword?: string
  courseId?: number
  studentId?: number
  examType?: string
}

/**
 * 文件下载（导出/模板）
 */
function downloadFile(url: string, filename: string) {
  fetch(url)
    .then(res => {
      if (!res.ok) throw new Error('下载失败')
      return res.blob()
    })
    .then(blob => {
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = filename
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    })
    .catch(() => {
      window.open(url, '_blank')
    })
}

/**
 * 带参数的文件下载
 */
function downloadFileWithParams(url: string, filename: string, params?: Record<string, any>) {
  if (params) {
    const searchParams = new URLSearchParams()
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        searchParams.append(key, String(value))
      }
    })
    const queryString = searchParams.toString()
    if (queryString) {
      url = url + '?' + queryString
    }
  }
  downloadFile(url, filename)
}

/**
 * 文件上传（导入）
 */
function uploadExcel(url: string, file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<ImportResult>(url, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// ==================== 用户 ====================
export const userExcelApi = {
  exportData: () => downloadFile('/api/excel/export/users', '用户列表.xlsx'),
  downloadTemplate: () => downloadFile('/api/excel/template/users', '用户导入模板.xlsx'),
  importData: (file: File) => uploadExcel('/api/excel/import/users', file),
}

// ==================== 教师 ====================
export const teacherExcelApi = {
  exportData: () => downloadFile('/api/excel/export/teachers', '教师列表.xlsx'),
  downloadTemplate: () => downloadFile('/api/excel/template/teachers', '教师导入模板.xlsx'),
  importData: (file: File) => uploadExcel('/api/excel/import/teachers', file),
}

// ==================== 学生 ====================
export const studentExcelApi = {
  exportData: () => downloadFile('/api/excel/export/students', '学生列表.xlsx'),
  downloadTemplate: () => downloadFile('/api/excel/template/students', '学生导入模板.xlsx'),
  importData: (file: File) => uploadExcel('/api/excel/import/students', file),
}

// ==================== 班级 ====================
export const classExcelApi = {
  exportData: () => downloadFile('/api/excel/export/classes', '班级列表.xlsx'),
  downloadTemplate: () => downloadFile('/api/excel/template/classes', '班级导入模板.xlsx'),
  importData: (file: File) => uploadExcel('/api/excel/import/classes', file),
}

// ==================== 课程 ====================
export const courseExcelApi = {
  exportData: () => downloadFile('/api/excel/export/courses', '课程列表.xlsx'),
  downloadTemplate: () => downloadFile('/api/excel/template/courses', '课程导入模板.xlsx'),
  importData: (file: File) => uploadExcel('/api/excel/import/courses', file),
}

// ==================== 成绩 ====================
export const scoreExcelApi = {
  exportData: (params?: ScoreExportParams) => downloadFileWithParams('/api/excel/export/scores', '成绩列表.xlsx', params),
  downloadTemplate: () => downloadFile('/api/excel/template/scores', '成绩导入模板.xlsx'),
  importData: (file: File) => uploadExcel('/api/excel/import/scores', file),
}
