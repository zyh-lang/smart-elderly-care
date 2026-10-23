import request from './request'

// 服务目录列表
export const getServiceList = () => request.get('/order/service/list')

// 新增服务
export const addService = (data) => request.post('/order/service/add', data)

// 修改服务
export const updateService = (data) => request.put('/order/service/update', data)

// 删除服务
export const deleteService = (id) => request.delete(`/order/service/${id}`)
