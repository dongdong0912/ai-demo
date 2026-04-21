<script setup lang="ts">
import { ref } from 'vue'
import SideMenu from './SideMenu.vue'
import AppHeader from './AppHeader.vue'

const collapsed = ref(false)

const handleCollapse = (isCollapsed: boolean) => {
  collapsed.value = isCollapsed
}
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
</style>
