import request from '@/utils/request'
import type { User } from '@/types'

export const userApi = {
  getList: () => {
    return request.get<User[]>('/api/users')
  },

  getUserById: (id: number) => {
    return request.get<User>(`/api/users/${id}`)
  },

  create: (data: Partial<User>) => {
    return request.post<User>('/api/users', data)
  },

  update: (data: Partial<User>) => {
    return request.put<User>(`/api/users/${data.id}`, data)
  },

  delete: (id: number) => {
    return request.delete(`/api/users/${id}`)
  },

  getProfile: () => {
    return request.get<User>('/api/users/profile')
  },

  updateProfile: (data: { email?: string; phone?: string; avatar?: string }) => {
    return request.put<User>('/api/users/profile', data)
  },

  changePassword: (oldPassword: string, newPassword: string) => {
    return request.put<void>('/api/users/password', { oldPassword, newPassword })
  }
}
