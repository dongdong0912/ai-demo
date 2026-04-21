export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface User {
  id?: number
  username: string
  email?: string
  phone?: string
  avatar?: string
  role?: string
  status: number
  createTime?: string
  updateTime?: string
  lastLoginTime?: string
  loginCount?: number
}

export interface Teacher {
  id?: number
  name: string
  gender: string
  subject: string
  phone: string
  email: string
  remark: string
  photo?: string
  status: number
  createTime?: string
  updateTime?: string
}

export interface LoginForm {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  username: string
  role: string
  id: number
}

export interface UserInfo {
  id: number
  username: string
  role: string
  email?: string
  phone?: string
}
