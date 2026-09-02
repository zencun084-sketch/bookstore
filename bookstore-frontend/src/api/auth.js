import request from '@/utils/request'

export function login(data) {
  return request.post('/auth/login', data)
}

export function register(data) {
  return request.post('/auth/register', data)
}

export function logout() {
  return request.post('/auth/logout')
}

export function getUserInfo() {
  return request.get('/users/info')
}

export function updateProfile(data) {
  return request.put('/users/profile', data)
}

export function changePassword(data) {
  return request.put('/users/password', data)
}

export function updateAvatar(avatar) {
  return request.put('/users/avatar', null, { params: { avatar } })
}

// 后台
export function getUserPage(params) {
  return request.get('/users/page', { params })
}

export function updateUserStatus(id, status) {
  return request.put(`/users/${id}/status`, null, { params: { status } })
}
