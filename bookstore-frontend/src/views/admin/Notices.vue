<template>
  <div>
    <div class="toolbar">
      <el-button type="success" :icon="Plus" @click="openDialog()">新增公告</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column type="index" label="#" width="60" />
      <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
      <el-table-column prop="content" label="内容" min-width="280" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ row.statusText }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="170" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button text type="primary" size="small" @click="openDialog(row)">编辑</el-button>
          <el-button v-if="row.status !== 1" text type="success" size="small" @click="handlePublish(row)">发布</el-button>
          <el-button v-if="row.status === 1" text type="warning" size="small" @click="handleOffline(row)">下线</el-button>
          <el-button text type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editing.id ? '编辑公告' : '新增公告'" width="600px">
      <el-form :model="editing" label-width="80px">
        <el-form-item label="标题" required><el-input v-model="editing.title" /></el-form-item>
        <el-form-item label="内容" required><el-input v-model="editing.content" type="textarea" :rows="5" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editing.status" style="width: 100%">
            <el-option label="未发布" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已下线" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllNotices, addNotice, updateNotice, deleteNotice } from '@/api/content'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const editing = reactive({ id: null, title: '', content: '', status: 0 })

function statusType(status) {
  return ['', 'success', 'info'][status] || ''
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getAllNotices()
    list.value = res.data || []
  } catch (e) { /* ignore */ }
  loading.value = false
}

function openDialog(row) {
  if (row) Object.assign(editing, row)
  else Object.assign(editing, { id: null, title: '', content: '', status: 0 })
  dialogVisible.value = true
}

async function handleSave() {
  if (!editing.title || !editing.content) { ElMessage.warning('请填写必填项'); return }
  try {
    if (editing.id) await updateNotice(editing)
    else await addNotice(editing)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* ignore */ }
}

async function handlePublish(row) {
  try { await updateNotice({ id: row.id, status: 1 }); ElMessage.success('已发布'); fetchData() } catch (e) {}
}

async function handleOffline(row) {
  try { await updateNotice({ id: row.id, status: 2 }); ElMessage.success('已下线'); fetchData() } catch (e) {}
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定删除该公告？', '提示', { type: 'warning' })
    await deleteNotice(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) { /* ignore */ }
}

onMounted(fetchData)
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
</style>
