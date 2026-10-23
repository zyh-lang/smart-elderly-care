<template>
  <el-card shadow="never">
    <el-alert
      type="info"
      :closable="false"
      show-icon
      title="服务商工作台：接单（开始服务）后上门履约，服务完成后提交，等待家属验收"
      style="margin-bottom: 16px"
    />
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="workOrderNo" label="工单号" width="220" />
      <el-table-column prop="orderId" label="订单ID" width="90" />
      <el-table-column prop="status" label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="dispatchTime" label="派单时间" min-width="170" />
      <el-table-column prop="acceptTime" label="开始时间" min-width="170" />
      <el-table-column prop="finishTime" label="完成时间" min-width="170" />
      <el-table-column label="操作" width="190" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 'DISPATCHED'" size="small" type="primary" @click="handleStart(row)">
            接单（开始服务）
          </el-button>
          <el-button v-if="row.status === 'IN_SERVICE'" size="small" type="success" @click="handleFinish(row)">
            完成服务
          </el-button>
          <span v-else-if="row.status === 'WAIT_ACCEPT'" class="wait-tip">等待家属验收</span>
          <span v-else class="done-tip">已完结</span>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="!list.length && !loading" description="暂无工单" />
  </el-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyWorkOrders, startWorkOrder, finishWorkOrder } from '../api/workOrder'

const list = ref([])
const loading = ref(false)

const statusMap = {
  DISPATCHED: '已派单',
  IN_SERVICE: '服务中',
  WAIT_ACCEPT: '待验收',
  COMPLETED: '已完成',
  EVALUATED: '已评价',
  CANCELLED: '已取消',
  COMPLAINED: '已投诉'
}
const statusTagMap = {
  DISPATCHED: 'warning',
  IN_SERVICE: 'primary',
  WAIT_ACCEPT: 'warning',
  COMPLETED: 'success',
  EVALUATED: 'success',
  CANCELLED: 'info',
  COMPLAINED: 'danger'
}
const statusText = (s) => statusMap[s] || s
const statusTagType = (s) => statusTagMap[s] || 'info'

const load = async () => {
  loading.value = true
  try {
    const res = await getMyWorkOrders()
    list.value = res.data
  } catch (e) { /* 拦截器已提示 */ } finally {
    loading.value = false
  }
}

const handleStart = (row) => {
  ElMessageBox.confirm(`确认接单并开始工单「${row.workOrderNo}」的服务吗？`, '接单确认', { type: 'info' })
    .then(async () => {
      await startWorkOrder(row.id)
      ElMessage.success('已接单，服务开始')
      load()
    })
    .catch(() => {})
}

const handleFinish = (row) => {
  ElMessageBox.confirm(`确认工单「${row.workOrderNo}」服务已完成吗？提交后将等待家属验收`, '完成确认', { type: 'info' })
    .then(async () => {
      await finishWorkOrder(row.id)
      ElMessage.success('已完成，等待家属验收')
      load()
    })
    .catch(() => {})
}

onMounted(load)
</script>

<style scoped>
.wait-tip { color: #fa8c16; font-size: 13px; }
.done-tip { color: #909399; font-size: 13px; }
</style>
