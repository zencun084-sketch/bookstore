<template>
  <div class="orders-page container">
    <h1 class="page-title">我的订单</h1>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="待付款" name="0" />
      <el-tab-pane label="待发货" name="1" />
      <el-tab-pane label="已发货" name="2" />
      <el-tab-pane label="已完成" name="3" />
      <el-tab-pane label="已取消" name="4" />
    </el-tabs>

    <div v-loading="loading">
      <div v-for="order in orders" :key="order.id" class="order-card" @click="$router.push(`/order/${order.id}`)">
        <div class="order-header">
          <span class="order-no">订单号：{{ order.orderNo }}</span>
          <span :class="['order-status', `status-${order.status}`]">{{ order.statusText }}</span>
        </div>
        <div class="order-body">
          <div class="order-items-preview">
            <div v-for="item in (order.items || []).slice(0, 4)" :key="item.id" class="item-cover">
              <img v-if="item.bookCover" :src="item.bookCover" />
              <div v-else class="cover-placeholder">{{ item.bookTitle?.charAt(0) }}</div>
            </div>
            <div v-if="(order.items?.length || 0) > 4" class="more-items">+{{ order.items.length - 4 }}</div>
          </div>
          <div class="order-summary">
            <p class="item-count">共 {{ (order.items || []).length }} 件商品</p>
            <p class="total-amount">¥{{ order.totalAmount }}</p>
          </div>
        </div>
        <div class="order-footer" @click.stop>
          <span class="order-time">{{ order.createTime }}</span>
          <div class="order-actions">
            <el-button v-if="order.status === 0" type="primary" size="small" @click="handlePay(order)">立即付款</el-button>
            <el-button v-if="order.status === 0" size="small" @click="handleCancel(order)">取消订单</el-button>
            <el-button v-if="order.status === 2" type="primary" size="small" @click="handleConfirm(order)">确认收货</el-button>
            <el-button v-if="order.status === 3" size="small" @click="handleRepurchase(order)">再次购买</el-button>
            <el-button size="small" @click="$router.push(`/order/${order.id}`)">查看详情</el-button>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />
    </div>

    <div class="pagination" v-if="total > 10">
      <el-pagination v-model:current-page="current" :page-size="10" :total="total" layout="prev, pager, next" @current-change="fetchOrders" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyOrders, payOrder, cancelOrder, confirmOrder, repurchase } from '@/api/order'

const activeTab = ref('all')
const orders = ref([])
const total = ref(0)
const current = ref(1)
const loading = ref(false)

async function fetchOrders() {
  loading.value = true
  try {
    const res = await getMyOrders({
      current: current.value,
      size: 10,
      status: activeTab.value === 'all' ? undefined : activeTab.value,
    })
    orders.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) { /* ignore */ }
  loading.value = false
}

function handleTabChange() {
  current.value = 1
  fetchOrders()
}

async function handlePay(order) {
  try {
    await ElMessageBox.confirm('确认支付该订单？', '模拟支付', { type: 'info' })
    await payOrder(order.id)
    ElMessage.success('支付成功')
    fetchOrders()
  } catch (e) { /* ignore */ }
}

async function handleCancel(order) {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
    await cancelOrder(order.id)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (e) { /* ignore */ }
}

async function handleConfirm(order) {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '确认收货', { type: 'info' })
    await confirmOrder(order.id)
    ElMessage.success('确认收货成功')
    fetchOrders()
  } catch (e) { /* ignore */ }
}

async function handleRepurchase(order) {
  try {
    await repurchase(order.id)
    ElMessage.success('已加入购物车')
  } catch (e) { /* ignore */ }
}

onMounted(fetchOrders)
</script>

<style scoped>
.orders-page { padding: 20px 0; }
.page-title { font-size: 24px; margin-bottom: 20px; }

.order-card {
  background: #fff;
  border-radius: var(--radius-md);
  padding: 20px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: box-shadow 0.2s;
  box-shadow: var(--shadow-sm);
}
.order-card:hover { box-shadow: var(--shadow-md); }

.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.order-no { font-size: 13px; color: var(--text-secondary); }
.order-status { font-weight: 600; font-size: 14px; }
.status-0 { color: #f59e0b; }
.status-1 { color: #3b82f6; }
.status-2 { color: #8b5cf6; }
.status-3 { color: #10b981; }
.status-4 { color: var(--text-secondary); }

.order-body { display: flex; justify-content: space-between; align-items: center; }
.order-items-preview { display: flex; gap: 8px; }
.item-cover { width: 60px; height: 80px; border-radius: var(--radius-sm); overflow: hidden; background: #f3f4f6; }
.item-cover img { width: 100%; height: 100%; object-fit: cover; }
.cover-placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 18px; color: #d1d5db; }
.more-items { width: 60px; height: 80px; display: flex; align-items: center; justify-content: center; background: #f3f4f6; border-radius: var(--radius-sm); font-size: 14px; color: var(--text-secondary); }
.order-summary { text-align: right; }
.item-count { font-size: 13px; color: var(--text-secondary); margin-bottom: 4px; }
.total-amount { font-size: 20px; font-weight: 700; color: #ef4444; }

.order-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 16px; padding-top: 16px; border-top: 1px solid #f0f0f0; }
.order-time { font-size: 12px; color: var(--text-placeholder); }
.order-actions { display: flex; gap: 8px; }

.pagination { margin-top: 20px; display: flex; justify-content: center; }
</style>
