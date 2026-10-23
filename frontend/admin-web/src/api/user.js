import request from './request'

// 当前登录用户信息
export const getUserInfo = () => request.get('/user/user/info')

// 用户分页列表（管理员）
export const getUserList = (params) => request.get('/user/user/list', { params })

// 启用/禁用用户（管理员）
export const updateUserStatus = (id, status) => request.put('/user/user/status', null, { params: { id, status } })

// 当前家属绑定的老人列表
export const getElderList = () => request.get('/user/elder/my')

// 全部老人列表（管理员/工作人员，管理端健康档案用）
export const getAllElders = () => request.get('/user/elder/list')
