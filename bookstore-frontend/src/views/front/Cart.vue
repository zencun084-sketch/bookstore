<template>
  <div class="cart-page container">
    <h1 class="page-title">购物车</h1>

    <div v-loading="loading">
      <template v-if="cartItems.length > 0">
        <div class="cart-list">
          <div class="cart-header">
            <div class="col-check">
              <el-checkbox v-model="allChecked" @change="handleCheckAll" />
            </div>
            <div class="col-info">商品信息</div>
            <div class="col-price">单价</div>
            <div class="col-qty">数量</div>
            <div class="col-subtotal">小计</div>
            <div class="col-action">操作</div>
          </div>

          <div v-for="item in cartItems" :key="item.id" class="cart-item">
            <div class="col-check">
              <el-checkbox :model-value="item.checked === 1" @change="(val) => handleCheck(item, val)" />
            </div>
            <div class="col-info" @click="$router.push(`/book/${item.bookId}`)">
              <div class="book-cover">
                <img v-if="item.book?.cover" :src="item.book.cover" />
                <div v-else class="cover-placeholder">{{ item.book?.title?.charAt(0) }}</div>
              </div>
              <div class="book-detail">
                <h3 class="book-name">{{ item.book?.title }}</h3>
                <p class="book-author">{{ item.book?.author }}</p>
                <span v-if="item.book?.stock === 0" class="out-stock">缺货</span>
              </div>
            </div>
            <div class="col-price">¥{{ item.book?.price }}</div>
            <div class="col-qty">
              <el-input-number v-model="item.quantity" :min="1" :max="item.book?.stock || 1" size="small" @change="(val) => handleQtyChange(item, val)" />
            </div>
            <div class="col-subtotal">¥{{ (item.book?.price * item.quantity).toFixed(2) }}</div>
            <div class="col-action">
              <el-button text type="danger" :icon="Delete" @click="handleRemove(item)">删除</el-button>
            </div>
          </div>
        </div>

        <div class="cart-footer">
          <div class="footer-left">
            <el-checkbox v-model="allChecked" @change="handleCheckAll">全选</el-checkbox>
            <el-button text type="danger" @click="handleRemoveSelected">删除选中</el-button>
          </div>
          <div class="footer-right">
            <span class="total-label">已选 <b>{{ selectedCount }}</b> 件商品</span>
            <span class="total-price">合计：¥<b>{{ totalPrice }}</b></span>
            <el-button type="primary" size="large" @click="handleCheckout" :disabled="selectedCount === 0">
              去结算
            </el-button>
          </div>
        </div>
      </template>

      <el-empty v-else description="购物车空空如也" :image-size="120">
        <el-button type="primary" @click="$router.push('/')">去逛逛</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCartList, updateCartQuantity, updateCartChecked, checkAll, removeCartItem, removeCartItems } from '@/api/cart'
import { useCartStore } from '@/store/cart'

const router = useRouter()
const cartStore = useCartStore()

const cartItems = ref([])
const loading = ref(false)

const allChecked = computed({
  get: () => cartItems.value.length > 0 && cartItems.value.every(i => i.checked === 1),
  set: () => {},
})

const selectedCount = computed(() => cartItems.value.filter(i => i.checked === 1).reduce((sum, i) => sum + i.quantity, 0))
const totalPrice = computed(() => cartItems.value.filter(i => i.checked === 1).reduce((sum, i) => sum + (i.book?.price || 0) * i.quantity, 0).toFixed(2))

async function fetchCart() {
  loading.value = true
  try {
    const res = await getCartList()
    cartItems.value = res.data || []
  } catch (e) { /* ignore */ }
  loading.value = false
}

async function handleCheck(item, val) {
  try {
    await updateCartChecked(item.id, val ? 1 : 0)
    item.checked = val ? 1 : 0
  } catch (e) { /* ignore */ }
}

async function handleCheckAll(val) {
  try {
    await checkAll(val ? 1 : 0)
    cartItems.value.forEach(i => i.checked = val ? 1 : 0)
  } catch (e) { /* ignore */ }
}

async function handleQtyChange(item, val) {
  try {
    await updateCartQuantity(item.id, val)
    await cartStore.fetchCart()
  } catch (e) {
    fetchCart()
  }
}

async function handleRemove(item) {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', { type: 'warning' })
    await removeCartItem(item.id)
    ElMessage.success('删除成功')
    fetchCart()
    cartStore.fetchCart()
  } catch (e) { /* ignore */ }
}

async function handleRemoveSelected() {
  const selected = cartItems.value.filter(i => i.checked === 1)
  if (selected.length === 0) {
    ElMessage.warning('请选择要删除的商品')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selected.length} 件商品吗？`, '提示', { type: 'warning' })
    await removeCartItems(selected.map(i => i.id))
    ElMessage.success('删除成功')
    fetchCart()
    cartStore.fetchCart()
  } catch (e) { /* ignore */ }
}

function handleCheckout() {
  router.push('/checkout')
}

onMounted(fetchCart)
</script>

<style scoped>
.cart-page { padding: 20px 0; }
.page-title { font-size: 24px; margin-bottom: 20px; }

.cart-list { background: #fff; border-radius: var(--radius-md); overflow: hidden; box-shadow: var(--shadow-sm); }

.cart-header, .cart-item {
  display: grid;
  grid-template-columns: 60px 1fr 120px 160px 120px 100px;
  align-items: center;
  padding: 16px 20px;
}
.cart-header {
  background: #f9fafb;
  font-size: 13px;
  color: var(--text-secondary);
  font-weight: 500;
}
.cart-item { border-top: 1px solid #f0f0f0; }

.col-info { display: flex; align-items: center; gap: 12px; cursor: pointer; }
.book-cover {
  width: 80px;
  height: 100px;
  border-radius: var(--radius-sm);
  overflow: hidden;
  background: #f3f4f6;
  flex-shrink: 0;
}
.book-cover img { width: 100%; height: 100%; object-fit: cover; }
.cover-placeholder {
  width: 100%; height: 100%;
  display: flex; align-items: center; justify-content: center;
  font-size: 24px; font-weight: 700; color: #d1d5db;
}
.book-name { font-size: 14px; margin-bottom: 4px; }
.book-author { font-size: 12px; color: var(--text-secondary); }
.out-stock { color: var(--danger-color); font-size: 12px; }

.col-price { color: #ef4444; font-weight: 500; }
.col-subtotal { color: #ef4444; font-weight: 700; font-size: 16px; }

.cart-footer {
  margin-top: 20px;
  background: #fff;
  border-radius: var(--radius-md);
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: var(--shadow-sm);
  position: sticky;
  bottom: 0;
}
.footer-left { display: flex; align-items: center; gap: 16px; }
.footer-right { display: flex; align-items: center; gap: 20px; }
.total-label { font-size: 14px; }
.total-price { font-size: 14px; }
.total-price b { font-size: 24px; color: #ef4444; }

@media (max-width: 768px) {
  .cart-header { display: none; }
  .cart-item {
    grid-template-columns: 40px 1fr;
    gap: 8px;
  }
  .col-price, .col-qty, .col-subtotal, .col-action {
    grid-column: 2;
  }
}
</style>
