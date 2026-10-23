<template>
  <el-card shadow="never">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="workOrderNo" label="工单号" width="220" />
      <el-table-column prop="orderId" label="订单ID" width="80" />
      <el-table-column prop="providerName" label="服务商" width="170" />
      <el-table-column prop="status" label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="dispatchTime" label="派单时间" min-width="170" />
      <el-table-column prop="acceptTime" label="开始时间" min-width="170" />
      <el-table-column prop="finishTime" label="完成时间" min-width="170" />
      <el-table-column label="操作" width="110" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="['DISPATCHED', 'IN_SERVICE', 'WAIT_ACCEPT'].includes(row.status)"
            size="small"
            type="danger"
            @click="handleCancel(row)"
          >
            取消
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
import { getWorkOrderPage, cancelWorkOrder } from '../api/workOrder'

const list = ref([])
const total = ref(0)
const current = ref(1)
const size = ref(10)
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
    const res = await getWorkOrderPage({ current: current.value, size: size.value })
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

const handleCancel = (row) => {
  ElMessageBox.confirm(`确定取消工单「${row.workOrderNo}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await cancelWorkOrder(row.id)
      ElMessage.success('工单已取消')
      load()
    })
    .catch(() => {})
}

onMounted(load)
</script>

<style scoped>
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
