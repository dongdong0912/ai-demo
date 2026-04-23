import request from '@/utils/request'
import type { ApiResponse, Student } from '@/types'

export const studentApi = {
  getList: (keyword?: string) => {
    const params = keyword ? `?keyword=${encodeURIComponent(keyword)}` : ''
    return request.get<ApiResponse<Student[]>>(`/api/students${params}`)
  },

  getStudentById: (id: number) => {
    return request.get<ApiResponse<Student>>(`/api/students/${id}`)
  },

  getStudentsByClass: (classId: number) => {
    return request.get<ApiResponse<Student[]>>(`/api/students/class/${classId}`)
  },

  create: (data: Partial<Student>) => {
    return request.post<ApiResponse<Student>>('/api/students', data)
  },

  update: (data: Partial<Student>) => {
    return request.put<ApiResponse<Student>>(`/api/students/${data.id}`, data)
  },

  delete: (id: number) => {
    return request.delete(`/api/students/${id}`)
  },

  uploadImage: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post<ApiResponse<{ url: string }>>('/api/upload/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}
