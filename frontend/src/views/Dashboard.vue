<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick, markRaw } from 'vue'
import { User, Reading, School, UserFilled, Notebook, DataLine } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import type { ApiResponse, DashboardStats, DashboardChartData } from '@/types'
import * as echarts from 'echarts'

const userStore = useUserStore()

const statsData = ref<DashboardStats>({
  totalUsers: 0, totalTeachers: 0, totalStudents: 0, totalClasses: 0, totalCourses: 0,
  activeStudents: 0, activeTeachers: 0, activeClasses: 0, activeCourses: 0
})

const chartData = ref<DashboardChartData>({
  teacherSubjectDist: {}, gradeStudentDist: {}, classStudentCount: [],
  courseCategoryDist: {}, courseTypeDist: {}, teacherTitleDist: {}, studentStatusDist: {}
})

const stats = ref([
  { title: '用户总数', value: '0', icon: markRaw(User), color: '#409EFF', key: 'totalUsers' },
  { title: '教师总数', value: '0', icon: markRaw(Reading), color: '#67C23A', key: 'totalTeachers' },
  { title: '学生总数', value: '0', icon: markRaw(School), color: '#E6A23C', key: 'totalStudents' },
  { title: '班级总数', value: '0', icon: markRaw(UserFilled), color: '#909399', key: 'totalClasses' },
  { title: '课程总数', value: '0', icon: markRaw(Notebook), color: '#667eea', key: 'totalCourses' }
])

// ECharts refs
const subjectChartRef = ref<HTMLElement>()
const gradeChartRef = ref<HTMLElement>()
const classChartRef = ref<HTMLElement>()
const courseTypeChartRef = ref<HTMLElement>()
const studentStatusChartRef = ref<HTMLElement>()
const teacherTitleChartRef = ref<HTMLElement>()

let chartInstances: echarts.ECharts[] = []

const initChart = (el: HTMLElement, option: echarts.EChartsOption): echarts.ECharts => {
  const chart = echarts.init(el)
  chart.setOption(option)
  chartInstances.push(chart)
  return chart
}

const fetchStats = async () => {
  try {
    const res: any = await request.get<ApiResponse<DashboardStats>>('/api/dashboard/stats')
    if (res.code === 200) {
      statsData.value = res.data
      stats.value = [
        { title: '用户总数', value: String(res.data.totalUsers), icon: markRaw(User), color: '#409EFF', key: 'totalUsers' },
        { title: '教师总数', value: String(res.data.totalTeachers), icon: markRaw(Reading), color: '#67C23A', key: 'totalTeachers' },
        { title: '学生总数', value: String(res.data.totalStudents), icon: markRaw(School), color: '#E6A23C', key: 'totalStudents' },
        { title: '班级总数', value: String(res.data.totalClasses), icon: markRaw(UserFilled), color: '#909399', key: 'totalClasses' },
        { title: '课程总数', value: String(res.data.totalCourses || 0), icon: markRaw(Notebook), color: '#667eea', key: 'totalCourses' }
      ]
    }
  } catch {
    ElMessage.error('获取统计数据失败')
  }
}

const fetchChartData = async () => {
  try {
    const res: any = await request.get<ApiResponse<DashboardChartData>>('/api/dashboard/chart-data')
    if (res.code === 200) {
      chartData.value = res.data
      await nextTick()
      renderCharts()
    }
  } catch {
    ElMessage.error('获取图表数据失败')
  }
}

