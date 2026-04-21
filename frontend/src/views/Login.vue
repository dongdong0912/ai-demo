<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import type { LoginForm } from '@/types'

const router = useRouter()
const userStore = useUserStore()
const loginForm = reactive<LoginForm>({ username: '', password: '' })
const loading = ref(false)

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const success = await userStore.login(loginForm)
    if (success) {
      ElMessage.success('登录成功')
      router.push('/user')
    }
  } catch (error) {
    console.error('登录失败', error)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-container">
    <!-- 左侧品牌区 -->
    <div class="brand-section">
      <div class="brand-content">
        <div class="brand-icon">
          <svg viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect width="64" height="64" rx="16" fill="rgba(255,255,255,0.2)"/>
            <path d="M32 16L44 24V40L32 48L20 40V24L32 16Z" stroke="white" stroke-width="2" fill="rgba(255,255,255,0.3)"/>
            <circle cx="32" cy="32" r="6" fill="white"/>
          </svg>
        </div>
        <h1 class="brand-title">智慧管理系统</h1>
        <p class="brand-subtitle">Intelligent Management System</p>
        <div class="brand-features">
          <div class="feature-item">
            <span class="feature-dot"></span>
            <span>高效便捷的管理体验</span>
          </div>
          <div class="feature-item">
            <span class="feature-dot"></span>
            <span>安全可靠的数据保障</span>
          </div>
          <div class="feature-item">
            <span class="feature-dot"></span>
            <span>智能化的业务流程</span>
          </div>
        </div>
      </div>
      <div class="brand-decoration">
        <div class="decoration-circle circle-1"></div>
        <div class="decoration-circle circle-2"></div>
        <div class="decoration-circle circle-3"></div>
      </div>
    </div>

    <!-- 右侧登录区 -->
    <div class="login-section">
      <div class="login-box">
        <div class="login-header">
          <h2>欢迎回来</h2>
          <p>请登录您的账号</p>
        </div>

        <el-form class="login-form" @submit.prevent="handleLogin">
          <div class="form-item">
            <label>用户名</label>
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              size="large"
              :prefix-icon="User"
              clearable
            />
          </div>
          <div class="form-item">
            <label>密码</label>
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </div>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form>

        <div class="login-tip">默认账号: <strong>admin</strong> / <strong>123456</strong></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
}

/* 左侧品牌区 */
.brand-section {
  flex: 1;
  background: linear-gradient(135deg, #1a1f36 0%, #2d3555 50%, #1a1f36 100%);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.brand-content {
  position: relative;
  z-index: 2;
  text-align: center;
  padding: 40px;
}

.brand-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 32px;
}

.brand-icon svg {
  width: 100%;
  height: 100%;
}

.brand-title {
  font-size: 36px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 12px 0;
  letter-spacing: 2px;
}

.brand-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0 0 48px 0;
  letter-spacing: 1px;
}

.brand-features {
  text-align: left;
  display: inline-block;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 15px;
}

.feature-dot {
  width: 8px;
  height: 8px;
  background: #409EFF;
  border-radius: 50%;
  box-shadow: 0 0 12px #409EFF;
}

/* 装饰圆圈 */
.brand-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.circle-1 {
  width: 400px;
  height: 400px;
  top: -100px;
  right: -100px;
}

.circle-2 {
  width: 300px;
  height: 300px;
  bottom: -50px;
  left: -50px;
}

.circle-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(64, 158, 255, 0.05);
  border: none;
}

/* 右侧登录区 */
.login-section {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  padding: 40px;
}

.login-box {
  width: 100%;
  max-width: 360px;
}

.login-header {
  margin-bottom: 40px;
}

.login-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.login-header p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

/* 表单样式 */
.login-form {
  margin-bottom: 24px;
}

.form-item {
  margin-bottom: 24px;
}

.form-item label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  margin-bottom: 8px;
}

.login-form :deep(.el-input__wrapper) {
  padding: 14px 16px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #e8e8e8;
  transition: all 0.3s;
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: #409EFF;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: #409EFF;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.15);
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 10px;
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
  border: none;
  margin-top: 8px;
  transition: all 0.3s;
}

.login-btn:hover {
  background: linear-gradient(135deg, #66b1ff 0%, #409EFF 100%);
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.35);
  transform: translateY(-1px);
}

.login-tip {
  text-align: center;
  font-size: 13px;
  color: #909399;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  border: 1px dashed #e8e8e8;
}

.login-tip strong {
  color: #409EFF;
}

/* 响应式 */
@media (max-width: 900px) {
  .brand-section {
    display: none;
  }
  .login-section {
    width: 100%;
    flex: 1;
  }
}
</style>
