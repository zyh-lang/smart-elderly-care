const { baseUrl } = require('./config')

/**
 * 封装 wx.request：自动携带 token、统一处理业务码。
 */
const request = (url, method = 'GET', data = {}) => {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    wx.request({
      url: baseUrl + url,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? 'Bearer ' + token : ''
      },
      success(res) {
        const body = res.data
        if (body && body.code === 200) {
          resolve(body)
        } else if (body && body.code === 401) {
          wx.removeStorageSync('token')
          wx.showToast({ title: '请重新登录', icon: 'none' })
          setTimeout(() => wx.reLaunch({ url: '/pages/login/login' }), 800)
          reject(body)
        } else {
          wx.showToast({ title: (body && body.message) || '操作失败', icon: 'none' })
          reject(body)
        }
      },
      fail(err) {
        wx.showToast({ title: '网络错误，请检查后端服务', icon: 'none' })
        reject(err)
      }
    })
  })
}

module.exports = {
  request,
  get: (url, data) => request(url, 'GET', data),
  post: (url, data) => request(url, 'POST', data),
  put: (url, data) => request(url, 'PUT', data)
}
