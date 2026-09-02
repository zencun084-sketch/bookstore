import request from '@/utils/request'

// Banner
export function getBannerList() {
  return request.get('/banners', { silent: true })
}

export function getAllBanners() {
  return request.get('/banners/all')
}

export function addBanner(data) {
  return request.post('/banners', data)
}

export function updateBanner(data) {
  return request.put('/banners', data)
}

export function deleteBanner(id) {
  return request.delete(`/banners/${id}`)
}

// Notice
export function getPublishedNotices() {
  return request.get('/notices/published', { silent: true })
}

export function getAllNotices() {
  return request.get('/notices/all')
}

export function addNotice(data) {
  return request.post('/notices', data)
}

export function updateNotice(data) {
  return request.put('/notices', data)
}

export function deleteNotice(id) {
  return request.delete(`/notices/${id}`)
}
