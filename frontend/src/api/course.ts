import request from '@/utils/request'
import type { Course } from '@/types'

export const courseApi = {
  getList: (keyword?: string) => {
    const params = keyword ? `?keyword=${encodeURIComponent(keyword)}` : ''
    return request.get<Course[]>(`/api/courses${params}`)
  },

  getCourseById: (id: number) => {
    return request.get<Course>(`/api/courses/${id}`)
  },

  create: (data: Partial<Course>) => {
    return request.post<Course>('/api/courses', data)
  },

  update: (data: Partial<Course>) => {
    return request.put<Course>(`/api/courses/${data.id}`, data)
  },

  delete: (id: number) => {
    return request.delete(`/api/courses/${id}`)
  }
}
