<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { User, Reading, HomeFilled, School, UserFilled } from '@element-plus/icons-vue'

const props = defineProps<{
  collapsed: boolean
}>()

const router = useRouter()
const route = useRoute()

const menuItems = [
  { path: '/dashboard', name: 'Dashboard', title: '首页', icon: HomeFilled },
  { path: '/user', name: 'UserManagement', title: '用户管理', icon: User },
  { path: '/teacher', name: 'TeacherManagement', title: '教师管理', icon: Reading },
  { path: '/class', name: 'ClassManagement', title: '班级管理', icon: School },
  { path: '/student', name: 'StudentManagement', title: '学生管理', icon: UserFilled }
]

const activeMenu = computed(() => route.path)

const navigateTo = (path: string) => {
  router.push(path)
}
</script>

<template>
  <div class="side-menu">
    <div class="logo-container">
      <div class="logo-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M12 2L2 7l10 5 10-5-10-5z"/>
          <path d="M2 17l10 5 10-5"/>
          <path d="M2 12l10 5 10-5"/>
        </svg>
      </div>
      <span v-show="!collapsed" class="logo-text">管理系统</span>
    </div>

    <el-menu
      :default-active="activeMenu"
      :collapse="collapsed"
      :collapse-transition="false"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#fff"
      class="menu"
    >
      <el-menu-item
        v-for="item in menuItems"
        :key="item.path"
        :index="item.path"
        @click="navigateTo(item.path)"
      >
        <el-icon size="18">
          <component :is="item.icon" />
        </el-icon>
        <template #title>
          <span>{{ item.title }}</span>
        </template>
      </el-menu-item>
    </el-menu>

    <div v-show="!collapsed" class="menu-footer">
      <span class="version">v1.0.0</span>
    </div>
  </div>
</template>

<style scoped>
.side-menu {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #304156;
  overflow: hidden;
}

.logo-container {
  height: 56px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  gap: 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.logo-icon svg {
  width: 18px;
  height: 18px;
  color: #fff;
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
}

.menu {
  flex: 1;
  border-right: none;
  overflow-y: auto;
}

.menu :deep(.el-menu-item) {
  height: 48px;
  line-height: 48px;
  margin: 2px 8px;
  border-radius: 6px;
}

.menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.1) !important;
}

.menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%) !important;
}

.menu :deep(.el-menu-item .el-icon) {
  margin-right: 10px;
}

.menu :deep(.el-menu-item span) {
  font-size: 14px;
}

.menu-footer {
  padding: 12px 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.version {
  font-size: 11px;
  color: #606266;
}
</style>
