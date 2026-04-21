<script setup lang="ts">
import { ref } from 'vue'
import SideMenu from './SideMenu.vue'
import AppHeader from './AppHeader.vue'

const collapsed = ref(false)

const handleCollapse = (isCollapsed: boolean) => {
  collapsed.value = isCollapsed
}

const version = 'v1.0.0'
const year = new Date().getFullYear()
</script>

<template>
  <div class="main-layout">
    <aside class="sidebar" :class="{ collapsed }">
      <SideMenu :collapsed="collapsed" />
    </aside>

    <div class="main-content">
      <header class="header">
        <AppHeader @collapse="handleCollapse" />
      </header>

      <main class="page-content">
        <router-view />
      </main>

      <footer class="footer">
        <div class="footer-content">
          <span class="copyright">© {{ year }} 用户管理系统</span>
          <span class="divider">|</span>
          <span class="version">版本 {{ version }}</span>
          <span class="divider">|</span>
          <span class="author">Developed by CodeBuddy</span>
        </div>
      </footer>
    </div>
  </div>
</template>

<style scoped>
.main-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  width: 200px;
  height: 100vh;
  background: #304156;
  transition: width 0.2s ease;
  overflow: hidden;
  flex-shrink: 0;
}

.sidebar.collapsed {
  width: 64px;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #f0f2f5;
}

.header {
  height: 56px;
  flex-shrink: 0;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
}

.page-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.footer {
  height: 40px;
  flex-shrink: 0;
  background: #fff;
  border-top: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.footer-content {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}

.copyright {
  color: #606266;
  font-weight: 500;
}

.divider {
  color: #dcdfe6;
}

.version {
  color: #667eea;
  font-weight: 500;
}

.author {
  color: #c0c4cc;
}
</style>
