export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface User {
  id?: number;
  username: string;
  email?: string;
  phone?: string;
  avatar?: string;
  role?: string;
  status: number;
  createTime?: string;
  updateTime?: string;
  lastLoginTime?: string;
  loginCount?: number;
}
