<template>
  <div class="book-detail container" v-loading="loading">
    <template v-if="book">
      <!-- 面包屑 -->
      <el-breadcrumb :separator-icon="ArrowRight" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-if="book.categoryId" :to="{ path: `/category/${book.categoryId}` }">分类</el-breadcrumb-item>
        <el-breadcrumb-item>{{ book.title }}</el-breadcrumb-item>
      </el-breadcrumb>

      <!-- 图书信息 -->
      <div class="book-main">
        <div class="book-cover-area">
          <img v-if="book.cover" :src="book.cover" :alt="book.title" class="book-cover" />
          <div v-else class="cover-placeholder">{{ book.title.charAt(0) }}</div>
        </div>

        <div class="book-info-area">
          <h1 class="book-title">{{ book.title }}</h1>
          <p class="book-author">作者：{{ book.author }}</p>

          <div class="book-meta">
            <div class="meta-item"><span class="label">出版社：</span>{{ book.publisher || '暂无' }}</div>
            <div class="meta-item"><span class="label">ISBN：</span>{{ book.isbn || '暂无' }}</div>
            <div class="meta-item"><span class="label">出版日期：</span>{{ book.publishDate || '暂无' }}</div>
            <div class="meta-item">
              <span class="label">库存：</span>
              <span :class="book.stock > 0 ? 'in-stock' : 'out-stock'">
                {{ book.stock > 0 ? `现货 ${book.stock} 件` : '暂时缺货' }}
              </span>
            </div>
            <div class="meta-item"><span class="label">销量：</span>{{ book.sales }} 件</div>
            <div class="meta-item">
              <span class="label">评分：</span>
              <el-rate :model-value="avgRating" disabled show-score text-color="#ff9900" />
            </div>
          </div>

          <div class="price-box">
            <span class="price-label">促销价</span>
            <span class="price-now">¥{{ book.price }}</span>
            <span v-if="book.originalPrice" class="price-old">¥{{ book.originalPrice }}</span>
          </div>

          <div class="book-desc">
            <h3>内容简介</h3>
            <p>{{ book.description || '暂无简介' }}</p>
          </div>

          <div class="action-bar">
            <div class="quantity-box">
              <el-button :icon="Minus" circle size="small" @click="quantity > 1 && quantity--" :disabled="quantity <= 1" />
              <span class="quantity">{{ quantity }}</span>
              <el-button :icon="Plus" circle size="small" @click="quantity < book.stock && quantity++" :disabled="quantity >= book.stock" />
            </div>

            <el-button type="warning" size="large" :icon="ShoppingCart" @click="handleAddCart" :disabled="book.stock === 0">
              加入购物车
            </el-button>
            <el-button type="primary" size="large" @click="handleBuyNow" :disabled="book.stock === 0">
              立即购买
            </el-button>
            <el-button size="large" :icon="Star" :type="isFav ? 'danger' : 'default'" @click="handleFavorite">
              {{ isFav ? '已收藏' : '收藏' }}
            </el-button>
          </div>
        </div>
      </div>

      <!-- 评论区 -->
      <div class="review-section">
        <div class="section-header">
          <h2>用户评论 ({{ reviewTotal }})</h2>
        </div>

        <div class="review-form" v-if="userStore.isLogin">
          <el-rate v-model="reviewForm.rating" :max="5" />
          <el-input v-model="reviewForm.content" type="textarea" :rows="3" placeholder="写下您的读书感受..." maxlength="500" show-word-limit />
          <el-button type="primary" @click="submitReview">发表评论</el-button>
        </div>

        <div class="review-list">
          <div v-for="review in reviews" :key="review.id" class="review-item">
            <div class="review-header">
              <el-avatar :size="36" :src="review.userAvatar">{{ review.userNickname?.charAt(0) }}</el-avatar>
              <div class="review-user">
                <span class="user-name">{{ review.userNickname }}</span>
                <el-rate :model-value="review.rating" disabled size="small" />
              </div>
              <span class="review-time">{{ review.createTime }}</span>
            </div>
            <p class="review-content">{{ review.content }}</p>
            <div class="review-footer">
              <el-button text size="small" :icon="Pointer" @click="handleLike(review)">点赞 ({{ review.likes }})</el-button>
            </div>
          </div>
          <el-empty v-if="reviews.length === 0" description="暂无评论" />
        </div>

        <div class="pagination" v-if="reviewTotal > 10">
          <el-pagination
            v-model:current-page="reviewPage"
            :page-size="10"
            :total="reviewTotal"
            layout="prev, pager, next"
            @current-change="fetchReviews"
          />
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowRight, Minus, Plus, ShoppingCart, Star, Pointer } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { useCartStore } from '@/store/cart'
import { getBookDetail } from '@/api/book'
import { getBookReviews, getAvgRating, addReview, likeReview } from '@/api/review'
import { addFavorite, removeFavorite, checkFavorite } from '@/api/favorite'
import { addToCart } from '@/api/cart'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const book = ref(null)
const loading = ref(true)
const quantity = ref(1)
const isFav = ref(false)
const avgRating = ref(0)

const reviews = ref([])
const reviewTotal = ref(0)
const reviewPage = ref(1)
const reviewForm = reactive({ rating: 5, content: '' })

