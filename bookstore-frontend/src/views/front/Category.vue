<template>
  <div class="book-list-page container">
    <el-breadcrumb :separator-icon="ArrowRight" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>{{ categoryName }}</el-breadcrumb-item>
    </el-breadcrumb>

    <div class="filter-bar">
      <div class="sort-tabs">
        <span :class="['sort-tab', sortField === 'sales' && sortOrder === 'desc' ? 'active' : '']" @click="changeSort('sales', 'desc')">销量优先</span>
        <span :class="['sort-tab', sortField === 'create_time' && sortOrder === 'desc' ? 'active' : '']" @click="changeSort('create_time', 'desc')">最新</span>
        <span :class="['sort-tab', sortField === 'price' && sortOrder === 'asc' ? 'active' : '']" @click="changeSort('price', 'asc')">价格升序</span>
        <span :class="['sort-tab', sortField === 'price' && sortOrder === 'desc' ? 'active' : '']" @click="changeSort('price', 'desc')">价格降序</span>
      </div>
    </div>

    <div class="book-grid" v-loading="loading">
      <div v-for="book in books" :key="book.id" class="book-card" @click="$router.push(`/book/${book.id}`)">
        <div class="book-cover">
          <img v-if="book.cover" :src="book.cover" />
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

    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="current"
        :page-size="12"
        :total="total"
        layout="prev, pager, next, total"
        @current-change="fetchBooks"
      />
    </div>

    <el-empty v-if="!loading && books.length === 0" description="暂无图书" />
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowRight } from '@element-plus/icons-vue'
import { getBookList } from '@/api/book'
import { getCategoryList } from '@/api/category'

const route = useRoute()
const books = ref([])
const total = ref(0)
const current = ref(1)
const loading = ref(false)
const sortField = ref('sales')
const sortOrder = ref('desc')
const categories = ref([])
const categoryName = ref('分类')

async function fetchBooks() {
  loading.value = true
  try {
    const res = await getBookList({
      current: current.value,
      size: 12,
      categoryId: route.params.id,
      sortField: sortField.value,
      sortOrder: sortOrder.value,
    })
    books.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) { /* ignore */ }
  loading.value = false
}

function changeSort(field, order) {
  sortField.value = field
  sortOrder.value = order
  current.value = 1
  fetchBooks()
}

async function fetchCategoryName() {
  if (categories.value.length === 0) {
    try {
      const res = await getCategoryList()
      categories.value = res.data || []
    } catch (e) { /* ignore */ }
  }
  const cat = categories.value.find(c => c.id == route.params.id)
  categoryName.value = cat?.name || '分类图书'
}

watch(() => route.params.id, () => {
  current.value = 1
  fetchBooks()
  fetchCategoryName()
})

onMounted(() => {
  fetchBooks()
  fetchCategoryName()
})
</script>

<style scoped>
.book-list-page { padding: 20px 0; }
.breadcrumb { margin-bottom: 16px; }

.filter-bar {
  background: #fff;
  border-radius: var(--radius-md);
  padding: 12px 20px;
  margin-bottom: 20px;
  box-shadow: var(--shadow-sm);
}
.sort-tabs { display: flex; gap: 24px; }
.sort-tab {
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 4px 0;
  position: relative;
}
.sort-tab.active { color: var(--primary-color); font-weight: 600; }
.sort-tab.active::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--primary-color);
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  min-height: 200px;
}
.book-card {
  background: #fff;
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: var(--shadow-sm);
}
.book-card:hover { transform: translateY(-4px); box-shadow: var(--shadow-lg); }
.book-cover {
  height: 200px;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.book-cover img { width: 100%; height: 100%; object-fit: cover; }
.cover-placeholder { font-size: 40px; font-weight: 700; color: #d1d5db; }
.book-info { padding: 12px; }
.book-title { font-size: 14px; margin-bottom: 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.book-author { font-size: 12px; color: var(--text-secondary); margin-bottom: 8px; }
.book-price { display: flex; align-items: baseline; gap: 6px; }
.price-now { font-size: 18px; color: #ef4444; font-weight: 700; }
.price-old { font-size: 12px; color: var(--text-placeholder); text-decoration: line-through; }
.book-sales { font-size: 12px; color: var(--text-secondary); margin-top: 4px; }

.pagination { margin-top: 30px; display: flex; justify-content: center; }

@media (max-width: 768px) {
  .book-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
