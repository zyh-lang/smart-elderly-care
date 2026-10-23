<template>
  <div class="login-page">
    <el-card class="login-card" shadow="always">
      <h2 class="title">社区智慧养老服务平台</h2>
      <p class="subtitle">管理端登录</p>
      <el-form :model="form" @keyup.enter="handleLogin">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" size="large">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password>
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="tips">默认管理员：admin / 123456</div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/auth'

const router = useRouter()
const loading = ref(false)
const form = reactive({ username: 'admin', password: '' })

const handleLogin = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await login(form)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('username', res.data.username)
    localStorage.setItem('role', res.data.role)
    ElMessage.success('登录成功，欢迎 ' + res.data.realName)
    router.push('/dashboard')
  } catch (e) {
    /* 错误提示由拦截器处理 */
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1677ff 0%, #36cfc9 100%);
}
.login-card { width: 400px; border-radius: 8px; }
.title { text-align: center; margin: 8px 0 4px; color: #333; }
.subtitle { text-align: center; color: #999; margin-bottom: 24px; font-size: 14px; }
.tips { text-align: center; color: #bbb; font-size: 13px; margin-top: 4px; }
</style>
