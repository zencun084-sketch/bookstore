import request from '@/utils/request'

export function getCategoryList() {
  return request.get('/categories', { silent: true })
}

export function getAllCategories() {
  return request.get('/categories/all')
}

export function addCategory(data) {
  return request.post('/categories', data)
}

export function updateCategory(data) {
  return request.put('/categories', data)
}

export function deleteCategory(id) {
  return request.delete(`/categories/${id}`)
}
