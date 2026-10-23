const { get, post } = require('../../utils/request')

const METRIC_LABELS = {
  BLOOD_PRESSURE: '血压',
  BLOOD_SUGAR: '血糖',
  HEART_RATE: '心率',
  TEMPERATURE: '体温',
  WEIGHT: '体重'
}

Page({
  data: {
    elders: [],
    elderNames: [],
    elderIndex: -1,
    record: null,
    metrics: [],
    medications: [],
    metricTypes: [
      { label: '血压', value: 'BLOOD_PRESSURE' },
      { label: '血糖', value: 'BLOOD_SUGAR' },
      { label: '心率', value: 'HEART_RATE' },
      { label: '体温', value: 'TEMPERATURE' },
      { label: '体重', value: 'WEIGHT' }
    ],
    metricTypeIndex: 0,
    metricValue: '',
    metricLabel(v) {
      return METRIC_LABELS[v] || v
    }
  },

  async onShow() {
    if (!wx.getStorageSync('token')) {
      wx.reLaunch({ url: '/pages/login/login' })
      return
    }
    try {
      const elderRes = await get('/user/elder/my')
      this.setData({
        elders: elderRes.data,
        elderNames: elderRes.data.map(e => e.name)
      })
      if (elderRes.data.length > 0 && this.data.elderIndex < 0) {
        this.setData({ elderIndex: 0 })
      }
      await this.loadHealthData()
    } catch (e) { /* ignore */ }
  },

  onElderChange(e) {
    this.setData({ elderIndex: Number(e.detail.value) })
    this.loadHealthData()
  },

  async loadHealthData() {
    const { elders, elderIndex } = this.data
    if (!elders.length || elderIndex < 0) return
    const elderId = elders[elderIndex].id
    try {
      const res = await get('/health/health-record/' + elderId)
      this.setData({ record: res.data })
    } catch (e) { /* ignore */ }
    try {
      const res = await get('/health/health-metric/list', { elderId })
      this.setData({ metrics: res.data })
    } catch (e) { /* ignore */ }
    try {
      const res = await get('/health/medication/list', { elderId })
      this.setData({ medications: res.data })
    } catch (e) { /* ignore */ }
  },

  onMetricTypeChange(e) {
    this.setData({ metricTypeIndex: Number(e.detail.value) })
  },

  onMetricValue(e) {
    this.setData({ metricValue: e.detail.value })
  },

  async addMetric() {
    const { elders, elderIndex, metricTypes, metricTypeIndex, metricValue } = this.data
    if (elderIndex < 0) {
      wx.showToast({ title: '请先选择老人', icon: 'none' })
      return
    }
    if (!metricValue) {
      wx.showToast({ title: '请输入指标值', icon: 'none' })
      return
    }
    try {
      await post('/health/health-metric/add', {
        elderId: elders[elderIndex].id,
        metricType: metricTypes[metricTypeIndex].value,
        metricValue
      })
      wx.showToast({ title: '录入成功', icon: 'success' })
      this.setData({ metricValue: '' })
      await this.loadHealthData()
    } catch (e) { /* ignore */ }
  }
})
