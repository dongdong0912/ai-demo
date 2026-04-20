export interface Teacher {
  id?: number;
  name: string;
  gender: string;
  subject: string;
  phone: string;
  email: string;
  remark: string;
  photo?: string;
  status: number;
  createTime?: string;
  updateTime?: string;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface UploadResponse {
  url: string;
  filename: string;
}
