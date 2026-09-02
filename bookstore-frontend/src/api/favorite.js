import request from '@/utils/request'

export function getMyFavorites() {
  return request.get('/favorites')
}

export function addFavorite(bookId) {
  return request.post(`/favorites/${bookId}`)
}

export function removeFavorite(bookId) {
  return request.delete(`/favorites/${bookId}`)
}

export function checkFavorite(bookId) {
  return request.get(`/favorites/check/${bookId}`)
}
