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
      { path: 'elder', name: 'ElderManage', component: () => import('../views/ElderManage.vue'), meta: { title: '老人管理' } },
      { path: 'service', name: 'ServiceManage', component: () => import('../views/ServiceManage.vue'), meta: { title: '服务管理' } },
      { path: 'order', name: 'OrderManage', component: () => import('../views/OrderManage.vue'), meta: { title: '订单管理' } },
      { path: 'work-order', name: 'WorkOrderManage', component: () => import('../views/WorkOrderManage.vue'), meta: { title: '工单管理' } },
      { path: 'health', name: 'HealthManage', component: () => import('../views/HealthManage.vue'), meta: { title: '健康档案' } },
      { path: 'message', name: 'MessageCenter', component: () => import('../views/MessageCenter.vue'), meta: { title: '消息中心' } },
      { path: 'provider-workbench', name: 'ProviderWorkbench', component: () => import('../views/ProviderWorkbench.vue'), meta: { title: '服务商工作台' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录跳登录页；服务商仅可访问工作台与消息中心
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')
  if (to.path !== '/login' && !token) {
    next('/login')
    return
  }
  if (to.path === '/login' && token) {
    next(role === 'PROVIDER' ? '/provider-workbench' : '/dashboard')
    return
  }
  if (role === 'PROVIDER') {
    const allowed = ['/provider-workbench', '/message']
    if (!allowed.includes(to.path)) {
      next('/provider-workbench')
    } else {
      next()
    }
    return
  }
  next()
})

export default router
