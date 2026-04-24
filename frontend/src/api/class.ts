import request from '@/utils/request'
import type { GradeClass } from '@/types'

export interface GradeClassWithCount extends GradeClass {
  studentCount: number
}

export const classApi = {
  getList: () => {
    return request.get<GradeClass[]>('/api/classes')
  },

  getListWithCount: () => {
    return request.get<GradeClassWithCount[]>('/api/classes/with-count')
  },

  getClassById: (id: number) => {
    return request.get<GradeClass>(`/api/classes/${id}`)
  },

  create: (data: Partial<GradeClass>) => {
    return request.post<GradeClass>('/api/classes', data)
  },

  update: (data: Partial<GradeClass>) => {
    return request.put<GradeClass>(`/api/classes/${data.id}`, data)
  },

  delete: (id: number) => {
    return request.delete(`/api/classes/${id}`)
  }
}
