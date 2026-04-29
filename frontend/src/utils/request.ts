import axios, { AxiosInstance, AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import { auth } from './auth'
import router from '@/router'

const request = axios.create({
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const token = auth.getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error: AxiosError<{ code?: number; message?: string }>) => {
    // 确保 loading 状态被关闭
    const loadingEl = document.querySelector('.el-loading-mask')
    if (loadingEl) {
      loadingEl.remove()
    }
    document.body.style.overflow = 'auto'
    document.body.style.position = 'static'

    if (error.response) {
      const { status, data } = error.response

      switch (status) {
        case 401:
          ElMessage.error(data?.message || '未登录或登录已过期')
          auth.logout()
          router.push('/login')
          break
        case 403:
          ElMessage.error('没有权限访问该资源')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(data?.message || '请求失败')
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请稍后重试')
    } else if (error.request) {
      ElMessage.error('网络连接失败，请检查网络')
    } else {
      ElMessage.error('请求配置错误')
    }
    return Promise.reject(error)
  }
)

export default request as AxiosInstance
