<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="6" v-for="card in cards" :key="card.label">
        <el-card shadow="hover">
          <div class="stat-label">{{ card.label }}</div>
          <div class="stat-value">{{ card.value }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="14">
        <el-card shadow="hover">
          <template #header>近 7 天订单趋势</template>
          <div ref="trendRef" style="height: 320px"></div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card shadow="hover">
          <template #header>服务类型排名</template>
          <div ref="rankRef" style="height: 320px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { getSummary, getTrend, getServiceRank } from '../api/statistics'

const cards = ref([])
const trendRef = ref(null)
const rankRef = ref(null)

onMounted(async () => {
  try {
    const summary = (await getSummary()).data
    cards.value = [
      { label: '订单总数', value: summary.orderCount ?? 0 },
      { label: '总营收（元）', value: summary.totalAmount ?? 0 },
      { label: '工单总数', value: summary.workOrderCount ?? 0 },
      { label: '平均满意度', value: summary.avgRating ?? 0 }
    ]
  } catch (e) { /* 拦截器已提示 */ }

  try {
    const trendRes = await getTrend(7)
    const trend = trendRes.data
    const trendChart = echarts.init(trendRef.value)
    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['订单数', '营收(元)'] },
      grid: { left: 50, right: 50, top: 40, bottom: 30 },
      xAxis: { type: 'category', data: trend.map(i => i.date.slice(5)) },
      yAxis: { type: 'value' },
      series: [
        { name: '订单数', type: 'line', smooth: true, data: trend.map(i => i.orderCount), areaStyle: { opacity: 0.15 } },
        { name: '营收(元)', type: 'bar', data: trend.map(i => i.amount) }
      ]
    })
    window.addEventListener('resize', () => trendChart.resize())
  } catch (e) { /* ignore */ }

  try {
    const rankRes = await getServiceRank()
    const rank = rankRes.data
    const rankChart = echarts.init(rankRef.value)
    rankChart.setOption({
      tooltip: {},
      grid: { left: 50, right: 30, top: 40, bottom: 30 },
      xAxis: { type: 'category', data: rank.map(i => i.serviceType) },
      yAxis: { type: 'value' },
      series: [{ name: '订单数', type: 'bar', barWidth: 40, data: rank.map(i => i.orderCount), itemStyle: { color: '#36cfc9' } }]
    })
    window.addEventListener('resize', () => rankChart.resize())
  } catch (e) { /* ignore */ }
})
</script>

<style scoped>
.stat-label { color: #909399; font-size: 14px; }
.stat-value { font-size: 28px; font-weight: 700; color: #303133; margin-top: 8px; }
</style>
