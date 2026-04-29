<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Fold, Expand, ArrowDown, UserFilled, SwitchButton } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const emit = defineEmits<{
  collapse: [collapsed: boolean]
}>()

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapsed = ref(false)

const pageTitle = computed(() => {
  const map: Record<string, string> = {
    '/dashboard': '首页',
    '/user': '用户管理',
    '/teacher': '教师管理',
    '/student': '学生管理',
    '/class': '班级管理',
    '/course': '课程管理',
    '/score': '成绩管理',
    '/score-analysis': '成绩分析',
    '/profile': '个人中心'
  }
  return map[route.path] || ''
})

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
  emit('collapse', isCollapsed.value)
}

const goToProfile = () => router.push('/profile')

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    userStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  } catch {}
}
</script>

<template>
  <div class="app-header">
    <div class="header-left">
      <el-button :icon="isCollapsed ? Expand : Fold" @click="toggleCollapse" text />
      <span class="page-title">{{ pageTitle }}</span>
    </div>

    <div class="header-right">
      <el-dropdown trigger="click">
        <div class="user-info">
          <el-avatar :size="32" style="background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);">
            {{ userStore.userInfo?.username?.charAt(0).toUpperCase() || 'U' }}
          </el-avatar>
          <span class="username">{{ userStore.userInfo?.username }}</span>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="goToProfile">
              <el-icon><UserFilled /></el-icon>个人中心
            </el-dropdown-item>
            <el-dropdown-item divided @click="handleLogout" style="color: #F56C6C;">
              <el-icon><SwitchButton /></el-icon>退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<style scoped>
.app-header {
  height: 56px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  border-bottom: 1px solid #ebeef5;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  border-radius: 6px;
  cursor: pointer;
}

.user-info:hover {
  background: #f5f7fa;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  font-size: 13px;
}
</style>
