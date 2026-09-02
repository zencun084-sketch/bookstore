import request from '@/utils/request'

export function getBookReviews(bookId, params) {
  return request.get(`/reviews/book/${bookId}`, { params })
}

export function getAvgRating(bookId) {
  return request.get(`/reviews/book/${bookId}/rating`)
}

export function addReview(data) {
  return request.post('/reviews', data)
}

export function likeReview(id) {
  return request.put(`/reviews/${id}/like`)
}

// 后台
export function getReviewPage(params) {
  return request.get('/reviews/page', { params })
}

export function updateReviewStatus(id, status) {
  return request.put(`/reviews/${id}/status`, null, { params: { status } })
}

export function deleteReview(id) {
  return request.delete(`/reviews/${id}`)
}
