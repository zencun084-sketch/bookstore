<template>
  <div class="order-detail-page container" v-loading="loading">
    <template v-if="order">
      <el-page-header @back="$router.back()" content="订单详情" class="page-header" />

      <div class="status-banner" :class="`status-${order.status}`">
        <h2>{{ order.statusText }}</h2>
        <p>订单号：{{ order.orderNo }}</p>
      </div>

      <div class="section">
        <h3>收货信息</h3>
        <div class="address-info">
          <p><b>{{ order.receiver }}</b> {{ order.phone }}</p>
          <p class="text-muted">{{ order.address }}</p>
        </div>
      </div>

      <div class="section">
        <h3>商品清单</h3>
        <div v-for="item in order.items" :key="item.id" class="order-item" @click="$router.push(`/book/${item.bookId}`)">
          <div class="item-cover">
            <img v-if="item.bookCover" :src="item.bookCover" />
            <div v-else class="cover-placeholder">{{ item.bookTitle?.charAt(0) }}</div>
          </div>
          <div class="item-info">
            <h4>{{ item.bookTitle }}</h4>
          </div>
          <div class="item-price">¥{{ item.price }}</div>
          <div class="item-qty">x{{ item.quantity }}</div>
          <div class="item-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
        </div>
      </div>

      <div class="section">
        <h3>订单信息</h3>
        <div class="info-grid">
          <div><span class="label">下单时间：</span>{{ order.createTime }}</div>
          <div v-if="order.payTime"><span class="label">支付时间：</span>{{ order.payTime }}</div>
          <div v-if="order.shipTime"><span class="label">发货时间：</span>{{ order.shipTime }}</div>
          <div v-if="order.finishTime"><span class="label">完成时间：</span>{{ order.finishTime }}</div>
          <div v-if="order.cancelTime"><span class="label">取消时间：</span>{{ order.cancelTime }}</div>
          <div v-if="order.remark"><span class="label">备注：</span>{{ order.remark }}</div>
        </div>
      </div>

      <div class="section total-section">
        <div class="total-row">
          <span>商品总额</span>
          <span class="price">¥{{ order.totalAmount }}</span>
        </div>
        <div class="total-row grand">
          <span>实付款</span>
          <span class="price">¥{{ order.totalAmount }}</span>
        </div>
      </div>

      <div class="action-bar">
        <el-button v-if="order.status === 0" type="primary" @click="handlePay">立即付款</el-button>
        <el-button v-if="order.status === 0" @click="handleCancel">取消订单</el-button>
        <el-button v-if="order.status === 2" type="primary" @click="handleConfirm">确认收货</el-button>
        <el-button v-if="order.status === 3" @click="handleRepurchase">再次购买</el-button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderDetail, payOrder, cancelOrder, confirmOrder, repurchase } from '@/api/order'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const loading = ref(false)

async function fetchOrder() {
  loading.value = true
  try {
    const res = await getOrderDetail(route.params.id)
    order.value = res.data
  } catch (e) { /* ignore */ }
  loading.value = false
}

async function handlePay() {
  try { await payOrder(order.value.id); ElMessage.success('支付成功'); fetchOrder() } catch (e) {}
}
async function handleCancel() {
  try { await ElMessageBox.confirm('确定取消？', '提示', { type: 'warning' }); await cancelOrder(order.value.id); ElMessage.success('已取消'); fetchOrder() } catch (e) {}
}
async function handleConfirm() {
  try { await ElMessageBox.confirm('确认收货？', '提示', { type: 'info' }); await confirmOrder(order.value.id); ElMessage.success('已确认'); fetchOrder() } catch (e) {}
}
async function handleRepurchase() {
  try { await repurchase(order.value.id); ElMessage.success('已加入购物车'); router.push('/cart') } catch (e) {}
}

onMounted(fetchOrder)
</script>

<style scoped>
.order-detail-page { padding: 20px 0 80px; }
.page-header { margin-bottom: 20px; }

.status-banner { background: #fff; border-radius: var(--radius-md); padding: 24px; margin-bottom: 20px; box-shadow: var(--shadow-sm); }
.status-banner h2 { font-size: 22px; margin-bottom: 8px; }
.status-0 h2 { color: #f59e0b; }
.status-1 h2 { color: #3b82f6; }
.status-2 h2 { color: #8b5cf6; }
.status-3 h2 { color: #10b981; }
.status-4 h2 { color: var(--text-secondary); }

.section { background: #fff; border-radius: var(--radius-md); padding: 20px; margin-bottom: 16px; box-shadow: var(--shadow-sm); }
.section h3 { font-size: 16px; margin-bottom: 16px; }
.text-muted { color: var(--text-secondary); font-size: 14px; }

.order-item { display: grid; grid-template-columns: 60px 1fr 80px 60px 80px; align-items: center; gap: 16px; padding: 12px 0; border-bottom: 1px solid #f0f0f0; cursor: pointer; }
.item-cover { width: 50px; height: 70px; border-radius: 4px; overflow: hidden; background: #f3f4f6; }
.item-cover img { width: 100%; height: 100%; object-fit: cover; }
.cover-placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 16px; color: #d1d5db; }

.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; font-size: 14px; }
.info-grid .label { color: var(--text-secondary); }

.total-section { text-align: right; }
.total-row { display: flex; justify-content: flex-end; gap: 40px; padding: 8px 0; font-size: 14px; }
.total-row.grand { font-size: 18px; font-weight: 700; border-top: 1px solid #f0f0f0; padding-top: 12px; margin-top: 8px; }
.total-row .price { color: #ef4444; min-width: 100px; text-align: right; }

.action-bar { display: flex; justify-content: center; gap: 12px; margin-top: 20px; }
</style>
