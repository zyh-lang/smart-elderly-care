import request from './request'

// 工单分页（管理员/工作人员）
export const getWorkOrderPage = (params) => request.get('/order/work-order/page', { params })

// 我的工单（服务商/家属）
export const getMyWorkOrders = () => request.get('/order/work-order/my')

// 服务商接单（开始服务）
export const startWorkOrder = (id) => request.put(`/order/work-order/start/${id}`)

// 服务商完成服务
export const finishWorkOrder = (id) => request.put(`/order/work-order/finish/${id}`)

// 取消工单
export const cancelWorkOrder = (id) => request.put(`/order/work-order/cancel/${id}`)
