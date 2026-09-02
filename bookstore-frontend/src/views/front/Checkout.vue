<template>
  <div class="checkout-page container" v-loading="loading">
    <h1 class="page-title">确认订单</h1>

    <template v-if="cartItems.length > 0">
      <!-- 收货地址 -->
      <div class="section">
        <h2 class="section-title">收货地址</h2>
        <div class="address-list">
          <div v-for="addr in addresses" :key="addr.id"
               :class="['address-card', selectedAddress === addr.id ? 'selected' : '']"
               @click="selectedAddress = addr.id">
            <div class="address-info">
              <span class="receiver">{{ addr.receiver }}</span>
              <span class="phone">{{ addr.phone }}</span>
              <el-tag v-if="addr.isDefault" size="small" type="danger">默认</el-tag>
            </div>
            <p class="address-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}</p>
          </div>
          <div class="address-card add-card" @click="$router.push('/addresses')">
            <el-icon :size="24"><Plus /></el-icon>
            <span>新增地址</span>
          </div>
        </div>
      </div>

      <!-- 商品清单 -->
      <div class="section">
        <h2 class="section-title">商品清单</h2>
        <div class="order-items">
          <div v-for="item in cartItems" :key="item.id" class="order-item">
            <div class="item-cover">
              <img v-if="item.book?.cover" :src="item.book.cover" />
              <div v-else class="cover-placeholder">{{ item.book?.title?.charAt(0) }}</div>
            </div>
            <div class="item-info">
              <h3>{{ item.book?.title }}</h3>
              <p>{{ item.book?.author }}</p>
            </div>
            <div class="item-price">¥{{ item.book?.price }}</div>
            <div class="item-qty">x{{ item.quantity }}</div>
            <div class="item-subtotal">¥{{ (item.book?.price * item.quantity).toFixed(2) }}</div>
          </div>
        </div>
      </div>

      <!-- 备注 -->
      <div class="section">
        <h2 class="section-title">订单备注</h2>
        <el-input v-model="remark" type="textarea" :rows="2" placeholder="选填，给卖家留言（50字以内）" maxlength="50" show-word-limit />
      </div>

      <!-- 结算 -->
      <div class="settle-bar">
        <div class="settle-info">
          <span>共 <b>{{ totalCount }}</b> 件商品</span>
          <span class="total">应付：<b>¥{{ totalPrice }}</b></span>
        </div>
        <el-button type="primary" size="large" @click="handleSubmit" :loading="submitting">提交订单</el-button>
      </div>
    </template>

    <el-empty v-else description="购物车没有选中商品">
      <el-button type="primary" @click="$router.push('/cart')">返回购物车</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getCartList } from '@/api/cart'
import { getAddressList } from '@/api/address'
import { createOrder } from '@/api/order'
import { useCartStore } from '@/store/cart'

const router = useRouter()
const cartStore = useCartStore()

const loading = ref(false)
const submitting = ref(false)
const cartItems = ref([])
const addresses = ref([])
const selectedAddress = ref(null)
const remark = ref('')

const totalCount = computed(() => cartItems.value.reduce((sum, i) => sum + i.quantity, 0))
const totalPrice = computed(() => cartItems.value.reduce((sum, i) => sum + (i.book?.price || 0) * i.quantity, 0).toFixed(2))

async function fetchData() {
  loading.value = true
  try {
    const [cartRes, addrRes] = await Promise.all([getCartList(), getAddressList()])
    cartItems.value = (cartRes.data || []).filter(i => i.checked === 1)
    addresses.value = addrRes.data || []
    // 默认选中默认地址
    const def = addresses.value.find(a => a.isDefault === 1)
    selectedAddress.value = def?.id || addresses.value[0]?.id
  } catch (e) { /* ignore */ }
  loading.value = false
}

async function handleSubmit() {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  submitting.value = true
  try {
    const res = await createOrder({ addressId: selectedAddress.value, remark: remark.value })
    await cartStore.fetchCart()
    ElMessage.success('订单创建成功')
    router.replace(`/order/${res.data.id}`)
  } catch (e) { /* ignore */ }
  submitting.value = false
}

onMounted(fetchData)
</script>

<style scoped>
.checkout-page { padding: 20px 0 80px; }
.page-title { font-size: 24px; margin-bottom: 20px; }

.section { background: #fff; border-radius: var(--radius-md); padding: 20px; margin-bottom: 20px; box-shadow: var(--shadow-sm); }
.section-title { font-size: 16px; margin-bottom: 16px; }

.address-list { display: flex; gap: 16px; flex-wrap: wrap; }
.address-card {
  border: 2px solid #e5e7eb;
  border-radius: var(--radius-md);
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 280px;
}
.address-card:hover { border-color: var(--primary-light); }
.address-card.selected { border-color: var(--primary-color); background: #eef2ff; }
.address-info { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.receiver { font-weight: 600; font-size: 15px; }
.phone { color: var(--text-secondary); font-size: 14px; }
.address-detail { font-size: 13px; color: var(--text-regular); }

.add-card { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 8px; color: var(--text-secondary); }

.order-items { }
.order-item { display: grid; grid-template-columns: 80px 1fr 100px 80px 100px; align-items: center; gap: 16px; padding: 12px 0; border-bottom: 1px solid #f0f0f0; }
.order-item:last-child { border-bottom: none; }
.item-cover { width: 60px; height: 80px; border-radius: var(--radius-sm); overflow: hidden; background: #f3f4f6; }
.item-cover img { width: 100%; height: 100%; object-fit: cover; }
.cover-placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 20px; color: #d1d5db; }
.item-info h3 { font-size: 14px; }
.item-info p { font-size: 12px; color: var(--text-secondary); }
.item-price { color: var(--text-secondary); }
.item-qty { color: var(--text-secondary); }
.item-subtotal { color: #ef4444; font-weight: 600; }

.settle-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 16px 20px;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.06);
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 24px;
  z-index: 50;
}
.settle-info { display: flex; align-items: center; gap: 24px; font-size: 14px; }
.total b { font-size: 24px; color: #ef4444; }
</style>
