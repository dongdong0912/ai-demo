import axios from 'axios'
import type { LoginForm, LoginResponse, UserInfo, ApiResponse } from '@/types'
import { auth } from '@/utils/auth'

const request = axios.create({
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

request.interceptors.request.use((config) => {
  const token = auth.getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export const authApi = {
  login: (data: LoginForm) => {
    return request.post<ApiResponse<LoginResponse>>('/api/auth/login', data).then((res) => {
      const response = res as unknown as ApiResponse<LoginResponse>
      if (response.code === 200) {
        const { token, id, username, role } = response.data
        auth.setToken(token)
        auth.setUser({ id, username, role })
      }
      return response
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