const renderCharts = () => {
  // 清除旧实例
  chartInstances.forEach(c => c.dispose())
  chartInstances = []

  // 1. 各科目教师分布 - 柱状图
  if (subjectChartRef.value) {
    const data = chartData.value.teacherSubjectDist
    initChart(subjectChartRef.value, {
      tooltip: { trigger: 'axis' },
      grid: { left: 60, right: 20, top: 20, bottom: 30 },
      xAxis: { type: 'category', data: Object.keys(data), axisLabel: { fontSize: 11 } },
      yAxis: { type: 'value', minInterval: 1 },
      series: [{
        type: 'bar', data: Object.values(data), barWidth: '40%',
        itemStyle: {
          borderRadius: [4, 4, 0, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#667eea' }, { offset: 1, color: '#764ba2' }
          ])
        }
      }]
    })
  }

  // 2. 各年级学生分布 - 饼图
  if (gradeChartRef.value) {
    const data = chartData.value.gradeStudentDist
    const pieData = Object.entries(data).map(([name, value]) => ({ name, value }))
    initChart(gradeChartRef.value, {
      tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
      legend: { bottom: 0, textStyle: { fontSize: 11 } },
      series: [{
        type: 'pie', radius: ['35%', '60%'], center: ['50%', '45%'],
        label: { show: true, fontSize: 11, formatter: '{b}\n{c}人' },
        data: pieData,
        itemStyle: { borderColor: '#fff', borderWidth: 2 },
        color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
      }]
    })
  }

  // 3. 各班级学生人数 - 横向柱状图
  if (classChartRef.value) {
    const data = chartData.value.classStudentCount
    const sorted = [...data].sort((a, b) => b.count - a.count)
    initChart(classChartRef.value, {
      tooltip: { trigger: 'axis' },
      grid: { left: 90, right: 20, top: 10, bottom: 20 },
      xAxis: { type: 'value', minInterval: 1 },
      yAxis: { type: 'category', data: sorted.map(d => d.className), axisLabel: { fontSize: 11 } },
      series: [{
        type: 'bar', data: sorted.map(d => d.count), barWidth: '50%',
        itemStyle: {
          borderRadius: [0, 4, 4, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#409EFF' }, { offset: 1, color: '#66b1ff' }
          ])
        }
      }]
    })
  }

  // 4. 课程类型分布 - 环形图
  if (courseTypeChartRef.value) {
    const data = chartData.value.courseTypeDist
    const pieData = Object.entries(data).map(([name, value]) => ({ name, value }))
    initChart(courseTypeChartRef.value, {
      tooltip: { trigger: 'item', formatter: '{b}: {c}门 ({d}%)' },
      legend: { bottom: 0, textStyle: { fontSize: 11 } },
      series: [{
        type: 'pie', radius: ['40%', '65%'], center: ['50%', '42%'],
        label: { show: true, fontSize: 12, formatter: '{b}\n{c}门' },
        data: pieData,
        itemStyle: { borderColor: '#fff', borderWidth: 2 },
        color: ['#F56C6C', '#67C23A']
      }]
    })
  }

  // 5. 学生状态分布 - 环形图
  if (studentStatusChartRef.value) {
    const data = chartData.value.studentStatusDist
    const pieData = Object.entries(data).map(([name, value]) => ({ name, value }))
    initChart(studentStatusChartRef.value, {
      tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
      legend: { bottom: 0, textStyle: { fontSize: 11 } },
      series: [{
        type: 'pie', radius: ['40%', '65%'], center: ['50%', '42%'],
        label: { show: true, fontSize: 12, formatter: '{b}\n{c}人' },
        data: pieData,
        itemStyle: { borderColor: '#fff', borderWidth: 2 },
        color: ['#67C23A', '#E6A23C', '#909399', '#F56C6C']
      }]
    })
  }

  // 6. 教师职称分布 - 饼图
  if (teacherTitleChartRef.value) {
    const data = chartData.value.teacherTitleDist
    const pieData = Object.entries(data).map(([name, value]) => ({ name, value }))
    initChart(teacherTitleChartRef.value, {
      tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
      legend: { bottom: 0, textStyle: { fontSize: 11 } },
      series: [{
        type: 'pie', radius: ['35%', '60%'], center: ['50%', '45%'],
        label: { show: true, fontSize: 11, formatter: '{b}\n{c}人' },
        data: pieData,
        itemStyle: { borderColor: '#fff', borderWidth: 2 },
        color: ['#667eea', '#764ba2', '#409EFF', '#67C23A', '#E6A23C']
      }]
    })
  }
}

const handleResize = () => {
  chartInstances.forEach(c => c.resize())
}

