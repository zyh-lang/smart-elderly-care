const { get, post, put } = require('../../utils/request')

const STATUS_TEXT = {
  DISPATCHED: '已派单',
  IN_SERVICE: '服务中',
  WAIT_ACCEPT: '待验收',
  COMPLETED: '已完成',
  EVALUATED: '已评价',
  CANCELLED: '已取消',
  COMPLAINED: '已投诉'
}

Page({
  data: {
    id: null,
    workOrder: null,
    serviceOrder: null,
    statusText: STATUS_TEXT,
    canCancel: false,
    canComplaint: false,
    showEvaluate: false,
    rating: 5,
    content: '',
    showComplaint: false,
    complaintReason: ''
  },

  onLoad(options) {
    this.setData({ id: options.id })
  },

  noop() {},

  async onShow() {
    await this.load()
  },

  async load() {
    try {
      const res = await get('/order/work-order/detail/' + this.data.id)
      const wo = res.data.workOrder
      this.setData({
        workOrder: wo,
        serviceOrder: res.data.serviceOrder,
        canCancel: ['DISPATCHED', 'IN_SERVICE', 'WAIT_ACCEPT'].indexOf(wo.status) >= 0,
        canComplaint: wo.status === 'IN_SERVICE' || wo.status === 'WAIT_ACCEPT'
      })
    } catch (e) { /* ignore */ }
  },

  async onAccept() {
    try {
      await put('/order/work-order/accept/' + this.data.id)
      wx.showToast({ title: '验收成功', icon: 'success' })
      await this.load()
    } catch (e) { /* ignore */ }
  },

  toggleEvaluate() {
    this.setData({ showEvaluate: !this.data.showEvaluate })
  },

  onRating(e) {
    this.setData({ rating: Number(e.detail.value) })
  },

  onContent(e) {
    this.setData({ content: e.detail.value })
  },

  async submitEvaluate() {
    try {
      await post('/order/evaluation/create', {
        workOrderId: this.data.id,
        rating: this.data.rating,
        content: this.data.content
      })
      wx.showToast({ title: '评价成功', icon: 'success' })
      this.setData({ showEvaluate: false, content: '', rating: 5 })
      await this.load()
    } catch (e) { /* ignore */ }
  },

  toggleComplaint() {
    this.setData({ showComplaint: !this.data.showComplaint })
  },

  onReason(e) {
    this.setData({ complaintReason: e.detail.value })
  },

  async submitComplaint() {
    if (!this.data.complaintReason) {
      wx.showToast({ title: '请填写投诉原因', icon: 'none' })
      return
    }
    try {
      await post('/order/complaint/create', {
        workOrderId: this.data.id,
        reason: this.data.complaintReason
      })
      wx.showToast({ title: '投诉已提交', icon: 'success' })
      this.setData({ showComplaint: false, complaintReason: '' })
      await this.load()
    } catch (e) { /* ignore */ }
  },

  onCancel() {
    wx.showModal({
      title: '提示',
      content: '确定取消该工单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await put('/order/work-order/cancel/' + this.data.id)
            wx.showToast({ title: '已取消', icon: 'success' })
            await this.load()
          } catch (e) { /* ignore */ }
        }
      }
    })
  }
})
