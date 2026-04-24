import request from '@/utils/request'
import type { Student } from '@/types'

export const studentApi = {
  getList: (keyword?: string) => {
    const params = keyword ? `?keyword=${encodeURIComponent(keyword)}` : ''
    return request.get<Student[]>(`/api/students${params}`)
  },

  getStudentById: (id: number) => {
    return request.get<Student>(`/api/students/${id}`)
  },

  getStudentsByClass: (classId: number) => {
    return request.get<Student[]>(`/api/students/class/${classId}`)
  },

  create: (data: Partial<Student>) => {
    return request.post<Student>('/api/students', data)
  },

  update: (data: Partial<Student>) => {
    return request.put<Student>(`/api/students/${data.id}`, data)
  },

  delete: (id: number) => {
    return request.delete(`/api/students/${id}`)
  },

  uploadImage: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post<{ url: string }>('/api/upload/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}
