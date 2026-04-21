import request from '@/utils/request'
import type { ApiResponse, Teacher } from '@/types'

export const teacherApi = {
  getList: (keyword?: string) => {
    const params = keyword ? `?keyword=${encodeURIComponent(keyword)}` : ''
    return request.get<ApiResponse<Teacher[]>>(`/api/teachers${params}`)
  },

  getTeacherById: (id: number) => {
    return request.get<ApiResponse<Teacher>>(`/api/teachers/${id}`)
  },

  create: (data: Partial<Teacher>) => {
    return request.post<ApiResponse<Teacher>>('/api/teachers', data)
  },

  update: (data: Partial<Teacher>) => {
    return request.put<ApiResponse<Teacher>>(`/api/teachers/${data.id}`, data)
  },

  delete: (id: number) => {
    return request.delete(`/api/teachers/${id}`)
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
