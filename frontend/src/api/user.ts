import axios from 'axios';
import { User, ApiResponse } from '../types/user';

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
});

export const getUsers = async (): Promise<User[]> => {
  const response = await api.get<ApiResponse<User[]>>('/users');
  return response.data.data;
};

export const getUserById = async (id: number): Promise<User> => {
  const response = await api.get<ApiResponse<User>>(`/users/${id}`);
  return response.data.data;
};

export const createUser = async (user: User): Promise<User> => {
  const response = await api.post<ApiResponse<User>>('/users', user);
  return response.data.data;
};

export const updateUser = async (id: number, user: User): Promise<User> => {
  const response = await api.put<ApiResponse<User>>(`/users/${id}`, user);
  return response.data.data;
};

export const deleteUser = async (id: number): Promise<void> => {
  await api.delete(`/users/${id}`);
};
