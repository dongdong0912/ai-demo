<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, TrendCharts, Trophy, DataLine, Document, List } from '@element-plus/icons-vue'
import { scoreApi, type Score } from '@/api/score'
import { courseApi } from '@/api/course'
import { classApi } from '@/api/class'
import { studentApi } from '@/api/student'
import type { Course, GradeClass, Student } from '@/types'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart, LineChart, PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'

use([CanvasRenderer, BarChart, LineChart, PieChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

// 统计数据接口
interface ScoreStatistics {
  averageScore: number
  maxScore: number
  minScore: number
  totalScore: number
  totalCount: number
  passRate: number
  passCount: number
  failCount: number
  excellentCount: number
  goodCount: number
  mediumCount: number
  passCountLevel: number
  failLevelCount: number
  excellentRate: number
  goodRate: number
  mediumRate: number
  schoolYear: string
  semester: string
  courseId: number
  classId: number
  examType: string
  courseName: string
  className: string
  gradeName: string
}

interface ScoreRanking {
  studentId: number
  studentNo: string
  studentName: string
  classId: number
  className: string
  gradeName: string
  score: number
  classRank: number
  gradeRank: number
  totalInClass: number
  totalInGrade: number
  classPercentile: number
  gradePercentile: number
  courseName: string
  examType: string
}

interface ScoreTrend {
  examType: string
  examDate: string
  averageScore: number
  maxScore: number
  minScore: number
  examCount: number
}

interface StudentReport {
  studentId: number
  studentNo: string
  studentName: string
  className: string
  gradeName: string
  scores: Score[]
  overallStats: {
    totalExams: number
    averageScore: number
    maxScore: number
    minScore: number
    totalScore: number
    passRate: number
  }
  courseStats: Array<{
    courseId: number
    courseName: string
    averageScore: number
    maxScore: number
    minScore: number
    examCount: number
  }>
  schoolYear: string
  semester: string
}

// 状态
const loading = ref(false)
const statisticsLoading = ref(false)
const activeTab = ref('statistics')

// 筛选条件
const searchForm = ref({
  schoolYear: '',
  semester: '',
  courseId: undefined as number | undefined,
  classId: undefined as number | undefined,
  examType: ''
})

// 数据
const statistics = ref<ScoreStatistics | null>(null)
const rankings = ref<ScoreRanking[]>([])
const trends = ref<ScoreTrend[]>([])
const courseList = ref<Course[]>([])
const classList = ref<GradeClass[]>([])
const studentList = ref<Student[]>([])

// 排名分页
const rankingPage = ref(1)
const rankingPageSize = ref(10)

// 成绩单弹窗
const reportDialogVisible = ref(false)
const selectedStudent = ref<number | undefined>()
const studentReport = ref<StudentReport | null>(null)
const reportLoading = ref(false)

// 图表ref
const distributionChartRef = ref<HTMLElement>()
const trendChartRef = ref<HTMLElement>()

const examTypeOptions = [
  { label: '全部', value: '' },
  { label: '期中考试', value: '期中考试' },
  { label: '期末考试', value: '期末考试' },
  { label: '月考', value: '月考' },
  { label: '周考', value: '周考' },
  { label: '随堂测试', value: '随堂测试' },
  { label: '补考', value: '补考' }
]

const semesterOptions = [
  { label: '全部', value: '' },
  { label: '第一学期', value: '第一学期' },
  { label: '第二学期', value: '第二学期' }
]

// 当前年份
const currentYear = new Date().getFullYear()
const yearOptions = computed(() => {
  const years = []
  for (let y = currentYear; y >= currentYear - 5; y--) {
    years.push({ label: `${y}-${y + 1}学年`, value: `${y}-${y + 1}` })
  }
  return [{ label: '全部', value: '' }, ...years]
})

// 获取课程列表
const fetchCourses = async () => {
  try {
    const res = await courseApi.getList()
    courseList.value = res.data || []
  } catch {
    ElMessage.error('获取课程列表失败')
  }
}

// 获取班级列表
const fetchClasses = async () => {
  try {
    const res = await classApi.getList()
    classList.value = res.data || []
  } catch {
    ElMessage.error('获取班级列表失败')
  }
}

// 获取学生列表
const fetchStudents = async () => {
  try {
    const res = await studentApi.getList()
    studentList.value = res.data || []
  } catch {
    ElMessage.error('获取学生列表失败')
  }
}

// 获取统计数据
const fetchStatistics = async () => {
  statisticsLoading.value = true
  try {
    const res = await scoreApi.getStatistics({
      schoolYear: searchForm.value.schoolYear || undefined,
      semester: searchForm.value.semester || undefined,
      courseId: searchForm.value.courseId,
      classId: searchForm.value.classId,
      examType: searchForm.value.examType || undefined
    })
    statistics.value = res.data
  } catch {
    ElMessage.error('获取统计数据失败')
  } finally {
    statisticsLoading.value = false
  }
}

// 获取排名数据
const fetchRankings = async () => {
  loading.value = true
  try {
    const res = await scoreApi.getRanking({
      schoolYear: searchForm.value.schoolYear || undefined,
      semester: searchForm.value.semester || undefined,
      courseId: searchForm.value.courseId,
      classId: searchForm.value.classId,
      examType: searchForm.value.examType || undefined,
      orderBy: 'desc'
    })
    rankings.value = res.data || []
  } catch {
    ElMessage.error('获取排名数据失败')
  } finally {
    loading.value = false
  }
}

// 获取趋势数据
const fetchTrends = async () => {
  loading.value = true
  try {
    const res = await scoreApi.getTrend({
      schoolYear: searchForm.value.schoolYear || undefined,
      semester: searchForm.value.semester || undefined,
      courseId: searchForm.value.courseId,
      classId: searchForm.value.classId
    })
    trends.value = res.data || []
  } catch {
    ElMessage.error('获取趋势数据失败')
  } finally {
    loading.value = false
  }
}

// 获取学生成绩单
const fetchStudentReport = async () => {
  if (!selectedStudent.value) return
  reportLoading.value = true
  try {
    const res = await scoreApi.getStudentReport(selectedStudent.value, {
      schoolYear: searchForm.value.schoolYear || undefined,
      semester: searchForm.value.semester || undefined
    })
    studentReport.value = res.data
  } catch {
    ElMessage.error('获取成绩单失败')
  } finally {
    reportLoading.value = false
  }
}

// 搜索
const handleSearch = () => {
  rankingPage.value = 1
  fetchStatistics()
  fetchRankings()
  fetchTrends()
}

// 重置
const handleReset = () => {
  searchForm.value = {
    schoolYear: '',
    semester: '',
    courseId: undefined,
    classId: undefined,
    examType: ''
  }
  handleSearch()
}

// 查看学生成绩单
const viewStudentReport = (studentId: number) => {
  selectedStudent.value = studentId
  reportDialogVisible.value = true
}

watch(selectedStudent, () => {
  if (selectedStudent.value) {
    fetchStudentReport()
  }
})

// 排名分页
const paginatedRankings = computed(() => {
  const start = (rankingPage.value - 1) * rankingPageSize.value
  const end = start + rankingPageSize.value
  return rankings.value.slice(start, end)
})

// ECharts 配置
const distributionChartOption = computed(() => {
  if (!statistics.value) return {}
  const stats = statistics.value
  return {
    title: { text: '成绩分布', left: 'center', textStyle: { fontSize: 16 } },
    tooltip: { trigger: 'item' },
    legend: { bottom: 10, left: 'center' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: '{b}: {c}人 ({d}%)' },
      data: [
        { value: stats.excellentCount, name: '优秀(90+)', itemStyle: { color: '#67C23A' } },
        { value: stats.goodCount, name: '良好(80-89)', itemStyle: { color: '#409EFF' } },
        { value: stats.mediumCount, name: '中等(70-79)', itemStyle: { color: '#E6A23C' } },
        { value: stats.passCountLevel, name: '及格(60-69)', itemStyle: { color: '#909399' } },
        { value: stats.failLevelCount, name: '不及格(<60)', itemStyle: { color: '#F56C6C' } }
      ].filter(d => d.value > 0)
    }]
  }
})

