import request from '@/utils/request'

export function createOrder(data) {
  return request.post('/orders', data)
}

export function getMyOrders(params) {
  return request.get('/orders/my', { params })
}

export function getOrderDetail(id) {
  return request.get(`/orders/${id}`)
}

export function payOrder(id) {
  return request.put(`/orders/${id}/pay`)
}

export function cancelOrder(id) {
  return request.put(`/orders/${id}/cancel`)
}

export function confirmOrder(id) {
  return request.put(`/orders/${id}/confirm`)
}

export function repurchase(id) {
  return request.post(`/orders/${id}/repurchase`)
}

// 后台
export function getOrderPage(params) {
  return request.get('/orders/page', { params })
}

export function shipOrder(id) {
  return request.put(`/orders/${id}/ship`)
}
