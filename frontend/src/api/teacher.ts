import request from '../utils/request';
import { Teacher } from '../types/teacher';

export const getTeachers = async (): Promise<Teacher[]> => {
  const res = await request.get<any, { code: number; message: string; data: Teacher[] }>('/api/teachers');
  return res.data || [];
};

export const getTeacherById = async (id: number): Promise<Teacher> => {
  const res = await request.get<any, { code: number; message: string; data: Teacher }>(`/api/teachers/${id}`);
  return res.data;
};

export const createTeacher = async (teacher: Partial<Teacher>): Promise<Teacher> => {
  const res = await request.post<any, { code: number; message: string; data: Teacher }>('/api/teachers', teacher);
  return res.data;
};

export const updateTeacher = async (id: number, teacher: Partial<Teacher>): Promise<Teacher> => {
  const res = await request.put<any, { code: number; message: string; data: Teacher }>(`/api/teachers/${id}`, teacher);
  return res.data;
};

export const deleteTeacher = async (id: number): Promise<void> => {
  await request.delete(`/api/teachers/${id}`);
};

export const uploadImage = async (file: File): Promise<{ url: string }> => {
  const formData = new FormData();
  formData.append('file', file);
  const res = await request.post<any, { code: number; message: string; data: { url: string } }>('/api/upload/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return res.data;
};
