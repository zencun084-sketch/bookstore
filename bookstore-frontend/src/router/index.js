import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/front/Login.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/front/Register.vue'),
    meta: { title: '注册' },
  },
  {
    path: '/',
    component: () => import('@/layouts/FrontLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/front/Home.vue'), meta: { title: '首页' } },
      { path: 'category/:id', name: 'Category', component: () => import('@/views/front/Category.vue'), meta: { title: '分类' } },
      { path: 'search', name: 'Search', component: () => import('@/views/front/Search.vue'), meta: { title: '搜索' } },
      { path: 'book/:id', name: 'BookDetail', component: () => import('@/views/front/BookDetail.vue'), meta: { title: '图书详情' } },
      { path: 'cart', name: 'Cart', component: () => import('@/views/front/Cart.vue'), meta: { title: '购物车', requireAuth: true } },
      { path: 'checkout', name: 'Checkout', component: () => import('@/views/front/Checkout.vue'), meta: { title: '确认订单', requireAuth: true } },
      { path: 'orders', name: 'MyOrders', component: () => import('@/views/front/MyOrders.vue'), meta: { title: '我的订单', requireAuth: true } },
      { path: 'order/:id', name: 'OrderDetail', component: () => import('@/views/front/OrderDetail.vue'), meta: { title: '订单详情', requireAuth: true } },
      { path: 'profile', name: 'Profile', component: () => import('@/views/front/Profile.vue'), meta: { title: '个人中心', requireAuth: true } },
      { path: 'favorites', name: 'Favorites', component: () => import('@/views/front/Favorites.vue'), meta: { title: '我的收藏', requireAuth: true } },
      { path: 'addresses', name: 'Addresses', component: () => import('@/views/front/Addresses.vue'), meta: { title: '地址管理', requireAuth: true } },
    ],
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requireAuth: true, requireAdmin: true },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', name: 'AdminDashboard', component: () => import('@/views/admin/Dashboard.vue'), meta: { title: '控制台' } },
      { path: 'users', name: 'AdminUsers', component: () => import('@/views/admin/Users.vue'), meta: { title: '用户管理' } },
      { path: 'books', name: 'AdminBooks', component: () => import('@/views/admin/Books.vue'), meta: { title: '图书管理' } },
      { path: 'categories', name: 'AdminCategories', component: () => import('@/views/admin/Categories.vue'), meta: { title: '分类管理' } },
      { path: 'orders', name: 'AdminOrders', component: () => import('@/views/admin/Orders.vue'), meta: { title: '订单管理' } },
      { path: 'reviews', name: 'AdminReviews', component: () => import('@/views/admin/Reviews.vue'), meta: { title: '评论管理' } },
      { path: 'banners', name: 'AdminBanners', component: () => import('@/views/admin/Banners.vue'), meta: { title: 'Banner管理' } },
      { path: 'notices', name: 'AdminNotices', component: () => import('@/views/admin/Notices.vue'), meta: { title: '公告管理' } },
    ],
  },
  { path: '/:pathMatch(.*)*', redirect: '/' },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  },
})

// 路由守卫
router.beforeEach((to, from, next) => {
  NProgress.start()
  document.title = (to.meta.title ? to.meta.title + ' - ' : '') + 'BookStore'

  const userStore = useUserStore()

  if (to.meta.requireAuth && !userStore.isLogin) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  if (to.meta.requireAdmin && !userStore.isAdmin) {
    next('/')
    return
  }

  next()
})

router.afterEach(() => {
  NProgress.done()
})

export default router
