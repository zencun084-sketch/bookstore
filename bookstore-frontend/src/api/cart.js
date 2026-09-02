import request from '@/utils/request'

export function getCartList() {
  return request.get('/cart')
}

export function addToCart(bookId, quantity = 1) {
  return request.post('/cart/items', null, { params: { bookId, quantity } })
}

export function updateCartQuantity(id, quantity) {
  return request.put(`/cart/items/${id}/quantity`, null, { params: { quantity } })
}

export function updateCartChecked(id, checked) {
  return request.put(`/cart/items/${id}/checked`, null, { params: { checked } })
}

export function checkAll(checked) {
  return request.put('/cart/checkAll', null, { params: { checked } })
}

export function removeCartItem(id) {
  return request.delete(`/cart/items/${id}`)
}

export function removeCartItems(ids) {
  return request.delete('/cart/items', { data: ids })
}

export function getCartSummary() {
  return request.get('/cart/summary')
}
