<template>
  <div class="dashboard">
    <!-- 概览卡片 -->
    <div class="stat-cards">
      <div class="stat-card" v-for="item in statCards" :key="item.label">
        <div class="stat-icon" :style="{ background: item.color }">
          <el-icon :size="28"><component :is="item.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-label">{{ item.label }}</p>
          <p class="stat-value">{{ item.value }}</p>
        </div>
      </div>
    </div>

    <!-- 图表区 -->
    <div class="chart-row">
      <div class="chart-card">
        <h3>近7天订单趋势</h3>
        <div ref="orderChartRef" class="chart-box"></div>
      </div>
      <div class="chart-card">
        <h3>近7天销售趋势</h3>
        <div ref="salesChartRef" class="chart-box"></div>
      </div>
    </div>

    <!-- 热门图书 -->
    <div class="chart-card">
      <h3>热门图书销量排行 TOP10</h3>
      <div ref="hotBooksChartRef" class="chart-box" style="height: 400px"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { Goods, Money, User, DataAnalysis } from '@element-plus/icons-vue'
import { getOverview, getOrderTrend, getSalesTrend, getHotBooksRank } from '@/api/dashboard'

const orderChartRef = ref(null)
const salesChartRef = ref(null)
const hotBooksChartRef = ref(null)

const statCards = ref([
  { label: '今日订单', value: 0, icon: 'Goods', color: 'linear-gradient(135deg, #667eea, #764ba2)' },
  { label: '今日销售额', value: '¥0', icon: 'Money', color: 'linear-gradient(135deg, #f093fb, #f5576c)' },
  { label: '今日新用户', value: 0, icon: 'User', color: 'linear-gradient(135deg, #4facfe, #00f2fe)' },
  { label: '总用户数', value: 0, icon: 'DataAnalysis', color: 'linear-gradient(135deg, #43e97b, #38f9d7)' },
])

let orderChart, salesChart, hotBooksChart

onMounted(async () => {
  await fetchOverview()
  await fetchOrderTrend()
  await fetchSalesTrend()
  await fetchHotBooks()
})

async function fetchOverview() {
  try {
    const res = await getOverview()
    const d = res.data
    statCards.value[0].value = d.todayOrders || 0
    statCards.value[1].value = '¥' + (d.todaySales || 0)
    statCards.value[2].value = d.todayNewUsers || 0
    statCards.value[3].value = d.totalUsers || 0
  } catch (e) { /* ignore */ }
}

async function fetchOrderTrend() {
  try {
    const res = await getOrderTrend(7)
    const d = res.data
    await nextTick()
    orderChart = echarts.init(orderChartRef.value)
    orderChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 20, top: 20, bottom: 30 },
      xAxis: { type: 'category', data: d.dates, axisLabel: { fontSize: 11 } },
      yAxis: { type: 'value' },
      series: [{
        data: d.counts,
        type: 'line',
        smooth: true,
        areaStyle: { color: 'rgba(79, 70, 229, 0.15)' },
        lineStyle: { color: '#4f46e5', width: 2 },
        itemStyle: { color: '#4f46e5' },
      }],
    })
  } catch (e) { /* ignore */ }
}

async function fetchSalesTrend() {
  try {
    const res = await getSalesTrend(7)
    const d = res.data
    await nextTick()
    salesChart = echarts.init(salesChartRef.value)
    salesChart.setOption({
      tooltip: { trigger: 'axis', formatter: (p) => `${p[0].name}<br/>销售额: ¥${p[0].value}` },
      grid: { left: 50, right: 20, top: 20, bottom: 30 },
      xAxis: { type: 'category', data: d.dates, axisLabel: { fontSize: 11 } },
      yAxis: { type: 'value' },
      series: [{
        data: d.amounts,
        type: 'bar',
        barWidth: '50%',
        itemStyle: {
          borderRadius: [4, 4, 0, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#f5576c' },
            { offset: 1, color: '#f093fb' },
          ]),
        },
      }],
    })
  } catch (e) { /* ignore */ }
}

async function fetchHotBooks() {
  try {
    const res = await getHotBooksRank(10)
    const d = res.data
    await nextTick()
    hotBooksChart = echarts.init(hotBooksChartRef.value)
    const titles = (d.titles || []).reverse()
    const sales = (d.sales || []).reverse()
    hotBooksChart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: 150, right: 40, top: 20, bottom: 30 },
      xAxis: { type: 'value' },
      yAxis: { type: 'category', data: titles, axisLabel: { fontSize: 12 } },
      series: [{
        data: sales,
        type: 'bar',
        barWidth: '60%',
        itemStyle: {
          borderRadius: [0, 4, 4, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#4facfe' },
            { offset: 1, color: '#00f2fe' },
          ]),
        },
        label: { show: true, position: 'right', formatter: '{c} 件' },
      }],
    })
  } catch (e) { /* ignore */ }
}
</script>

<style scoped>
.dashboard { }

.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}
.stat-card {
  background: #fff;
  border-radius: var(--radius-md);
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: var(--shadow-sm);
}
.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}
.stat-label { font-size: 13px; color: var(--text-secondary); margin-bottom: 4px; }
.stat-value { font-size: 26px; font-weight: 700; }

.chart-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}
.chart-card {
  background: #fff;
  border-radius: var(--radius-md);
  padding: 20px;
  box-shadow: var(--shadow-sm);
}
.chart-card h3 { font-size: 16px; margin-bottom: 16px; }
.chart-box { height: 300px; }

@media (max-width: 1200px) {
  .stat-cards { grid-template-columns: repeat(2, 1fr); }
  .chart-row { grid-template-columns: 1fr; }
}
</style>
