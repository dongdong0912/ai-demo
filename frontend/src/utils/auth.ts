const TOKEN_KEY = 'auth_token';
const USER_KEY = 'auth_user';

export interface UserInfo {
  id: number;
  username: string;
  role: string;
  email?: string;
  phone?: string;
}

export const auth = {
  // 获取 Token
  getToken: (): string | null => {
    return localStorage.getItem(TOKEN_KEY);
  },

  // 设置 Token
  setToken: (token: string): void => {
    localStorage.setItem(TOKEN_KEY, token);
  },

  // 移除 Token
  removeToken: (): void => {
    localStorage.removeItem(TOKEN_KEY);
  },

  // 获取用户信息
  getUser: (): UserInfo | null => {
    const userStr = localStorage.getItem(USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
  },

  // 设置用户信息
  setUser: (user: UserInfo): void => {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  },

  // 移除用户信息
  removeUser: (): void => {
    localStorage.removeItem(USER_KEY);
  },

  // 判断是否已登录
  isAuthenticated: (): boolean => {
    return !!localStorage.getItem(TOKEN_KEY);
  },

  // 退出登录
  logout: (): void => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  }
};
