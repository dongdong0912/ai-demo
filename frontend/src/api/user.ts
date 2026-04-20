import request from '../utils/request';
import { User } from '../types/user';

export const getUsers = async (): Promise<User[]> => {
  const res = await request.get<any, { code: number; message: string; data: User[] }>('/api/users');
  return res.data || [];
};

export const getUserById = async (id: number): Promise<User> => {
  const res = await request.get<any, { code: number; message: string; data: User }>(`/api/users/${id}`);
  return res.data;
};

export const createUser = async (user: Partial<User>): Promise<User> => {
  const res = await request.post<any, { code: number; message: string; data: User }>('/api/users', user);
  return res.data;
};

export const updateUser = async (id: number, user: Partial<User>): Promise<User> => {
  const res = await request.put<any, { code: number; message: string; data: User }>(`/api/users/${id}`, user);
  return res.data;
};

export const deleteUser = async (id: number): Promise<void> => {
  await request.delete(`/api/users/${id}`);
};

// 个人中心相关 API
export const getProfile = async (): Promise<User> => {
  const res = await request.get<any, { code: number; message: string; data: User }>('/api/users/profile');
  if (res.code !== 200) {
    throw new Error(res.message);
  }
  return res.data;
};

export const updateProfile = async (params: { email?: string; phone?: string; avatar?: string }): Promise<User> => {
  const res = await request.put<any, { code: number; message: string; data: User }>('/api/users/profile', params);
  if (res.code !== 200) {
    throw new Error(res.message);
  }
  return res.data;
};

export const changePassword = async (oldPassword: string, newPassword: string): Promise<void> => {
  const res = await request.put<any, { code: number; message: string }>('/api/users/password', {
    oldPassword,
    newPassword,
  });
  if (res.code !== 200) {
    throw new Error(res.message);
  }
};
