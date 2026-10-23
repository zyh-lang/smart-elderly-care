const { post } = require('../../utils/request')

Page({
  data: {
    username: '',
    password: '',
    loading: false
  },

  onUsername(e) {
    this.setData({ username: e.detail.value })
  },

  onPassword(e) {
    this.setData({ password: e.detail.value })
  },

  async onLogin() {
    const { username, password } = this.data
    if (!username || !password) {
      wx.showToast({ title: '请输入用户名和密码', icon: 'none' })
      return
    }
    this.setData({ loading: true })
    try {
      const res = await post('/user/auth/login', { username, password })
      wx.setStorageSync('token', res.data.token)
      wx.setStorageSync('username', res.data.username)
      wx.setStorageSync('realName', res.data.realName)
      wx.showToast({ title: '登录成功', icon: 'success' })
      setTimeout(() => {
        wx.switchTab({ url: '/pages/index/index' })
      }, 600)
    } catch (e) {
      // 错误提示由 request 统一处理
    } finally {
      this.setData({ loading: false })
    }
  }
})
