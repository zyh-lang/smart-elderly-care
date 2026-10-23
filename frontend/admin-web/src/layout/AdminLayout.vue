<template>
  <el-container class="layout">
    <el-aside width="210px" class="aside">
      <div class="logo">🏠 智慧养老管理端</div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#001529"
        text-color="rgba(255,255,255,0.75)"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard"><el-icon><DataLine /></el-icon><span>数据看板</span></el-menu-item>
        <el-menu-item index="/user"><el-icon><User /></el-icon><span>用户管理</span></el-menu-item>
        <el-menu-item index="/service"><el-icon><Goods /></el-icon><span>服务管理</span></el-menu-item>
        <el-menu-item index="/order"><el-icon><List /></el-icon><span>订单管理</span></el-menu-item>
        <el-menu-item index="/work-order"><el-icon><Tickets /></el-icon><span>工单管理</span></el-menu-item>
        <el-menu-item index="/health"><el-icon><FirstAidKit /></el-icon><span>健康档案</span></el-menu-item>
        <el-menu-item index="/message"><el-icon><Bell /></el-icon><span>消息中心</span></el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="page-title">{{ $route.meta.title }}</div>
        <div class="user-info">
          <el-tag size="small">{{ role }}</el-tag>
          <span class="name">{{ username }}</span>
          <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { logout } from '../api/auth'

const router = useRouter()
const username = ref(localStorage.getItem('username') || '管理员')
const role = ref(localStorage.getItem('role') || 'ADMIN')

const handleLogout = async () => {
  try {
    await logout()
  } catch (e) {
    /* 忽略登出接口异常 */
  }
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.layout { height: 100vh; }
.aside { background-color: #001529; }
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-weight: bold;
  font-size: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
.aside :deep(.el-menu) { border-right: none; }
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #eee;
}
.page-title { font-size: 16px; font-weight: 600; }
.user-info { display: flex; align-items: center; gap: 10px; }
.name { font-size: 14px; color: #333; }
.main { background-color: #f5f7fa; }
</style>
