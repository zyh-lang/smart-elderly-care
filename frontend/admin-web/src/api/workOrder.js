import request from './request'

// 工单分页（管理员/工作人员）
export const getWorkOrderPage = (params) => request.get('/order/work-order/page', { params })

// 取消工单
export const cancelWorkOrder = (id) => request.put(`/order/work-order/cancel/${id}`)
