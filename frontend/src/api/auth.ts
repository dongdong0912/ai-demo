import request from '@/utils/request'
import type { ApiResponse, LoginForm, LoginResponse, UserInfo } from '@/types'
import { auth } from '@/utils/auth'

export const authApi = {
  login: (data: LoginForm) => {
    return request.post<ApiResponse<LoginResponse>>('/api/auth/login', data).then((res) => {
      if (res.code === 200) {
        const { token, id, username, role } = res.data
        auth.setToken(token)
        auth.setUser({ id, username, role })
      }
      return res
    })
  },

  register: (data: { username: string; password: string }) => {
    return request.post('/api/auth/register', data)
  },

  getUserInfo: () => {
    return request.get<ApiResponse<UserInfo>>('/api/auth/userinfo')
  },

  logout: () => {
    auth.logout()
  }
}