const trendChartOption = computed(() => {
  if (!trends.value.length) return {}
  return {
    title: { text: '成绩趋势', left: 'center', textStyle: { fontSize: 16 } },
    tooltip: { trigger: 'axis' },
    legend: { data: ['平均分', '最高分', '最低分'], bottom: 10 },
    grid: { left: '3%', right: '4%', bottom: '15%' },
    xAxis: { type: 'category', data: trends.value.map(t => t.examType) },
    yAxis: { type: 'value', min: 0, max: 100 },
    series: [
      { name: '平均分', type: 'line', data: trends.value.map(t => t.averageScore), smooth: true, itemStyle: { color: '#409EFF' } },
      { name: '最高分', type: 'line', data: trends.value.map(t => t.maxScore), smooth: true, itemStyle: { color: '#67C23A' } },
      { name: '最低分', type: 'line', data: trends.value.map(t => t.minScore), smooth: true, itemStyle: { color: '#F56C6C' } }
    ]
  }
})

const classScoreChartOption = computed(() => {
  if (!rankings.value.length) return {}
  // 取前10名
  const top10 = rankings.value.slice(0, 10)
  return {
    title: { text: '班级成绩分布', left: 'center', textStyle: { fontSize: 16 } },
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '10%' },
    xAxis: { type: 'category', data: top10.map(r => r.studentName), axisLabel: { rotate: 45 } },
    yAxis: { type: 'value', min: 0, max: 100 },
    series: [{ type: 'bar', data: top10.map((r, i) => ({
      value: r.score,
      itemStyle: { color: i < 3 ? '#67C23A' : '#409EFF' }
    })) }]
  }
})

