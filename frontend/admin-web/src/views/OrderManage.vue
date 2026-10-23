<template>
  <el-card shadow="never">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="orderNo" label="订单号" width="220" />
      <el-table-column prop="serviceName" label="服务项目" width="140" />
      <el-table-column prop="amount" label="金额（元）" width="110" />
      <el-table-column prop="address" label="服务地址" min-width="150" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" min-width="170" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 'PENDING_DISPATCH'"
            size="small"
            type="primary"
            @click="handleDispatch(row)"
          >
            智能派单
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
import { getOrderPage, dispatchOrder } from '../api/order'

const list = ref([])
const total = ref(0)
const current = ref(1)
const size = ref(10)
const loading = ref(false)

const statusText = (s) => ({ PENDING_DISPATCH: '待派单', DISPATCHED: '已派单', CANCELLED: '已取消' }[s] || s)
const statusTagType = (s) => ({ PENDING_DISPATCH: 'warning', DISPATCHED: 'success', CANCELLED: 'info' }[s] || 'info')

const load = async () => {
  loading.value = true
  try {
    const res = await getOrderPage({ current: current.value, size: size.value })
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

const handleDispatch = (row) => {
  ElMessageBox.confirm(`对订单「${row.orderNo}」执行智能派单？系统将自动选择最优服务商`, '智能派单', { type: 'info' })
    .then(async () => {
      const res = await dispatchOrder(row.id)
      ElMessage.success(`派单成功，工单已派给：${res.data.providerName}`)
      load()
    })
    .catch(() => {})
}

onMounted(load)
</script>

<style scoped>
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
