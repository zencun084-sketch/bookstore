<template>
  <div class="front-layout">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-inner container">
        <router-link to="/" class="logo">
          <svg width="32" height="32" viewBox="0 0 32 32"><rect width="32" height="32" rx="6" fill="#4f46e5"/><path d="M9 8h14a1 1 0 011 1v15a1 1 0 01-1 1H9a1 1 0 01-1-1V9a1 1 0 011-1zm3 4v1h8v-1H12zm0 4v1h8v-1H12zm0 4v1h5v-1h-5z" fill="#fff"/></svg>
          <span class="logo-text">BookStore</span>
        </router-link>

        <div class="search-box">
          <el-input
            v-model="keyword"
            placeholder="搜索图书、作者、ISBN..."
            :prefix-icon="Search"
            clearable
            @keyup.enter="handleSearch"
          />
        </div>

        <nav class="nav-actions">
          <router-link to="/cart" class="nav-item">
            <el-badge :value="cartCount" :hidden="cartCount === 0" :max="99">
              <el-icon :size="22"><ShoppingCart /></el-icon>
            </el-badge>
            <span>购物车</span>
          </router-link>

          <router-link to="/favorites" class="nav-item">
            <el-icon :size="22"><Star /></el-icon>
            <span>收藏</span>
          </router-link>

          <template v-if="userStore.isLogin">
            <el-dropdown @command="handleCommand">
              <span class="nav-item user-item">
                <el-avatar :size="32" :src="userStore.userInfo?.avatar">
                  {{ userStore.userInfo?.nickname?.charAt(0) }}
                </el-avatar>
                <span>{{ userStore.userInfo?.nickname }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                  <el-dropdown-item command="favorites">我的收藏</el-dropdown-item>
                  <el-dropdown-item command="addresses">地址管理</el-dropdown-item>
                  <el-dropdown-item v-if="userStore.isAdmin" command="admin" divided>后台管理</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login" class="nav-item">
              <el-icon :size="22"><User /></el-icon>
              <span>登录</span>
            </router-link>
          </template>
        </nav>
      </div>
    </header>

    <!-- 公告条 -->
    <div v-if="notice" class="notice-bar" @click="$router.push('/profile')">
      <el-icon><Bell /></el-icon>
      <span>{{ notice }}</span>
    </div>

    <!-- 主内容 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="container">
        <div class="footer-grid">
          <div class="footer-col">
            <h4>关于我们</h4>
            <p>BookStore 是一个专注于图书销售的在线平台，致力于为读者提供优质的阅读体验。</p>
          </div>
          <div class="footer-col">
            <h4>快速链接</h4>
            <ul>
              <li><router-link to="/">首页</router-link></li>
              <li><router-link to="/cart">购物车</router-link></li>
              <li><router-link to="/orders">我的订单</router-link></li>
            </ul>
          </div>
          <div class="footer-col">
            <h4>客服支持</h4>
            <p>客服电话：400-888-8888</p>
            <p>服务时间：周一至周日 9:00-22:00</p>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2026 BookStore 在线图书销售平台 | 课程设计项目</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, ShoppingCart, Star, User, Bell } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { useCartStore } from '@/store/cart'
import { getPublishedNotices } from '@/api/content'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const keyword = ref('')
const notice = ref('')

const cartCount = computed(() => cartStore.count)

function handleSearch() {
  if (keyword.value.trim()) {
    router.push({ path: '/search', query: { keyword: keyword.value } })
  }
}

function handleCommand(command) {
  switch (command) {
    case 'profile': router.push('/profile'); break
    case 'orders': router.push('/orders'); break
    case 'favorites': router.push('/favorites'); break
    case 'addresses': router.push('/addresses'); break
    case 'admin': router.push('/admin'); break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
        .then(() => {
          userStore.logout()
          cartStore.clear()
          ElMessage.success('已退出登录')
          router.push('/')
        })
        .catch(() => {})
      break
  }
}

onMounted(async () => {
  // 加载公告
  try {
    const res = await getPublishedNotices()
    if (res.data && res.data.length > 0) {
      notice.value = res.data[0].title
    }
  } catch (e) { /* ignore */ }

  // 已登录则加载购物车
  if (userStore.isLogin) {
    cartStore.fetchCart().catch(() => {})
  }
})
</script>

<style scoped>
.front-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: #fff;
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-inner {
  height: 64px;
  display: flex;
  align-items: center;
  gap: 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.logo-text {
  font-size: 22px;
  font-weight: 700;
  color: var(--primary-color);
}

.search-box {
  flex: 1;
  max-width: 480px;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-shrink: 0;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--text-regular);
  cursor: pointer;
  transition: color 0.2s;
  font-size: 14px;
}

.nav-item:hover {
  color: var(--primary-color);
}

.user-item {
  gap: 8px;
}

.notice-bar {
  background: linear-gradient(90deg, #4f46e5, #6366f1);
  color: #fff;
  text-align: center;
  padding: 8px 20px;
  font-size: 13px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.main-content {
  flex: 1;
}

.footer {
  background: #1f2937;
  color: #9ca3af;
  padding: 40px 0 20px;
  margin-top: 40px;
}

.footer-grid {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 40px;
  margin-bottom: 30px;
}

.footer-col h4 {
  color: #fff;
  margin-bottom: 12px;
  font-size: 16px;
}

.footer-col p, .footer-col li {
  font-size: 14px;
  line-height: 1.8;
}

.footer-col a:hover {
  color: #fff;
}

.footer-bottom {
  border-top: 1px solid #374151;
  padding-top: 20px;
  text-align: center;
  font-size: 13px;
}

@media (max-width: 768px) {
  .header-inner { gap: 12px; }
  .search-box { max-width: none; }
  .nav-item span { display: none; }
  .footer-grid { grid-template-columns: 1fr; gap: 20px; }
}
</style>
