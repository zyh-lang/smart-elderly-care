import request from './request'

// 我的消息（分页）
export const getMessages = (params) => request.get('/notify/message/my', { params })

// 标记已读
export const readMessage = (id) => request.put(`/notify/message/read/${id}`)

// 未读数量
export const getUnreadCount = () => request.get('/notify/message/unread-count')
