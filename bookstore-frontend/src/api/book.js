import request from '@/utils/request'

// 前台
export function getBookList(params) {
  return request.get('/books/list', { params, silent: true })
}

export function searchBooks(params) {
  return request.get('/books/search', { params, silent: true })
}

export function getBookDetail(id) {
  return request.get(`/books/detail/${id}`, { silent: true })
}

export function getHotBooks(limit = 10) {
  return request.get('/books/hot', { params: { limit }, silent: true })
}

export function getNewBooks(limit = 10) {
  return request.get('/books/new', { params: { limit }, silent: true })
}

// 后台
export function getBookPage(params) {
  return request.get('/books/page', { params })
}

export function addBook(data) {
  return request.post('/books', data)
}

export function updateBook(data) {
  return request.put('/books', data)
}

export function deleteBook(id) {
  return request.delete(`/books/${id}`)
}

export function updateBookStatus(id, status) {
  return request.put(`/books/${id}/status`, null, { params: { status } })
}
