<template>
  <div>
    <div class="toolbar">
      <el-button type="success" :icon="Plus" @click="openDialog()">新增分类</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column type="index" label="#" width="60" />
      <el-table-column prop="name" label="分类名称" width="200" />
      <el-table-column prop="sort" label="排序" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button text type="primary" size="small" @click="openDialog(row)">编辑</el-button>
          <el-button text type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editing.id ? '编辑分类' : '新增分类'" width="400px">
      <el-form :model="editing" label-width="80px">
        <el-form-item label="名称" required><el-input v-model="editing.name" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="editing.sort" :min="0" style="width: 100%" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="editing.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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
import { getAllCategories, addCategory, updateCategory, deleteCategory } from '@/api/category'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const editing = reactive({ id: null, name: '', sort: 0, status: 1 })

async function fetchData() {
  loading.value = true
  try {
    const res = await getAllCategories()
    list.value = res.data || []
  } catch (e) { /* ignore */ }
  loading.value = false
}

function openDialog(row) {
  if (row) Object.assign(editing, row)
  else Object.assign(editing, { id: null, name: '', sort: 0, status: 1 })
  dialogVisible.value = true
}

async function handleSave() {
  if (!editing.name) { ElMessage.warning('请输入名称'); return }
  try {
    if (editing.id) await updateCategory(editing)
    else await addCategory(editing)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* ignore */ }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除分类"${row.name}"？`, '提示', { type: 'warning' })
    await deleteCategory(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) { /* ignore */ }
}

onMounted(fetchData)
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
</style>
