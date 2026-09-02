import request from '@/utils/request'

export function getAddressList() {
  return request.get('/addresses')
}

export function addAddress(data) {
  return request.post('/addresses', data)
}

export function updateAddress(data) {
  return request.put('/addresses', data)
}

export function deleteAddress(id) {
  return request.delete(`/addresses/${id}`)
}

export function setDefaultAddress(id) {
  return request.put(`/addresses/${id}/default`)
}
