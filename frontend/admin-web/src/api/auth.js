import request from './request'

// 登录
export const login = (data) => request.post('/user/auth/login', data)

// 退出登录
export const logout = () => request.post('/user/auth/logout')
