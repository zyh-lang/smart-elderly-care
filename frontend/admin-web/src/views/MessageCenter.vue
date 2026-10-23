<template>
  <el-card shadow="never">
    <el-table :data="list" v-loading="loading" border stripe @row-click="handleRead">
      <el-table-column width="70">
        <template #default="{ row }">
          <el-badge :is-dot="row.readFlag === 0" />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" width="180" />
      <el-table-column prop="content" label="内容" min-width="280" />
      <el-table-column prop="msgType" label="类型" width="100">
        <template #default="{ row }">
          <el-tag size="small">{{ msgTypeText(row.msgType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" min-width="170" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag size="small" :type="row.readFlag === 1 ? 'info' : 'danger'">{{ row.readFlag === 1 ? '已读' : '未读' }}</el-tag>
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
import { ElMessage } from 'element-plus'
import { getMessages, readMessage } from '../api/notify'

const list = ref([])
const total = ref(0)
const current = ref(1)
const size = ref(10)
const loading = ref(false)

const msgTypeText = (t) => ({ SYSTEM: '系统', ORDER: '工单', HEALTH: '健康' }[t] || t)

const load = async () => {
  loading.value = true
  try {
    const res = await getMessages({ current: current.value, size: size.value })
    list.value = res.data.records
    total.value = res.data.total
  } catch (e) { /* ignore */ } finally {
    loading.value = false
  }
}

const pageChange = (page) => {
  current.value = page
  load()
}

const handleRead = async (row) => {
  if (row.readFlag === 1) return
  await readMessage(row.id)
  row.readFlag = 1
  ElMessage.success('已标记为已读')
}

onMounted(load)
</script>

<style scoped>
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
