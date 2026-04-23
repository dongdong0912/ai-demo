export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface User {
  id?: number
  username: string
  realName?: string
  gender?: string
  email?: string
  phone?: string
  department?: string
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
  gender?: string
  subject: string
  title?: string
  idCard?: string
  phone?: string
  email?: string
  address?: string
  birthDate?: string
  entryDate?: string
  remark?: string
  photo?: string
  status: number
  createTime?: string
  updateTime?: string
}

export interface GradeClass {
  id?: number
  className: string
  gradeName: string
  headTeacherId?: number
  maxStudents?: number
  status: number
  createTime?: string
  updateTime?: string
}

export interface Student {
  id?: number
  studentNo: string
  name: string
  gender?: string
  idCard?: string
  classId?: number
  phone?: string
  parentPhone?: string
  parentName?: string
  address?: string
  birthDate?: string
  nation?: string
  nativePlace?: string
  enrollmentDate?: string
  remark?: string
  photo?: string
  status: number
  createTime?: string
  updateTime?: string
}

export interface DashboardStats {
  totalUsers: number
  totalTeachers: number
  totalStudents: number
  totalClasses: number
  activeStudents: number
  activeTeachers: number
  activeClasses: number
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
