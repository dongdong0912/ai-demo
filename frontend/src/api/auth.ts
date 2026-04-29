import axios from 'axios'
import type { LoginForm, LoginResponse, UserInfo, ApiResponse } from '@/types'
import { auth } from '@/utils/auth'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use((config) => {
  const token = auth.getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    if (error.response?.status === 401) {
      auth.logout()
    }
    return Promise.reject(error)
  }
)

export const authApi = {
  login: (data: LoginForm) => {
    return request.post<ApiResponse<LoginResponse>>('/auth/login', data).then((res) => {
      if (res.code === 200) {
        const { token, id, username, role } = res.data
        auth.setToken(token)
        auth.setUser({ id, username, role })
      }
      return res
    })
  },

  register: (data: { username: string; password: string }) => {
    return request.post('/auth/register', data)
  },

  getUserInfo: () => {
    return request.get<ApiResponse<UserInfo>>('/auth/userinfo')
  },

  logout: () => {
    auth.logout()
  }
}
