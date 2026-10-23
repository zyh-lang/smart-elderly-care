import request from './request'

// 登录
export const login = (data) => request.post('/user/auth/login', data)

// 注册/新增用户（管理员新增用户复用该接口）
export const register = (data) => request.post('/user/auth/register', data)

// 退出登录
export const logout = () => request.post('/user/auth/logout')