// 初始化
onMounted(() => {
  fetchCourses()
  fetchClasses()
  fetchStudents()
  fetchStatistics()
  fetchRankings()
  fetchTrends()
})
</script>

<template>
  <div class="page-container">
    <!-- 搜索筛选 -->
    <div class="filter-card">
      <div class="filter-row">
        <el-select v-model="searchForm.schoolYear" placeholder="学年" clearable style="width: 150px">
          <el-option v-for="y in yearOptions" :key="y.value" :label="y.label" :value="y.value" />
        </el-select>
        <el-select v-model="searchForm.semester" placeholder="学期" clearable style="width: 120px">
          <el-option v-for="s in semesterOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
        <el-select v-model="searchForm.courseId" placeholder="课程" clearable filterable style="width: 180px">
          <el-option v-for="c in courseList" :key="c.id" :label="c.courseName" :value="c.id" />
        </el-select>
        <el-select v-model="searchForm.classId" placeholder="班级" clearable filterable style="width: 160px">
          <el-option v-for="cls in classList" :key="cls.id" :label="cls.className" :value="cls.id" />
        </el-select>
        <el-select v-model="searchForm.examType" placeholder="考试类型" clearable style="width: 130px">
          <el-option v-for="e in examTypeOptions" :key="e.value" :label="e.label" :value="e.value" />
        </el-select>
        <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>查询</el-button>
        <el-button @click="handleReset"><el-icon><Refresh /></el-icon>重置</el-button>
      </div>
    </div>

    <!-- 标签页 -->
    <el-tabs v-model="activeTab" class="analysis-tabs">
      <!-- 统计分析 -->
      <el-tab-pane label="统计分析" name="statistics">
        <div v-loading="statisticsLoading" class="statistics-content">
          <!-- 统计卡片 -->
          <div class="stat-cards">
            <div class="stat-card">
              <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                <el-icon><TrendCharts /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ statistics?.averageScore || 0 }}</div>
                <div class="stat-label">平均分</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                <el-icon><Trophy /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ statistics?.maxScore || 0 }}</div>
                <div class="stat-label">最高分</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                <el-icon><DataLine /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ statistics?.minScore || 0 }}</div>
                <div class="stat-label">最低分</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ statistics?.passRate || 0 }}%</div>
                <div class="stat-label">及格率</div>
              </div>
            </div>
          </div>

          <!-- 详细统计 -->
          <div class="detail-stats" v-if="statistics">
            <el-card shadow="hover">
              <template #header>
                <span class="card-title">详细统计数据</span>
              </template>
              <el-descriptions :column="3" border>
                <el-descriptions-item label="考试人数">{{ statistics.totalCount }}人</el-descriptions-item>
                <el-descriptions-item label="总分">{{ statistics.totalScore }}</el-descriptions-item>
                <el-descriptions-item label="及格人数">{{ statistics.passCount }}人</el-descriptions-item>
                <el-descriptions-item label="不及格人数">{{ statistics.failCount }}人</el-descriptions-item>
                <el-descriptions-item label="优秀人数(90+)">{{ statistics.excellentCount }}人 ({{ statistics.excellentRate }}%)</el-descriptions-item>
                <el-descriptions-item label="良好人数(80-89)">{{ statistics.goodCount }}人 ({{ statistics.goodRate }}%)</el-descriptions-item>
                <el-descriptions-item label="中等人数(70-79)">{{ statistics.mediumCount }}人 ({{ statistics.mediumRate }}%)</el-descriptions-item>
                <el-descriptions-item label="及格人数(60-69)">{{ statistics.passCountLevel }}人</el-descriptions-item>
                <el-descriptions-item label="不及格(<60)">{{ statistics.failLevelCount }}人</el-descriptions-item>
              </el-descriptions>
            </el-card>
          </div>

          <!-- 图表 -->
          <div class="charts-grid">
            <el-card shadow="hover" class="chart-card">
              <div ref="distributionChartRef" style="height: 350px">
                <v-chart :option="distributionChartOption" style="height: 350px" autoresize />
              </div>
            </el-card>
            <el-card shadow="hover" class="chart-card">
              <div ref="trendChartRef" style="height: 350px">
                <v-chart :option="trendChartOption" style="height: 350px" autoresize />
              </div>
            </el-card>
          </div>
        </div>
      </el-tab-pane>

      <!-- 成绩排名 -->
      <el-tab-pane label="成绩排名" name="ranking">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">成绩排名榜</span>
              <el-radio-group v-model="rankingPageSize" size="small">
                <el-radio-button :value="10">前10名</el-radio-button>
                <el-radio-button :value="20">前20名</el-radio-button>
                <el-radio-button :value="50">前50名</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <el-table :data="paginatedRankings" v-loading="loading" stripe highlight-current-row>
            <el-table-column prop="classRank" label="班级排名" width="100" align="center">
              <template #default="{ row }">
                <el-tag v-if="row.classRank === 1" class="rank-gold" effect="dark">
                  <el-icon><Trophy /></el-icon> 第1名
                </el-tag>
                <el-tag v-else-if="row.classRank === 2" class="rank-silver" effect="dark">
                  第2名
                </el-tag>
                <el-tag v-else-if="row.classRank === 3" class="rank-bronze" effect="dark">
                  第3名
                </el-tag>
                <span v-else>#{{ row.classRank }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="studentNo" label="学号" width="120" />
            <el-table-column prop="studentName" label="姓名" width="120">
              <template #default="{ row }">
                <div class="student-cell">
                  <el-avatar :size="28" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                    {{ row.studentName?.charAt(0) }}
                  </el-avatar>
                  <span>{{ row.studentName }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="className" label="班级" width="140" />
            <el-table-column prop="courseName" label="课程" width="140" />
            <el-table-column prop="score" label="成绩" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.score >= 90 ? 'success' : row.score >= 60 ? 'primary' : 'danger'" effect="dark">
                  {{ row.score }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="examType" label="考试类型" width="120" align="center" />
            <el-table-column prop="gradePercentile" label="年级百分位" width="120" align="center">
              <template #default="{ row }">
                <el-progress :percentage="row.gradePercentile || 0" :color="getPercentileColor(row.gradePercentile)" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" align="center">
              <template #default="{ row }">
                <el-button type="primary" link @click="viewStudentReport(row.studentId)">查看成绩单</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-wrapper">
            <el-pagination
              v-model:current-page="rankingPage"
              :page-size="rankingPageSize"
              :total="rankings.length"
              layout="total, prev, pager, next"
              background
            />
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 成绩单管理 -->
      <el-tab-pane label="成绩单管理" name="report">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">学生成绩单查询</span>
          </template>
          <div class="report-search">
            <el-select v-model="selectedStudent" placeholder="选择学生" filterable clearable style="width: 300px">
              <el-option v-for="s in studentList" :key="s.id" :label="`${s.name} (${s.studentNo})`" :value="s.id" />
            </el-select>
            <el-button type="primary" @click="fetchStudentReport" :disabled="!selectedStudent">查询</el-button>
          </div>

          <div v-if="studentReport" class="report-content">
            <el-descriptions title="学生信息" :column="3" border class="report-section">
              <el-descriptions-item label="学号">{{ studentReport.studentNo }}</el-descriptions-item>
              <el-descriptions-item label="姓名">{{ studentReport.studentName }}</el-descriptions-item>
              <el-descriptions-item label="班级">{{ studentReport.gradeName }} {{ studentReport.className }}</el-descriptions-item>
              <el-descriptions-item label="学年/学期">{{ studentReport.schoolYear || '全部'}} {{ studentReport.semester || '' }}</el-descriptions-item>
            </el-descriptions>

            <el-descriptions title="总体成绩统计" :column="3" border class="report-section">
              <el-descriptions-item label="考试次数">{{ studentReport.overallStats?.totalExams }}次</el-descriptions-item>
              <el-descriptions-item label="平均分">{{ studentReport.overallStats?.averageScore }}分</el-descriptions-item>
              <el-descriptions-item label="最高分">{{ studentReport.overallStats?.maxScore }}分</el-descriptions-item>
              <el-descriptions-item label="最低分">{{ studentReport.overallStats?.minScore }}分</el-descriptions-item>
              <el-descriptions-item label="总分">{{ studentReport.overallStats?.totalScore }}分</el-descriptions-item>
              <el-descriptions-item label="及格率">{{ studentReport.overallStats?.passRate }}%</el-descriptions-item>
            </el-descriptions>

            <el-table :data="studentReport.courseStats" stripe class="report-section" v-if="studentReport.courseStats?.length">
              <el-table-column prop="courseName" label="课程" />
              <el-table-column prop="examCount" label="考试次数" align="center" />
              <el-table-column prop="averageScore" label="平均分" align="center">
                <template #default="{ row }">
                  <el-tag :type="row.averageScore >= 90 ? 'success' : row.averageScore >= 60 ? 'primary' : 'danger'">
                    {{ row.averageScore }}分
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="maxScore" label="最高分" align="center" />
              <el-table-column prop="minScore" label="最低分" align="center" />
            </el-table>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 成绩单查看弹窗 -->
    <el-dialog v-model="reportDialogVisible" title="学生成绩单" width="800px" destroy-on-close>
      <div v-loading="reportLoading">
        <template v-if="studentReport">
          <el-descriptions title="学生信息" :column="3" border class="report-section">
            <el-descriptions-item label="学号">{{ studentReport.studentNo }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ studentReport.studentName }}</el-descriptions-item>
            <el-descriptions-item label="班级">{{ studentReport.gradeName }} {{ studentReport.className }}</el-descriptions-item>
            <el-descriptions-item label="学年/学期">{{ studentReport.schoolYear || '全部'}} {{ studentReport.semester || '' }}</el-descriptions-item>
          </el-descriptions>

          <el-descriptions title="总体成绩统计" :column="3" border class="report-section">
            <el-descriptions-item label="考试次数">{{ studentReport.overallStats?.totalExams }}次</el-descriptions-item>
            <el-descriptions-item label="平均分">{{ studentReport.overallStats?.averageScore }}分</el-descriptions-item>
            <el-descriptions-item label="最高分">{{ studentReport.overallStats?.maxScore }}分</el-descriptions-item>
            <el-descriptions-item label="最低分">{{ studentReport.overallStats?.minScore }}分</el-descriptions-item>
            <el-descriptions-item label="总分">{{ studentReport.overallStats?.totalScore }}分</el-descriptions-item>
            <el-descriptions-item label="及格率">{{ studentReport.overallStats?.passRate }}%</el-descriptions-item>
          </el-descriptions>

          <el-table :data="studentReport.courseStats" stripe class="report-section" v-if="studentReport.courseStats?.length">
            <el-table-column prop="courseName" label="课程" />
            <el-table-column prop="examCount" label="考试次数" align="center" />
            <el-table-column prop="averageScore" label="平均分" align="center">
              <template #default="{ row }">
                <el-tag :type="row.averageScore >= 90 ? 'success' : row.averageScore >= 60 ? 'primary' : 'danger'">
                  {{ row.averageScore }}分
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="maxScore" label="最高分" align="center" />
            <el-table-column prop="minScore" label="最低分" align="center" />
          </el-table>
        </template>
        <el-empty v-else-if="!reportLoading" description="暂无成绩单数据" />
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
// 辅助函数
const getPercentileColor = (percentile: number | undefined) => {
  if (!percentile) return '#909399'
  if (percentile >= 90) return '#67C23A'
  if (percentile >= 70) return '#409EFF'
  if (percentile >= 50) return '#E6A23C'
  return '#F56C6C'
}

// Date格式化
declare global {
  interface Date {
    format(fmt: string): string
  }
}
Date.prototype.format = function(fmt: string) {
  const o: any = {
    'M+': this.getMonth() + 1,
    'd+': this.getDate(),
    'h+': this.getHours(),
    'm+': this.getMinutes(),
    's+': this.getSeconds(),
    'q+': Math.floor((this.getMonth() + 3) / 3),
    'S': this.getMilliseconds()
  }
  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length))
  }
  for (const k in o) {
    if (new RegExp('(' + k + ')').test(fmt)) {
      fmt = fmt.replace(RegExp.$1, RegExp.$1.length === 1 ? o[k] + '' : ('00' + o[k]).substr(('' + o[k]).length))
    }
  }
  return fmt
}
</script>

<style scoped>
.page-container { padding: 0; }

.filter-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.filter-row {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.analysis-tabs {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.analysis-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.statistics-content {
  min-height: 400px;
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon .el-icon {
  font-size: 28px;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.detail-stats {
  margin-bottom: 24px;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.chart-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.student-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.report-search {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.report-content {
  margin-top: 20px;
}

.report-section {
  margin-bottom: 20px;
}

/* 排名标签 */
:deep(.rank-gold) { background: linear-gradient(135deg, #ffd700, #ffb347) !important; border-color: #ffd700 !important; color: #fff !important; }
:deep(.rank-silver) { background: linear-gradient(135deg, #c0c0c0, #a9a9a9) !important; border-color: #c0c0c0 !important; color: #333 !important; }
:deep(.rank-bronze) { background: linear-gradient(135deg, #cd7f32, #b87333) !important; border-color: #cd7f32 !important; color: #fff !important; }

@media (max-width: 1200px) {
  .stat-cards { grid-template-columns: repeat(2, 1fr); }
  .charts-grid { grid-template-columns: 1fr; }
}
</style>
