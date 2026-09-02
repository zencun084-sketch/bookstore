<template>
  <div>
    <div class="toolbar">
      <el-button type="success" :icon="Plus" @click="openDialog()">新增Banner</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column type="index" label="#" width="60" />
      <el-table-column prop="imageUrl" label="图片" width="120">
        <template #default="{ row }">
          <img v-if="row.imageUrl" :src="row.imageUrl" style="width: 80px; height: 45px; object-fit: cover; border-radius: 4px" />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="160" />
      <el-table-column prop="linkUrl" label="跳转链接" min-width="160" show-overflow-tooltip />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button text type="primary" size="small" @click="openDialog(row)">编辑</el-button>
          <el-button text type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editing.id ? '编辑Banner' : '新增Banner'" width="500px">
      <el-form :model="editing" label-width="90px">
        <el-form-item label="标题"><el-input v-model="editing.title" /></el-form-item>
        <el-form-item label="图片URL" required><el-input v-model="editing.imageUrl" /></el-form-item>
        <el-form-item label="跳转链接"><el-input v-model="editing.linkUrl" /></el-form-item>
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
import { getAllBanners, addBanner, updateBanner, deleteBanner } from '@/api/content'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const editing = reactive({ id: null, title: '', imageUrl: '', linkUrl: '', sort: 0, status: 1 })

async function fetchData() {
  loading.value = true
  try {
    const res = await getAllBanners()
    list.value = res.data || []
  } catch (e) { /* ignore */ }
  loading.value = false
}

function openDialog(row) {
  if (row) Object.assign(editing, row)
  else Object.assign(editing, { id: null, title: '', imageUrl: '', linkUrl: '', sort: 0, status: 1 })
  dialogVisible.value = true
}

async function handleSave() {
  if (!editing.imageUrl) { ElMessage.warning('请输入图片URL'); return }
  try {
    if (editing.id) await updateBanner(editing)
    else await addBanner(editing)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* ignore */ }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定删除该Banner？', '提示', { type: 'warning' })
    await deleteBanner(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) { /* ignore */ }
}

onMounted(fetchData)
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
</style>
