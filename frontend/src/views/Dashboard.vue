<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { User, Reading, DataLine, Clock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const stats = ref([
  { title: '用户总数', value: '128', icon: User, color: '#409EFF' },
  { title: '教师总数', value: '100', icon: Reading, color: '#67C23A' },
  { title: '访问量', value: '2,458', icon: DataLine, color: '#E6A23C' },
  { title: '在线人数', value: '24', icon: Clock, color: '#909399' }
])

const activities = ref([
  { time: '10分钟前', user: '张三', action: '登录系统' },
  { time: '30分钟前', user: '李四', action: '更新个人信息' },
  { time: '1小时前', user: '王五', action: '添加新教师' },
  { time: '2小时前', user: '赵六', action: '删除用户' }
])
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">欢迎回来，{{ userStore.userInfo?.username }}</h2>
    </div>

    <div class="stats-grid">
      <div v-for="(stat, index) in stats" :key="index" class="stat-card">
        <div class="stat-icon" :style="{ background: stat.color + '20', color: stat.color }">
          <el-icon :size="24"><component :is="stat.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stat.value }}</span>
          <span class="stat-title">{{ stat.title }}</span>
        </div>
      </div>
    </div>

    <div class="content-grid">
      <el-card>
        <template #header><span class="card-title">快捷操作</span></template>
        <div class="actions-grid">
          <router-link to="/user" class="action-item">
            <el-icon size="20" color="#409EFF"><User /></el-icon>
            <span>用户管理</span>
          </router-link>
          <router-link to="/teacher" class="action-item">
            <el-icon size="20" color="#67C23A"><Reading /></el-icon>
            <span>教师管理</span>
          </router-link>
        </div>
      </el-card>

      <el-card>
        <template #header><span class="card-title">最近活动</span></template>
        <div class="activity-list">
          <div v-for="(item, index) in activities" :key="index" class="activity-item">
            <span class="activity-user">{{ item.user }}</span>
            <span class="activity-action">{{ item.action }}</span>
            <span class="activity-time">{{ item.time }}</span>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.page-container { padding: 0; }
.page-header { margin-bottom: 12px; }
.page-title { font-size: 18px; font-weight: 600; color: #303133; margin: 0; }

.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-bottom: 12px; }
.stat-card { background: #fff; border-radius: 8px; padding: 16px; display: flex; align-items: center; gap: 12px; }
.stat-icon { width: 48px; height: 48px; border-radius: 10px; display: flex; align-items: center; justify-content: center; }
.stat-info { display: flex; flex-direction: column; }
.stat-value { font-size: 22px; font-weight: 700; color: #303133; }
.stat-title { font-size: 12px; color: #909399; }

.content-grid { display: grid; grid-template-columns: 1fr 2fr; gap: 12px; }
.card-title { font-size: 14px; font-weight: 600; }

.actions-grid { display: flex; gap: 12px; }
.action-item { display: flex; align-items: center; gap: 8px; padding: 12px 20px; background: #f5f7fa; border-radius: 6px; text-decoration: none; color: #606266; font-size: 14px; transition: all 0.2s; }
.action-item:hover { background: #ecf5ff; color: #409EFF; }

.activity-list { display: flex; flex-direction: column; }
.activity-item { display: flex; align-items: center; gap: 8px; padding: 10px 0; border-bottom: 1px dashed #ebeef5; font-size: 13px; }
.activity-item:last-child { border-bottom: none; }
.activity-user { font-weight: 500; color: #303133; }
.activity-action { color: #606266; flex: 1; }
.activity-time { color: #909399; font-size: 12px; }
</style>
