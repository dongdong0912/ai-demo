import request from '@/utils/request'
import type { ApiResponse, GradeClass } from '@/types'

export interface GradeClassWithCount extends GradeClass {
  studentCount: number
}

export const classApi = {
  getList: () => {
    return request.get<ApiResponse<GradeClass[]>>('/api/classes')
  },

  getListWithCount: () => {
    return request.get<ApiResponse<GradeClassWithCount[]>>('/api/classes/with-count')
  },

  getClassById: (id: number) => {
    return request.get<ApiResponse<GradeClass>>(`/api/classes/${id}`)
  },

  create: (data: Partial<GradeClass>) => {
    return request.post<ApiResponse<GradeClass>>('/api/classes', data)
  },

  update: (data: Partial<GradeClass>) => {
    return request.put<ApiResponse<GradeClass>>(`/api/classes/${data.id}`, data)
  },

  delete: (id: number) => {
    return request.delete(`/api/classes/${id}`)
  }
}
