<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const activeTab = ref('info')
const loading = ref(false)

const profileForm = reactive({
  username: '',
  email: '',
  phone: '',
  role: '',
  department: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const loadUserInfo = () => {
  if (userStore.userInfo) {
    profileForm.username = userStore.userInfo.username || ''
    profileForm.email = userStore.userInfo.email || ''
    profileForm.phone = userStore.userInfo.phone || ''
    profileForm.role = userStore.isAdmin ? '管理员' : '普通用户'
    profileForm.department = '技术部'
  }
}

const handleUpdateProfile = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 800))
    ElMessage.success('保存成功')
  } catch {
    ElMessage.error('保存失败')
  } finally {
    loading.value = false
  }
}

const handleUpdatePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次密码不一致')
    return
  }
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 800))
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch {
    ElMessage.error('修改失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => loadUserInfo())
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">个人中心</h2>
    </div>

    <div class="profile-layout">
      <div class="profile-sidebar">
        <div class="user-card">
          <el-avatar :size="72" style="background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%); font-size: 28px;">
            {{ userStore.userInfo?.username?.charAt(0).toUpperCase() || 'U' }}
          </el-avatar>
          <h3 class="user-name">{{ userStore.userInfo?.username }}</h3>
          <el-tag :type="userStore.isAdmin ? 'danger' : 'info'">{{ profileForm.role }}</el-tag>
        </div>
      </div>

      <div class="profile-content">
        <el-card>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本信息" name="info">
              <el-form :model="profileForm" label-width="80px" class="profile-form">
                <el-form-item label="用户名"><el-input v-model="profileForm.username" disabled /></el-form-item>
                <el-form-item label="邮箱"><el-input v-model="profileForm.email" placeholder="请输入邮箱" /></el-form-item>
                <el-form-item label="手机号"><el-input v-model="profileForm.phone" placeholder="请输入手机号" /></el-form-item>
                <el-form-item label="角色"><el-input v-model="profileForm.role" disabled /></el-form-item>
                <el-form-item label="部门"><el-input v-model="profileForm.department" /></el-form-item>
                <el-form-item><el-button type="primary" :loading="loading" @click="handleUpdateProfile">保存修改</el-button></el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane label="修改密码" name="password">
              <el-form :model="passwordForm" label-width="80px" class="profile-form">
                <el-form-item label="原密码"><el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入原密码" /></el-form-item>
                <el-form-item label="新密码"><el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" /></el-form-item>
                <el-form-item label="确认密码"><el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入" /></el-form-item>
                <el-form-item><el-button type="primary" :loading="loading" @click="handleUpdatePassword">修改密码</el-button></el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-container { padding: 0; }
.page-header { margin-bottom: 12px; }
.page-title { font-size: 18px; font-weight: 600; color: #303133; margin: 0; }
.profile-layout { display: flex; gap: 16px; }
.profile-sidebar { width: 200px; flex-shrink: 0; }
.user-card { background: #fff; border-radius: 8px; padding: 24px; text-align: center; }
.user-name { font-size: 16px; font-weight: 600; color: #303133; margin: 12px 0; }
.profile-content { flex: 1; }
.profile-form { max-width: 400px; padding-top: 12px; }
</style>
