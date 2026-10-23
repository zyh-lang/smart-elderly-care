import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: () => import('../layout/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/Dashboard.vue'), meta: { title: '数据看板' } },
      { path: 'user', name: 'UserManage', component: () => import('../views/UserManage.vue'), meta: { title: '用户管理' } },
      { path: 'service', name: 'ServiceManage', component: () => import('../views/ServiceManage.vue'), meta: { title: '服务管理' } },
      { path: 'order', name: 'OrderManage', component: () => import('../views/OrderManage.vue'), meta: { title: '订单管理' } },
      { path: 'work-order', name: 'WorkOrderManage', component: () => import('../views/WorkOrderManage.vue'), meta: { title: '工单管理' } },
      { path: 'health', name: 'HealthManage', component: () => import('../views/HealthManage.vue'), meta: { title: '健康档案' } },
      { path: 'message', name: 'MessageCenter', component: () => import('../views/MessageCenter.vue'), meta: { title: '消息中心' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录跳转登录页
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
