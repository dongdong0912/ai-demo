import request from '@/utils/request'

export interface Score {
  id?: number
  studentId: number
  courseId: number
  score: number
  examType?: string
  examDate?: string
  remark?: string
  status?: number
  createTime?: string
  updateTime?: string
  // 附加信息
  studentName?: string
  studentNo?: string
  classId?: number
  courseName?: string
  courseCode?: string
}

export const scoreApi = {
  getList: (params?: { keyword?: string; courseId?: number; studentId?: number }) => {
    const searchParams = new URLSearchParams()
    if (params?.keyword) searchParams.append('keyword', params.keyword)
    if (params?.courseId) searchParams.append('courseId', String(params.courseId))
    if (params?.studentId) searchParams.append('studentId', String(params.studentId))
    const query = searchParams.toString()
    return request.get<Score[]>(`/api/scores${query ? '?' + query : ''}`)
  },

  getScoreById: (id: number) => {
    return request.get<Score>(`/api/scores/${id}`)
  },

  getScoresByStudent: (studentId: number) => {
    return request.get<Score[]>(`/api/scores/student/${studentId}`)
  },

  getScoresByCourse: (courseId: number) => {
    return request.get<Score[]>(`/api/scores/course/${courseId}`)
  },

  create: (data: Partial<Score>) => {
    return request.post<Score>('/api/scores', data)
  },

  update: (data: Partial<Score>) => {
    return request.put<Score>(`/api/scores/${data.id}`, data)
  },

  delete: (id: number) => {
    return request.delete(`/api/scores/${id}`)
  },

  // 统计分析
  getStatistics: (params?: {
    schoolYear?: string
    semester?: string
    courseId?: number
    classId?: number
    gradeId?: number
    examType?: string
  }) => {
    const searchParams = new URLSearchParams()
    if (params?.schoolYear) searchParams.append('schoolYear', params.schoolYear)
    if (params?.semester) searchParams.append('semester', params.semester)
    if (params?.courseId) searchParams.append('courseId', String(params.courseId))
    if (params?.classId) searchParams.append('classId', String(params.classId))
    if (params?.gradeId) searchParams.append('gradeId', String(params.gradeId))
    if (params?.examType) searchParams.append('examType', params.examType)
    const query = searchParams.toString()
    return request.get(`/api/scores/statistics${query ? '?' + query : ''}`)
  },

  // 成绩排名
  getRanking: (params?: {
    schoolYear?: string
    semester?: string
    courseId?: number
    classId?: number
    gradeId?: number
    examType?: string
    orderBy?: string
  }) => {
    const searchParams = new URLSearchParams()
    if (params?.schoolYear) searchParams.append('schoolYear', params.schoolYear)
    if (params?.semester) searchParams.append('semester', params.semester)
    if (params?.courseId) searchParams.append('courseId', String(params.courseId))
    if (params?.classId) searchParams.append('classId', String(params.classId))
    if (params?.gradeId) searchParams.append('gradeId', String(params.gradeId))
    if (params?.examType) searchParams.append('examType', params.examType)
    if (params?.orderBy) searchParams.append('orderBy', params.orderBy)
    const query = searchParams.toString()
    return request.get(`/api/scores/ranking${query ? '?' + query : ''}`)
  },

  // 成绩趋势
  getTrend: (params?: {
    schoolYear?: string
    semester?: string
    courseId?: number
    studentId?: number
    classId?: number
  }) => {
    const searchParams = new URLSearchParams()
    if (params?.schoolYear) searchParams.append('schoolYear', params.schoolYear)
    if (params?.semester) searchParams.append('semester', params.semester)
    if (params?.courseId) searchParams.append('courseId', String(params.courseId))
    if (params?.studentId) searchParams.append('studentId', String(params.studentId))
    if (params?.classId) searchParams.append('classId', String(params.classId))
    const query = searchParams.toString()
    return request.get(`/api/scores/trend${query ? '?' + query : ''}`)
  },

  // 多维度查询
  queryScores: (params?: {
    schoolYear?: string
    semester?: string
    courseId?: number
    studentId?: number
    classId?: number
    keyword?: string
    examType?: string
    page?: number
    size?: number
  }) => {
    const searchParams = new URLSearchParams()
    if (params?.schoolYear) searchParams.append('schoolYear', params.schoolYear)
    if (params?.semester) searchParams.append('semester', params.semester)
    if (params?.courseId) searchParams.append('courseId', String(params.courseId))
    if (params?.studentId) searchParams.append('studentId', String(params.studentId))
    if (params?.classId) searchParams.append('classId', String(params.classId))
    if (params?.keyword) searchParams.append('keyword', params.keyword)
    if (params?.examType) searchParams.append('examType', params.examType)
    if (params?.page) searchParams.append('page', String(params.page))
    if (params?.size) searchParams.append('size', String(params.size))
    const query = searchParams.toString()
    return request.get(`/api/scores/query${query ? '?' + query : ''}`)
  },

  // 学生成绩单
  getStudentReport: (studentId: number, params?: { schoolYear?: string; semester?: string }) => {
    const searchParams = new URLSearchParams()
    if (params?.schoolYear) searchParams.append('schoolYear', params.schoolYear)
    if (params?.semester) searchParams.append('semester', params.semester)
    const query = searchParams.toString()
    return request.get(`/api/scores/report/${studentId}${query ? '?' + query : ''}`)
  }
}
