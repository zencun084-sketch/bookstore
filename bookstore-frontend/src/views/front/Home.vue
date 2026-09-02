<template>
  <div class="home">
    <!-- Banner 轮播 -->
    <section class="banner-section container">
      <el-carousel height="360px" :interval="4000" arrow="hover" class="banner-carousel">
        <el-carousel-item v-for="banner in banners" :key="banner.id">
          <div class="banner-item" :style="{ background: getGradient(banner.id) }" @click="banner.linkUrl && $router.push(banner.linkUrl)">
            <div class="banner-content">
              <h2>{{ banner.title }}</h2>
              <p>探索更多精彩图书</p>
              <el-button type="primary" round>立即查看</el-button>
            </div>
          </div>
        </el-carousel-item>
        <el-carousel-item v-if="banners.length === 0">
          <div class="banner-item" :style="{ background: getGradient(0) }">
            <div class="banner-content">
              <h2>欢迎来到 BookStore</h2>
              <p>您的在线图书购物天堂</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- 分类导航 -->
    <section class="category-section container">
      <div class="section-header">
        <h2 class="section-title">图书分类</h2>
      </div>
      <div class="category-grid">
        <div v-for="cat in categories" :key="cat.id" class="category-card" @click="$router.push(`/category/${cat.id}`)">
          <div class="category-icon" :style="{ background: getCategoryColor(cat.id) }">
            {{ cat.name.charAt(0) }}
          </div>
          <span class="category-name">{{ cat.name }}</span>
        </div>
      </div>
    </section>

    <!-- 热门推荐 -->
    <section class="book-section container">
      <div class="section-header">
        <h2 class="section-title">
          <el-icon color="#ef4444"><StarFilled /></el-icon>
          热门推荐
        </h2>
        <router-link to="/search?sort=sales" class="more-link">查看更多 ></router-link>
      </div>
      <div class="book-grid" v-loading="loadingHot">
        <div v-for="book in hotBooks" :key="book.id" class="book-card" @click="$router.push(`/book/${book.id}`)">
          <div class="book-cover">
            <img v-if="book.cover" :src="book.cover" :alt="book.title" />
            <div v-else class="cover-placeholder">{{ book.title.charAt(0) }}</div>
          </div>
          <div class="book-info">
            <h3 class="book-title">{{ book.title }}</h3>
            <p class="book-author">{{ book.author }}</p>
            <div class="book-price">
              <span class="price-now">¥{{ book.price }}</span>
              <span v-if="book.originalPrice" class="price-old">¥{{ book.originalPrice }}</span>
            </div>
            <div class="book-sales">已售 {{ book.sales }} 件</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 新书推荐 -->
    <section class="book-section container">
      <div class="section-header">
        <h2 class="section-title">
          <el-icon color="#10b981"><Promotion /></el-icon>
          新书推荐
        </h2>
        <router-link to="/search?sort=create_time" class="more-link">查看更多 ></router-link>
      </div>
      <div class="book-grid" v-loading="loadingNew">
        <div v-for="book in newBooks" :key="book.id" class="book-card" @click="$router.push(`/book/${book.id}`)">
          <div class="book-cover">
            <img v-if="book.cover" :src="book.cover" :alt="book.title" />
            <div v-else class="cover-placeholder">{{ book.title.charAt(0) }}</div>
            <span class="new-badge">NEW</span>
          </div>
          <div class="book-info">
            <h3 class="book-title">{{ book.title }}</h3>
            <p class="book-author">{{ book.author }}</p>
            <div class="book-price">
              <span class="price-now">¥{{ book.price }}</span>
              <span v-if="book.originalPrice" class="price-old">¥{{ book.originalPrice }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { StarFilled, Promotion } from '@element-plus/icons-vue'
import { getBannerList } from '@/api/content'
import { getCategoryList } from '@/api/category'
import { getHotBooks, getNewBooks } from '@/api/book'

const banners = ref([])
const categories = ref([])
const hotBooks = ref([])
const newBooks = ref([])
const loadingHot = ref(false)
const loadingNew = ref(false)

const gradients = [
  'linear-gradient(135deg, #667eea, #764ba2)',
  'linear-gradient(135deg, #f093fb, #f5576c)',
  'linear-gradient(135deg, #4facfe, #00f2fe)',
  'linear-gradient(135deg, #43e97b, #38f9d7)',
  'linear-gradient(135deg, #fa709a, #fee140)',
]
const catColors = ['#4f46e5', '#0891b2', '#059669', '#d97706', '#dc2626', '#7c3aed', '#db2777', '#2563eb']

function getGradient(id) {
  return gradients[id % gradients.length]
}
function getCategoryColor(id) {
  return catColors[id % catColors.length]
}

onMounted(async () => {
  try {
    const [bannerRes, catRes] = await Promise.all([
      getBannerList(),
      getCategoryList(),
    ])
    banners.value = bannerRes.data || []
    categories.value = catRes.data || []
  } catch (e) { /* ignore */ }

  loadingHot.value = true
  loadingNew.value = true
  try {
    const [hotRes, newRes] = await Promise.all([
      getHotBooks(10),
      getNewBooks(10),
    ])
    hotBooks.value = hotRes.data || []
    newBooks.value = newRes.data || []
  } catch (e) { /* ignore */ }
  loadingHot.value = false
  loadingNew.value = false
})
</script>

<style scoped>
.home { padding: 20px 0; }

.banner-section { margin-bottom: 30px; }
.banner-carousel { border-radius: var(--radius-lg); overflow: hidden; }
.banner-item {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
.banner-content { text-align: center; color: #fff; }
.banner-content h2 { font-size: 36px; margin-bottom: 12px; }
.banner-content p { font-size: 18px; margin-bottom: 20px; opacity: 0.9; }

.category-section { margin-bottom: 40px; }
.category-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 16px;
}
.category-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 8px;
  background: #fff;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: var(--shadow-sm);
}
.category-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}
.category-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 22px;
  font-weight: 700;
}
.category-name { font-size: 14px; font-weight: 500; }

.book-section { margin-bottom: 40px; }
.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
.section-title {
  font-size: 22px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 8px;
}
.more-link { color: var(--primary-color); font-size: 14px; }
.more-link:hover { text-decoration: underline; }

.book-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}
.book-card {
  background: #fff;
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: var(--shadow-sm);
}
.book-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}
.book-cover {
  position: relative;
  height: 220px;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.book-cover img { width: 100%; height: 100%; object-fit: cover; }
.cover-placeholder {
  font-size: 48px;
  font-weight: 700;
  color: #d1d5db;
}
.new-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: #10b981;
  color: #fff;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 600;
}
.book-info { padding: 12px; }
.book-title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.book-author { font-size: 12px; color: var(--text-secondary); margin-bottom: 8px; }
.book-price { display: flex; align-items: baseline; gap: 6px; margin-bottom: 4px; }
.price-now { font-size: 18px; color: #ef4444; font-weight: 700; }
.price-old { font-size: 12px; color: var(--text-placeholder); text-decoration: line-through; }
.book-sales { font-size: 12px; color: var(--text-secondary); }

@media (max-width: 1200px) {
  .category-grid { grid-template-columns: repeat(4, 1fr); }
  .book-grid { grid-template-columns: repeat(4, 1fr); }
}
@media (max-width: 768px) {
  .category-grid { grid-template-columns: repeat(4, 1fr); }
  .book-grid { grid-template-columns: repeat(2, 1fr); }
  .banner-content h2 { font-size: 24px; }
}
</style>
