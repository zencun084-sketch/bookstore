<template>
  <div>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column type="index" label="#" width="60" />
      <el-table-column prop="bookTitle" label="图书" min-width="160" show-overflow-tooltip />
      <el-table-column prop="userNickname" label="评论人" width="100" />
      <el-table-column prop="rating" label="评分" width="140">
        <template #default="{ row }"><el-rate :model-value="row.rating" disabled size="small" /></template>
      </el-table-column>
      <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
      <el-table-column prop="likes" label="点赞" width="70" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '显示' : '隐藏' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="170" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button text :type="row.status === 1 ? 'warning' : 'success'" size="small" @click="handleToggle(row)">
            {{ row.status === 1 ? '隐藏' : '显示' }}
          </el-button>
          <el-button text type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:current-page="current" :page-size="10" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReviewPage, updateReviewStatus, deleteReview } from '@/api/review'

const list = ref([])
const total = ref(0)
const current = ref(1)
const loading = ref(false)

async function fetchData() {
  loading.value = true
  try {
    const res = await getReviewPage({ current: current.value, size: 10 })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) { /* ignore */ }
  loading.value = false
}

async function handleToggle(row) {
  try {
    await updateReviewStatus(row.id, row.status === 1 ? 0 : 1)
    ElMessage.success('操作成功')
    fetchData()
  } catch (e) { /* ignore */ }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定删除该评论？', '提示', { type: 'warning' })
    await deleteReview(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) { /* ignore */ }
}

onMounted(fetchData)
</script>

<style scoped>
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
