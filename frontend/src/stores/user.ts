import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, UserInfo, LoginForm } from '@/types'
import { auth } from '@/utils/auth'
import { authApi } from '@/api/auth'
import { userApi } from '@/api/user'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  // State
  const token = ref<string>(auth.getToken() || '')
  const userInfo = ref<UserInfo | null>(auth.getUser())
  const profile = ref<User | null>(null)

  // Getters
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')
  const isTeacher = computed(() => userInfo.value?.role === 'TEACHER')

  // Actions
  async function login(form: LoginForm) {
    const res = await authApi.login(form)
    if (res.code === 200) {
      token.value = res.data.token
      userInfo.value = {
        id: res.data.id,
        username: res.data.username,
        role: res.data.role
      }
      auth.setToken(res.data.token)
      auth.setUser(userInfo.value)
      return true
    }
    return false
  }

  async function logout() {
    token.value = ''
    userInfo.value = null
    profile.value = null
    auth.logout()
    router.push('/login')
  }

  async function fetchProfile() {
    try {
      const res = await userApi.getProfile()
      if (res.code === 200) {
        profile.value = res.data
      }
    } catch (error) {
      console.error('获取用户信息失败', error)
    }
  }

  async function updateProfile(data: { email?: string; phone?: string; avatar?: string }) {
    const res = await userApi.updateProfile(data)
    if (res.code === 200) {
      profile.value = res.data
    }
    return res
  }

  async function changePassword(oldPassword: string, newPassword: string) {
    return await userApi.changePassword(oldPassword, newPassword)
  }

  return {
    token,
    userInfo,
    profile,
    isLoggedIn,
    isAdmin,
    isTeacher,
    login,
    logout,
    fetchProfile,
    updateProfile,
    changePassword
  }
})