async function fetchBook() {
  loading.value = true
  try {
    const res = await getBookDetail(route.params.id)
    book.value = res.data
    fetchReviews()
    fetchRating()
    if (userStore.isLogin) fetchFavStatus()
  } catch (e) { /* ignore */ }
  loading.value = false
}

async function fetchReviews() {
  try {
    const res = await getBookReviews(route.params.id, { current: reviewPage.value, size: 10 })
    reviews.value = res.data.records || []
    reviewTotal.value = res.data.total || 0
  } catch (e) { /* ignore */ }
}

async function fetchRating() {
  try {
    const res = await getAvgRating(route.params.id)
    avgRating.value = res.data.avgRating || 0
  } catch (e) { /* ignore */ }
}

async function fetchFavStatus() {
  try {
    const res = await checkFavorite(route.params.id)
    isFav.value = res.data.isFavorite
  } catch (e) { /* ignore */ }
}

async function handleAddCart() {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  try {
    await addToCart(book.value.id, quantity.value)
    await cartStore.fetchCart()
    ElMessage.success('已加入购物车')
  } catch (e) { /* ignore */ }
}

function handleBuyNow() {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  handleAddCart().then(() => router.push('/checkout'))
}

async function handleFavorite() {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    if (isFav.value) {
      await removeFavorite(book.value.id)
      isFav.value = false
      ElMessage.success('已取消收藏')
    } else {
      await addFavorite(book.value.id)
      isFav.value = true
      ElMessage.success('收藏成功')
    }
  } catch (e) { /* ignore */ }
}

async function submitReview() {
  if (!reviewForm.content.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  try {
    await addReview({
      bookId: book.value.id,
      rating: reviewForm.rating,
      content: reviewForm.content,
    })
    ElMessage.success('评论发表成功')
    reviewForm.content = ''
    reviewForm.rating = 5
    fetchReviews()
    fetchRating()
  } catch (e) { /* ignore */ }
}

async function handleLike(review) {
  try {
    await likeReview(review.id)
    review.likes++
    ElMessage.success('点赞成功')
  } catch (e) { /* ignore */ }
}

watch(() => route.params.id, fetchBook)
onMounted(fetchBook)
</script>

<style scoped>
.book-detail { padding: 20px 0; }
.breadcrumb { margin-bottom: 20px; }

.book-main {
  display: flex;
  gap: 40px;
  background: #fff;
  border-radius: var(--radius-lg);
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: var(--shadow-sm);
}

.book-cover-area { flex-shrink: 0; }
.book-cover {
  width: 280px;
  height: 380px;
  object-fit: cover;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-md);
}
.cover-placeholder {
  width: 280px;
  height: 380px;
  background: linear-gradient(135deg, #e0e7ff, #c7d2fe);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 80px;
  font-weight: 700;
  color: #6366f1;
}

.book-info-area { flex: 1; }
.book-title { font-size: 26px; margin-bottom: 8px; }
.book-author { color: var(--text-secondary); margin-bottom: 20px; }

.book-meta {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin-bottom: 20px;
  padding: 16px;
  background: #f9fafb;
  border-radius: var(--radius-sm);
}
.meta-item { font-size: 14px; }
.meta-item .label { color: var(--text-secondary); }
.in-stock { color: var(--success-color); }
.out-stock { color: var(--danger-color); }

.price-box {
  display: flex;
  align-items: baseline;
  gap: 12px;
  padding: 20px;
  background: linear-gradient(135deg, #fff5f5, #fef2f2);
  border-radius: var(--radius-md);
  margin-bottom: 20px;
}
.price-label { color: #ef4444; font-size: 14px; font-weight: 500; }
.price-now { font-size: 32px; color: #ef4444; font-weight: 700; }
.price-old { font-size: 16px; color: var(--text-placeholder); text-decoration: line-through; }

.book-desc { margin-bottom: 24px; }
.book-desc h3 { font-size: 16px; margin-bottom: 8px; }
.book-desc p { font-size: 14px; color: var(--text-regular); line-height: 1.8; }

.action-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-top: 20px;
  border-top: 1px solid var(--border-color);
}
.quantity-box {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-right: 8px;
}
.quantity { font-size: 18px; min-width: 30px; text-align: center; }

.review-section {
  background: #fff;
  border-radius: var(--radius-lg);
  padding: 30px;
  box-shadow: var(--shadow-sm);
}
.section-header { margin-bottom: 20px; }
.section-header h2 { font-size: 20px; }

.review-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 30px;
  padding: 20px;
  background: #f9fafb;
  border-radius: var(--radius-md);
}
.review-form .el-button { align-self: flex-start; }

.review-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}
.review-item:last-child { border-bottom: none; }
.review-header { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.review-user { display: flex; flex-direction: column; gap: 4px; flex: 1; }
.user-name { font-weight: 500; font-size: 14px; }
.review-time { color: var(--text-placeholder); font-size: 12px; }
.review-content { color: var(--text-regular); line-height: 1.8; font-size: 14px; padding-left: 48px; }

.pagination { margin-top: 20px; display: flex; justify-content: center; }

@media (max-width: 768px) {
  .book-main { flex-direction: column; }
  .book-meta { grid-template-columns: 1fr; }
  .action-bar { flex-wrap: wrap; }
}
</style>
