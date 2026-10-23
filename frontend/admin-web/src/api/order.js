import request from './request'

// 订单分页（管理员/工作人员）
export const getOrderPage = (params) => request.get('/order/order/page', { params })

// 智能派单
export const dispatchOrder = (id) => request.post(`/order/order/dispatch/${id}`)
