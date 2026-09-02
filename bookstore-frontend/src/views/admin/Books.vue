<template>
  <div>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索书名/作者/ISBN" style="width: 280px" clearable @keyup.enter="fetchData" />
      <el-button type="primary" @click="fetchData">搜索</el-button>
      <el-button type="success" :icon="Plus" @click="openDialog()">新增图书</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column type="index" label="#" width="60" />
      <el-table-column prop="title" label="书名" min-width="160" show-overflow-tooltip />
      <el-table-column prop="author" label="作者" width="120" show-overflow-tooltip />
      <el-table-column prop="publisher" label="出版社" width="140" show-overflow-tooltip />
      <el-table-column prop="price" label="售价" width="90">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="sales" label="销量" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button text type="primary" size="small" @click="openDialog(row)">编辑</el-button>
          <el-button text :type="row.status === 1 ? 'warning' : 'success'" size="small" @click="handleToggleStatus(row)">
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
          <el-button text type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:current-page="current" :page-size="10" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </div>

    <el-dialog v-model="dialogVisible" :title="editing.id ? '编辑图书' : '新增图书'" width="700px">
      <el-form :model="editing" label-width="90px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="书名" required><el-input v-model="editing.title" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作者" required><el-input v-model="editing.author" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出版社"><el-input v-model="editing.publisher" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="ISBN"><el-input v-model="editing.isbn" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分类">
              <el-select v-model="editing.categoryId" placeholder="选择分类" style="width: 100%">
                <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出版日期"><el-date-picker v-model="editing.publishDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="售价" required><el-input-number v-model="editing.price" :min="0" :precision="2" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="原价"><el-input-number v-model="editing.originalPrice" :min="0" :precision="2" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="库存" required><el-input-number v-model="editing.stock" :min="0" style="width: 100%" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="封面URL"><el-input v-model="editing.cover" placeholder="图片URL" /></el-form-item>
        <el-form-item label="简介"><el-input v-model="editing.description" type="textarea" :rows="3" /></el-form-item>
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
import { getBookPage, addBook, updateBook, deleteBook, updateBookStatus } from '@/api/book'
import { getAllCategories } from '@/api/category'

const list = ref([])
const total = ref(0)
const current = ref(1)
const keyword = ref('')
const loading = ref(false)
const categories = ref([])
const dialogVisible = ref(false)

const editing = reactive({ id: null, title: '', author: '', publisher: '', isbn: '', categoryId: null, cover: '', price: 0, originalPrice: null, stock: 0, publishDate: null, description: '' })

async function fetchData() {
  loading.value = true
  try {
    const res = await getBookPage({ current: current.value, size: 10, keyword: keyword.value })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) { /* ignore */ }
  loading.value = false
}

async function fetchCategories() {
  try {
    const res = await getAllCategories()
    categories.value = res.data || []
  } catch (e) { /* ignore */ }
}

function openDialog(row) {
  if (row) {
    Object.assign(editing, row)
  } else {
    Object.assign(editing, { id: null, title: '', author: '', publisher: '', isbn: '', categoryId: null, cover: '', price: 0, originalPrice: null, stock: 0, publishDate: null, description: '' })
  }
  dialogVisible.value = true
}

async function handleSave() {
  if (!editing.title || !editing.author) {
    ElMessage.warning('请填写必填项')
    return
  }
  try {
    if (editing.id) {
      await updateBook(editing)
    } else {
      await addBook(editing)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* ignore */ }
}

async function handleToggleStatus(row) {
  try {
    await updateBookStatus(row.id, row.status === 1 ? 0 : 1)
    ElMessage.success('操作成功')
    fetchData()
  } catch (e) { /* ignore */ }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除图书《${row.title}》？`, '提示', { type: 'warning' })
    await deleteBook(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) { /* ignore */ }
}

onMounted(() => { fetchData(); fetchCategories() })
</script>

<style scoped>
.toolbar { margin-bottom: 16px; display: flex; gap: 12px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
