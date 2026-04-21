import request from '@/utils/request'
import type { ApiResponse, User } from '@/types'

export const userApi = {
  getList: () => {
    return request.get<ApiResponse<User[]>>('/api/users')
  },

  getUserById: (id: number) => {
    return request.get<ApiResponse<User>>(`/api/users/${id}`)
  },

  create: (data: Partial<User>) => {
    return request.post<ApiResponse<User>>('/api/users', data)
  },

  update: (data: Partial<User>) => {
    return request.put<ApiResponse<User>>(`/api/users/${data.id}`, data)
  },

  delete: (id: number) => {
    return request.delete(`/api/users/${id}`)
  },

  getProfile: () => {
    return request.get<ApiResponse<User>>('/api/users/profile')
  },

  updateProfile: (data: { email?: string; phone?: string; avatar?: string }) => {
    return request.put<ApiResponse<User>>('/api/users/profile', data)
  },

  changePassword: (oldPassword: string, newPassword: string) => {
    return request.put<ApiResponse<void>>('/api/users/password', { oldPassword, newPassword })
  }
}
