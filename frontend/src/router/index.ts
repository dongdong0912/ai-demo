import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false, title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/components/MainLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { requiresAuth: true, title: '首页' }
      },
      {
        path: 'user',
        name: 'UserManagement',
        component: () => import('@/views/UserManagement.vue'),
        meta: { requiresAuth: true, title: '用户管理', roles: ['ADMIN'] }
      },
      {
        path: 'teacher',
        name: 'TeacherManagement',
        component: () => import('@/views/TeacherManagement.vue'),
        meta: { requiresAuth: true, title: '教师管理' }
      },
      {
        path: 'class',
        name: 'ClassManagement',
        component: () => import('@/views/ClassManagement.vue'),
        meta: { requiresAuth: true, title: '班级管理' }
      },
      {
        path: 'course',
        name: 'CourseManagement',
        component: () => import('@/views/CourseManagement.vue'),
        meta: { requiresAuth: true, title: '课程管理' }
      },
      {
        path: 'student',
        name: 'StudentManagement',
        component: () => import('@/views/StudentManagement.vue'),
        meta: { requiresAuth: true, title: '学生管理' }
      },
      {
        path: 'score',
        name: 'ScoreManagement',
        component: () => import('@/views/ScoreManagement.vue'),
        meta: { requiresAuth: true, title: '成绩管理' }
      },
      {
        path: 'score-analysis',
        name: 'ScoreAnalysis',
        component: () => import('@/views/ScoreAnalysis.vue'),
        meta: { requiresAuth: true, title: '成绩分析' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { requiresAuth: true, title: '个人中心' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth)

  if (requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && userStore.isLoggedIn) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
