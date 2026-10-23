import request from './request'

// 汇总报表
export const getSummary = () => request.get('/statistics/report/summary')

// 日趋势
export const getTrend = (days) => request.get('/statistics/report/trend', { params: { days } })

// 服务类型排名
export const getServiceRank = () => request.get('/statistics/report/service-rank')
