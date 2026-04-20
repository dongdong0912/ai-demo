import request from '../utils/request';
import { auth, UserInfo } from '../utils/auth';

interface LoginResponse {
  token: string;
  username: string;
  role: string;
  id: number;
}

export const authApi = {
  // 登录
  login: (username: string, password: string) => {
    return request.post<any, { code: number; message: string; data: LoginResponse }>('/api/auth/login', {
      username,
      password,
    }).then((res) => {
      if (res.code === 200) {
        // 保存 Token 和用户信息
        auth.setToken(res.data.token);
        auth.setUser({
          id: res.data.id,
          username: res.data.username,
          role: res.data.role,
        });
      }
      return res;
    });
  },

  // 注册
  register: (username: string, password: string) => {
    return request.post('/api/auth/register', { username, password });
  },

  // 获取当前用户信息
  getUserInfo: () => {
    return request.get<any, { code: number; message: string; data: UserInfo }>('/api/auth/userinfo');
  },

  // 退出登录
  logout: () => {
    auth.logout();
  },
};
