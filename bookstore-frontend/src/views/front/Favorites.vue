<template>
  <div class="favorites-page container">
    <h1 class="page-title">我的收藏</h1>

    <div v-loading="loading">
      <div class="book-grid" v-if="favorites.length > 0">
        <div v-for="fav in favorites" :key="fav.id" class="book-card">
          <div class="book-cover" @click="$router.push(`/book/${fav.bookId}`)">
            <img v-if="fav.book?.cover" :src="fav.book.cover" />
            <div v-else class="cover-placeholder">{{ fav.book?.title?.charAt(0) }}</div>
          </div>
          <div class="book-info">
            <h3 class="book-title" @click="$router.push(`/book/${fav.bookId}`)">{{ fav.book?.title }}</h3>
            <p class="book-author">{{ fav.book?.author }}</p>
            <div class="book-price">
              <span class="price-now">¥{{ fav.book?.price }}</span>
            </div>
          </div>
          <div class="book-actions">
            <el-button type="primary" size="small" :disabled="fav.book?.stock === 0" @click="handleAddCart(fav)">加入购物车</el-button>
            <el-button text type="danger" size="small" @click="handleRemove(fav)">取消收藏</el-button>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无收藏" :image-size="120">
        <el-button type="primary" @click="$router.push('/')">去逛逛</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyFavorites, removeFavorite } from '@/api/favorite'
import { addToCart } from '@/api/cart'
import { useCartStore } from '@/store/cart'

const cartStore = useCartStore()
const favorites = ref([])
const loading = ref(false)

async function fetchFavorites() {
  loading.value = true
  try {
    const res = await getMyFavorites()
    favorites.value = res.data || []
  } catch (e) { /* ignore */ }
  loading.value = false
}

async function handleAddCart(fav) {
  try {
    await addToCart(fav.bookId, 1)
    await cartStore.fetchCart()
    ElMessage.success('已加入购物车')
  } catch (e) { /* ignore */ }
}

async function handleRemove(fav) {
  try {
    await ElMessageBox.confirm('确定取消收藏？', '提示', { type: 'warning' })
    await removeFavorite(fav.bookId)
    ElMessage.success('已取消收藏')
    fetchFavorites()
  } catch (e) { /* ignore */ }
}

onMounted(fetchFavorites)
</script>

<style scoped>
.favorites-page { padding: 20px 0; }
.page-title { font-size: 24px; margin-bottom: 20px; }

.book-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.book-card { background: #fff; border-radius: var(--radius-md); overflow: hidden; box-shadow: var(--shadow-sm); display: flex; flex-direction: column; }
.book-cover { height: 200px; background: #f3f4f6; cursor: pointer; overflow: hidden; }
.book-cover img { width: 100%; height: 100%; object-fit: cover; }
.cover-placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 40px; font-weight: 700; color: #d1d5db; }
.book-info { padding: 12px; flex: 1; }
.book-title { font-size: 14px; cursor: pointer; margin-bottom: 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.book-title:hover { color: var(--primary-color); }
.book-author { font-size: 12px; color: var(--text-secondary); margin-bottom: 8px; }
.price-now { font-size: 18px; color: #ef4444; font-weight: 700; }
.book-actions { padding: 0 12px 12px; display: flex; flex-direction: column; gap: 4px; }

@media (max-width: 768px) { .book-grid { grid-template-columns: repeat(2, 1fr); } }
</style>
