<template>
  <div>
    <div class="toolbar">
      <el-input v-model="orderNo" placeholder="搜索订单号" style="width: 240px" clearable @keyup.enter="fetchData" />
      <el-select v-model="statusFilter" placeholder="订单状态" clearable style="width: 140px" @change="fetchData">
        <el-option label="待付款" :value="0" />
        <el-option label="待发货" :value="1" />
        <el-option label="已发货" :value="2" />
        <el-option label="已完成" :value="3" />
        <el-option label="已取消" :value="4" />
      </el-select>
      <el-button type="primary" @click="fetchData">搜索</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="userNickname" label="用户" width="100" />
      <el-table-column prop="totalAmount" label="金额" width="100">
        <template #default="{ row }">¥{{ row.totalAmount }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ row.statusText }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="receiver" label="收货人" width="100" />
      <el-table-column prop="phone" label="电话" width="130" />
      <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
      <el-table-column prop="createTime" label="下单时间" width="170" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" text type="primary" size="small" @click="handleShip(row)">发货</el-button>
          <el-button text size="small" @click="handleViewDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:current-page="current" :page-size="10" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </div>

    <el-dialog v-model="detailVisible" title="订单详情" width="600px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ detail.statusText }}</el-descriptions-item>
          <el-descriptions-item label="收货人">{{ detail.receiver }}</el-descriptions-item>
          <el-descriptions-item label="电话">{{ detail.phone }}</el-descriptions-item>
          <el-descriptions-item label="地址" :span="2">{{ detail.address }}</el-descriptions-item>
          <el-descriptions-item label="总金额">¥{{ detail.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ detail.createTime }}</el-descriptions-item>
        </el-descriptions>
        <h4 style="margin: 16px 0 8px">商品明细</h4>
        <el-table :data="detail.items" size="small">
          <el-table-column prop="bookTitle" label="书名" min-width="160" />
          <el-table-column prop="price" label="单价" width="80" />
          <el-table-column prop="quantity" label="数量" width="60" />
        </el-table>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderPage, shipOrder, getOrderDetail } from '@/api/order'

const list = ref([])
const total = ref(0)
const current = ref(1)
const orderNo = ref('')
const statusFilter = ref(null)
const loading = ref(false)
const detailVisible = ref(false)
const detail = ref(null)

function statusType(status) {
  return ['', 'warning', 'primary', 'success', 'info'][status] || ''
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getOrderPage({ current: current.value, size: 10, status: statusFilter.value ?? undefined, orderNo: orderNo.value || undefined })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) { /* ignore */ }
  loading.value = false
}

async function handleShip(row) {
  try {
    await ElMessageBox.confirm(`确认发货？订单号：${row.orderNo}`, '提示', { type: 'info' })
    await shipOrder(row.id)
    ElMessage.success('发货成功')
    fetchData()
  } catch (e) { /* ignore */ }
}

async function handleViewDetail(row) {
  try {
    const res = await getOrderDetail(row.id)
    detail.value = res.data
    detailVisible.value = true
  } catch (e) { /* ignore */ }
}

onMounted(fetchData)
</script>

<style scoped>
.toolbar { margin-bottom: 16px; display: flex; gap: 12px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
