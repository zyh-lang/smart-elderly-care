const { get, put } = require('../../utils/request')

Page({
  data: {
    messages: [],
    current: 1,
    size: 10,
    total: 0
  },

  async onShow() {
    if (!wx.getStorageSync('token')) {
      wx.reLaunch({ url: '/pages/login/login' })
      return
    }
    await this.load()
  },

  async load() {
    try {
      const res = await get('/notify/message/my', {
        current: this.data.current,
        size: this.data.size
      })
      this.setData({
        messages: res.data.records,
        total: res.data.total
      })
    } catch (e) { /* ignore */ }
  },

  async onRead(e) {
    const { id, index } = e.currentTarget.dataset
    if (this.data.messages[index].readFlag === 1) return
    try {
      await put('/notify/message/read/' + id)
      this.setData({ ['messages[' + index + '].readFlag']: 1 })
    } catch (e) { /* ignore */ }
  },

  onReachBottom() {
    if (this.data.messages.length >= this.data.total) return
    this.setData({ current: this.data.current + 1 })
    this.load()
  }
})
