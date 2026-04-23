<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { User, Reading, School, UserFilled, DataLine, Clock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import type { ApiResponse, DashboardStats } from '@/types'

const userStore = useUserStore()

const statsData = ref<DashboardStats>({
  totalUsers: 0,
  totalTeachers: 0,
  totalStudents: 0,
  totalClasses: 0,
  activeStudents: 0,
  activeTeachers: 0,
  activeClasses: 0
})

const stats = ref([
  { title: '用户总数', value: '0', icon: User, color: '#409EFF', key: 'totalUsers' },
  { title: '教师总数', value: '0', icon: Reading, color: '#67C23A', key: 'totalTeachers' },
  { title: '学生总数', value: '0', icon: School, color: '#E6A23C', key: 'totalStudents' },
  { title: '班级总数', value: '0', icon: School, color: '#909399', key: 'totalClasses' }
])

const fetchStats = async () => {
  try {
    const res = await request.get<ApiResponse<DashboardStats>>('/api/dashboard/stats')
    if (res.code === 200) {
      statsData.value = res.data
      stats.value = [
        { title: '用户总数', value: String(res.data.totalUsers), icon: User, color: '#409EFF', key: 'totalUsers' },
        { title: '教师总数', value: String(res.data.totalTeachers), icon: Reading, color: '#67C23A', key: 'totalTeachers' },
        { title: '学生总数', value: String(res.data.totalStudents), icon: UserFilled, color: '#E6A23C', key: 'totalStudents' },
        { title: '班级总数', value: String(res.data.totalClasses), icon: School, color: '#909399', key: 'totalClasses' }
      ]
    }
  } catch {
    ElMessage.error('获取统计数据失败')
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">欢迎回来，{{ userStore.userInfo?.username }}</h2>
      <p class="page-subtitle">以下是系统的实时数据概览</p>
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
          <router-link to="/class" class="action-item">
            <el-icon size="20" color="#E6A23C"><School /></el-icon>
            <span>班级管理</span>
          </router-link>
          <router-link to="/student" class="action-item">
            <el-icon size="20" color="#909399"><UserFilled /></el-icon>
            <span>学生管理</span>
          </router-link>
        </div>
      </el-card>

      <el-card>
        <template #header><span class="card-title">数据概览</span></template>
        <div class="overview-grid">
          <div class="overview-item">
            <span class="overview-label">在读学生</span>
            <span class="overview-value success">{{ statsData.activeStudents }}</span>
          </div>
          <div class="overview-item">
            <span class="overview-label">在职教师</span>
            <span class="overview-value success">{{ statsData.activeTeachers }}</span>
          </div>
          <div class="overview-item">
            <span class="overview-label">活跃班级</span>
            <span class="overview-value success">{{ statsData.activeClasses }}</span>
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
.page-subtitle { font-size: 13px; color: #909399; margin: 4px 0 0; }

.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-bottom: 12px; }
.stat-card { background: #fff; border-radius: 8px; padding: 16px; display: flex; align-items: center; gap: 12px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04); }
.stat-icon { width: 48px; height: 48px; border-radius: 10px; display: flex; align-items: center; justify-content: center; }
.stat-info { display: flex; flex-direction: column; }
.stat-value { font-size: 22px; font-weight: 700; color: #303133; }
.stat-title { font-size: 12px; color: #909399; }

.content-grid { display: grid; grid-template-columns: 1fr 2fr; gap: 12px; }
.card-title { font-size: 14px; font-weight: 600; }

.actions-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }
.action-item { display: flex; align-items: center; gap: 8px; padding: 12px 16px; background: #f5f7fa; border-radius: 6px; text-decoration: none; color: #606266; font-size: 14px; transition: all 0.2s; }
.action-item:hover { background: #ecf5ff; color: #409EFF; }

.overview-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.overview-item { display: flex; flex-direction: column; align-items: center; padding: 16px; background: #f5f7fa; border-radius: 8px; }
.overview-label { font-size: 12px; color: #909399; margin-bottom: 8px; }
.overview-value { font-size: 24px; font-weight: 700; }
.overview-value.success { color: #67C23A; }
</style>