onMounted(() => {
  fetchStats()
  fetchChartData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstances.forEach(c => c.dispose())
})
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">欢迎回来，{{ userStore.userInfo?.username }}</h2>
      <p class="page-subtitle">以下是系统的实时数据概览</p>
    </div>

    <!-- 统计卡片 -->
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

    <!-- 图表第一行：3列 -->
    <div class="chart-row-3">
      <div class="chart-card">
        <div class="chart-card-header">
          <el-icon color="#667eea" size="16"><DataLine /></el-icon>
          <span>各科目教师分布</span>
        </div>
        <div ref="subjectChartRef" class="chart-area"></div>
      </div>
      <div class="chart-card">
        <div class="chart-card-header">
          <el-icon color="#409EFF" size="16"><DataLine /></el-icon>
          <span>各年级学生分布</span>
        </div>
        <div ref="gradeChartRef" class="chart-area"></div>
      </div>
      <div class="chart-card">
        <div class="chart-card-header">
          <el-icon color="#67C23A" size="16"><DataLine /></el-icon>
          <span>课程类型分布</span>
        </div>
        <div ref="courseTypeChartRef" class="chart-area"></div>
      </div>
    </div>

    <!-- 图表第二行：3列 -->
    <div class="chart-row-3">
      <div class="chart-card">
        <div class="chart-card-header">
          <el-icon color="#E6A23C" size="16"><DataLine /></el-icon>
          <span>学生状态分布</span>
        </div>
        <div ref="studentStatusChartRef" class="chart-area"></div>
      </div>
      <div class="chart-card">
        <div class="chart-card-header">
          <el-icon color="#764ba2" size="16"><DataLine /></el-icon>
          <span>教师职称分布</span>
        </div>
        <div ref="teacherTitleChartRef" class="chart-area"></div>
      </div>
      <div class="chart-card">
        <div class="chart-card-header">
          <el-icon color="#409EFF" size="16"><DataLine /></el-icon>
          <span>各班级学生人数</span>
        </div>
        <div ref="classChartRef" class="chart-area"></div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="quick-actions">
      <div class="chart-card-header" style="margin-bottom: 16px;">
        <el-icon color="#667eea" size="16"><DataLine /></el-icon>
        <span>快捷操作</span>
      </div>
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
        <router-link to="/course" class="action-item">
          <el-icon size="20" color="#667eea"><Notebook /></el-icon>
          <span>课程管理</span>
        </router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-container { padding: 0; }
.page-header { margin-bottom: 12px; }
.page-title { font-size: 18px; font-weight: 600; color: #303133; margin: 0; }
.page-subtitle { font-size: 13px; color: #909399; margin: 4px 0 0; }

.stats-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 12px; margin-bottom: 16px; }
.stat-card { background: #fff; border-radius: 8px; padding: 16px; display: flex; align-items: center; gap: 12px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04); }
.stat-icon { width: 48px; height: 48px; border-radius: 10px; display: flex; align-items: center; justify-content: center; }
.stat-info { display: flex; flex-direction: column; }
.stat-value { font-size: 22px; font-weight: 700; color: #303133; }
.stat-title { font-size: 12px; color: #909399; }

.chart-row-3 { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; margin-bottom: 12px; }
.chart-card { background: #fff; border-radius: 10px; padding: 16px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04); }
.chart-card-header { display: flex; align-items: center; gap: 6px; font-size: 14px; font-weight: 600; color: #303133; margin-bottom: 8px; }
.chart-area { width: 100%; height: 280px; }

.quick-actions { background: #fff; border-radius: 10px; padding: 16px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04); }
.actions-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 12px; }
.action-item { display: flex; align-items: center; gap: 8px; padding: 12px 16px; background: #f5f7fa; border-radius: 6px; text-decoration: none; color: #606266; font-size: 14px; transition: all 0.2s; }
.action-item:hover { background: #ecf5ff; color: #409EFF; }

@media (max-width: 1200px) {
  .stats-grid { grid-template-columns: repeat(3, 1fr); }
  .chart-row-3 { grid-template-columns: 1fr 1fr; }
}
@media (max-width: 768px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .chart-row-3 { grid-template-columns: 1fr; }
  .actions-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
