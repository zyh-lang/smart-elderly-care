import request from './request'

// 老人健康档案
export const getHealthRecord = (elderId) => request.get(`/health/health-record/${elderId}`)

// 保存健康档案
export const saveHealthRecord = (data) => request.post('/health/health-record/save', data)

// 指标列表
export const getMetrics = (params) => request.get('/health/health-metric/list', { params })

// 指标趋势
export const getTrend = (params) => request.get('/health/health-metric/trend', { params })

// 录入指标
export const addMetric = (data) => request.post('/health/health-metric/add', data)

// 用药列表
export const getMedications = (elderId) => request.get('/health/medication/list', { params: { elderId } })

// 添加用药
export const addMedication = (data) => request.post('/health/medication/add', data)

// 修改用药
export const updateMedication = (data) => request.put('/health/medication/update', data)

// 删除用药
export const deleteMedication = (id) => request.delete(`/health/medication/${id}`)
