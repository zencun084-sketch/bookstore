import request from '@/utils/request'

export function getOverview() {
  return request.get('/dashboard/overview')
}

export function getOrderTrend(days = 7) {
  return request.get('/dashboard/order-trend', { params: { days } })
}

export function getSalesTrend(days = 7) {
  return request.get('/dashboard/sales-trend', { params: { days } })
}

export function getHotBooksRank(limit = 10) {
  return request.get('/dashboard/hot-books', { params: { limit } })
}

export function getCategorySales() {
  return request.get('/dashboard/category-sales')
}
