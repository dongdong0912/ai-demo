export interface User {
  id?: number;
  username: string;
  email: string;
  phone: string;
  status: number;
  createTime?: string;
  updateTime?: string;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}
