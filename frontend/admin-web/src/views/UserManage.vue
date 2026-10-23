<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索用户名/姓名" style="width: 240px" clearable @keyup.enter="load" @clear="load" />
      <el-button type="primary" @click="load">查询</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="username" label="用户名" width="140" />
      <el-table-column prop="realName" label="姓名" width="140" />
      <el-table-column prop="phone" label="手机号" width="140" />
      <el-table-column prop="role" label="角色" width="120">
        <template #default="{ row }">
          <el-tag :type="roleTagType(row.role)">{{ roleText(row.role) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="170" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button
            size="small"
            :type="row.status === 1 ? 'danger' : 'success'"
            :disabled="row.role === 'ADMIN'"
            @click="toggleStatus(row)"
          >
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      class="pager"
      background
      layout="total, prev, pager, next"
      :total="total"
      :page-size="size"
      :current-page="current"
      @current-change="pageChange"
    />
  </el-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, updateUserStatus } from '../api/user'

const list = ref([])
const total = ref(0)
const current = ref(1)
const size = ref(10)
const keyword = ref('')
const loading = ref(false)

const roleText = (role) => ({ ADMIN: '管理员', STAFF: '工作人员', PROVIDER: '服务商', FAMILY: '家属' }[role] || role)
const roleTagType = (role) => ({ ADMIN: 'danger', STAFF: 'warning', PROVIDER: 'success', FAMILY: 'info' }[role] || 'info')

const load = async () => {
  loading.value = true
  try {
    const res = await getUserList({ current: current.value, size: size.value, keyword: keyword.value || undefined })
    list.value = res.data.records
    total.value = res.data.total
  } catch (e) { /* 拦截器已提示 */ } finally {
    loading.value = false
  }
}

const pageChange = (page) => {
  current.value = page
  load()
}

const toggleStatus = (row) => {
  const target = row.status === 1 ? 0 : 1
  ElMessageBox.confirm(`确定要${target === 1 ? '启用' : '禁用'}用户「${row.username}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await updateUserStatus(row.id, target)
      ElMessage.success('操作成功')
      load()
    })
    .catch(() => {})
}

onMounted(load)
</script>

<style scoped>
.toolbar { display: flex; gap: 10px; margin-bottom: 16px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
