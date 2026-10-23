App({
  globalData: {
    userInfo: null
  },
  onLaunch() {
    // 检查登录态
    const token = wx.getStorageSync('token')
    if (!token) {
      wx.reLaunch({ url: '/pages/login/login' })
    }
  }
})
