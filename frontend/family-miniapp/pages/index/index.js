const { get, post } = require('../../utils/request')

Page({
  data: {
    services: [],
    elders: [],
    elderNames: [],
    elderIndex: -1,
    showOrder: false,
    orderServiceId: null,
    orderServiceName: '',
    address: ''
  },

  async onShow() {
    if (!wx.getStorageSync('token')) {
      wx.reLaunch({ url: '/pages/login/login' })
      return
    }
    try {
      const serviceRes = await get('/order/service/list')
      this.setData({ services: serviceRes.data })
    } catch (e) { /* ignore */ }
    try {
      const elderRes = await get('/user/elder/my')
      this.setData({
        elders: elderRes.data,
        elderNames: elderRes.data.map(e => e.name)
      })
    } catch (e) { /* ignore */ }
  },

  openOrder(e) {
    this.setData({
      showOrder: true,
      orderServiceId: e.currentTarget.dataset.id,
      orderServiceName: e.currentTarget.dataset.name
    })
  },

  closeOrder() {
    this.setData({ showOrder: false })
  },

  noop() {},

  onElderChange(e) {
    this.setData({ elderIndex: Number(e.detail.value) })
  },

  onAddress(e) {
    this.setData({ address: e.detail.value })
  },

  async submitOrder() {
    const { elders, elderIndex, orderServiceId, address } = this.data
    if (elderIndex < 0) {
      wx.showToast({ title: '请选择服务对象', icon: 'none' })
      return
    }
    if (!address) {
      wx.showToast({ title: '请填写服务地址', icon: 'none' })
      return
    }
    try {
      await post('/order/order/create', {
        serviceId: orderServiceId,
        elderId: elders[elderIndex].id,
        address
      })
      wx.showToast({ title: '下单成功，等待派单', icon: 'success' })
      this.setData({ showOrder: false, address: '' })
    } catch (e) { /* ignore */ }
  }
})
