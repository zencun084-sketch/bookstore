<template>
  <div class="addresses-page container">
    <div class="page-header">
      <h1 class="page-title">地址管理</h1>
      <el-button type="primary" :icon="Plus" @click="openDialog()">新增地址</el-button>
    </div>

    <div v-loading="loading">
      <div class="address-list">
        <div v-for="addr in addresses" :key="addr.id" class="address-card">
          <div class="card-header">
            <span class="receiver">{{ addr.receiver }}</span>
            <span class="phone">{{ addr.phone }}</span>
            <el-tag v-if="addr.isDefault" size="small" type="danger">默认</el-tag>
          </div>
          <p class="address-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}</p>
          <div class="card-actions">
            <el-button text type="primary" size="small" @click="handleSetDefault(addr)" v-if="addr.isDefault !== 1">设为默认</el-button>
            <el-button text type="primary" size="small" @click="openDialog(addr)">编辑</el-button>
            <el-button text type="danger" size="small" @click="handleDelete(addr)">删除</el-button>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && addresses.length === 0" description="暂无地址" />
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editing.id ? '编辑地址' : '新增地址'" width="500px">
      <el-form :model="editing" label-width="80px">
        <el-form-item label="收货人" required>
          <el-input v-model="editing.receiver" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" required>
          <el-input v-model="editing.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="省份">
          <el-input v-model="editing.province" placeholder="省份" />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="editing.city" placeholder="城市" />
        </el-form-item>
        <el-form-item label="区县">
          <el-input v-model="editing.district" placeholder="区县" />
        </el-form-item>
        <el-form-item label="详细地址" required>
          <el-input v-model="editing.detail" type="textarea" :rows="2" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="默认地址">
          <el-switch v-model="editing.isDefault" :active-value="1" :inactive-value="0" />
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
import { getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/address'

const addresses = ref([])
const loading = ref(false)
const dialogVisible = ref(false)

const editing = reactive({ id: null, receiver: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: 0 })

async function fetchAddresses() {
  loading.value = true
  try {
    const res = await getAddressList()
    addresses.value = res.data || []
  } catch (e) { /* ignore */ }
  loading.value = false
}

function openDialog(addr) {
  if (addr) {
    Object.assign(editing, addr)
  } else {
    Object.assign(editing, { id: null, receiver: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: 0 })
  }
  dialogVisible.value = true
}

async function handleSave() {
  if (!editing.receiver || !editing.phone || !editing.detail) {
    ElMessage.warning('请填写必填项')
    return
  }
  try {
    if (editing.id) {
      await updateAddress(editing)
    } else {
      await addAddress(editing)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchAddresses()
  } catch (e) { /* ignore */ }
}

async function handleDelete(addr) {
  try {
    await ElMessageBox.confirm('确定删除该地址？', '提示', { type: 'warning' })
    await deleteAddress(addr.id)
    ElMessage.success('删除成功')
    fetchAddresses()
  } catch (e) { /* ignore */ }
}

async function handleSetDefault(addr) {
  try {
    await setDefaultAddress(addr.id)
    ElMessage.success('已设为默认')
    fetchAddresses()
  } catch (e) { /* ignore */ }
}

onMounted(fetchAddresses)
</script>

<style scoped>
.addresses-page { padding: 20px 0; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-title { font-size: 24px; }

.address-list { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.address-card { background: #fff; border-radius: var(--radius-md); padding: 20px; box-shadow: var(--shadow-sm); }
.card-header { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.receiver { font-weight: 600; font-size: 16px; }
.phone { color: var(--text-secondary); font-size: 14px; }
.address-detail { font-size: 14px; color: var(--text-regular); line-height: 1.6; margin-bottom: 12px; }
.card-actions { display: flex; gap: 4px; }

@media (max-width: 768px) { .address-list { grid-template-columns: 1fr; } }
</style>
