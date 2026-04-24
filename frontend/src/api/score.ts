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
  }
}
