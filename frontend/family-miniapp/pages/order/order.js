const { get } = require('../../utils/request')

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
    workOrders: [],
    statusText: STATUS_TEXT
  },

  async onShow() {
    if (!wx.getStorageSync('token')) {
      wx.reLaunch({ url: '/pages/login/login' })
      return
    }
    try {
      const res = await get('/order/work-order/my')
      this.setData({ workOrders: res.data })
    } catch (e) { /* ignore */ }
  },

  goDetail(e) {
    wx.navigateTo({ url: '/pages/order-detail/order-detail?id=' + e.currentTarget.dataset.id })
  }
})
