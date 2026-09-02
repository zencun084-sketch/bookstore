<template>
  <div class="profile-page container">
    <h1 class="page-title">个人中心</h1>

    <div class="profile-grid">
      <!-- 左侧：个人信息 -->
      <div class="profile-card">
        <div class="avatar-area">
          <el-avatar :size="80" :src="userStore.userInfo?.avatar">{{ userStore.userInfo?.nickname?.charAt(0) }}</el-avatar>
          <h3>{{ userStore.userInfo?.nickname }}</h3>
          <p class="username">@{{ userStore.userInfo?.username }}</p>
          <el-tag v-if="userStore.isAdmin" type="danger" size="small">管理员</el-tag>
        </div>

        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本资料" name="profile">
            <el-form :model="profileForm" label-width="80px" size="default">
              <el-form-item label="昵称">
                <el-input v-model="profileForm.nickname" />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="profileForm.email" disabled />
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="profileForm.phone" />
              </el-form-item>
              <el-form-item label="性别">
                <el-radio-group v-model="profileForm.gender">
                  <el-radio :label="1">男</el-radio>
                  <el-radio :label="2">女</el-radio>
                  <el-radio :label="0">保密</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSaveProfile">保存修改</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="修改密码" name="password">
            <el-form :model="pwdForm" label-width="80px" size="default">
              <el-form-item label="原密码">
                <el-input v-model="pwdForm.oldPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="新密码">
                <el-input v-model="pwdForm.newPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="确认密码">
                <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 右侧：快捷入口 -->
      <div class="quick-cards">
        <div class="quick-card" @click="$router.push('/orders')">
          <el-icon :size="32" color="#4f46e5"><List /></el-icon>
          <span>我的订单</span>
        </div>
        <div class="quick-card" @click="$router.push('/favorites')">
          <el-icon :size="32" color="#f59e0b"><Star /></el-icon>
          <span>我的收藏</span>
        </div>
        <div class="quick-card" @click="$router.push('/addresses')">
          <el-icon :size="32" color="#10b981"><Location /></el-icon>
          <span>地址管理</span>
        </div>
        <div class="quick-card" @click="$router.push('/cart')">
          <el-icon :size="32" color="#ef4444"><ShoppingCart /></el-icon>
          <span>购物车</span>
        </div>
        <div v-if="userStore.isAdmin" class="quick-card" @click="$router.push('/admin')">
          <el-icon :size="32" color="#8b5cf6"><Setting /></el-icon>
          <span>后台管理</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { List, Star, Location, ShoppingCart, Setting } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { updateProfile, changePassword } from '@/api/auth'

const userStore = useUserStore()
const activeTab = ref('profile')

const profileForm = reactive({ nickname: '', email: '', phone: '', gender: 0 })
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

function loadProfile() {
  const info = userStore.userInfo
  if (info) {
    profileForm.nickname = info.nickname
    profileForm.email = info.email
    profileForm.phone = info.phone
    profileForm.gender = info.gender
  }
}

async function handleSaveProfile() {
  try {
    await updateProfile(profileForm)
    await userStore.fetchUserInfo()
    ElMessage.success('保存成功')
  } catch (e) { /* ignore */ }
}

async function handleChangePassword() {
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    ElMessage.error('两次密码不一致')
    return
  }
  try {
    await changePassword({ oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
    ElMessage.success('密码修改成功')
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
  } catch (e) { /* ignore */ }
}

onMounted(loadProfile)
</script>

<style scoped>
.profile-page { padding: 20px 0; }
.page-title { font-size: 24px; margin-bottom: 20px; }

.profile-grid { display: grid; grid-template-columns: 1fr 320px; gap: 20px; }

.profile-card { background: #fff; border-radius: var(--radius-md); padding: 30px; box-shadow: var(--shadow-sm); }
.avatar-area { text-align: center; margin-bottom: 24px; }
.avatar-area h3 { margin-top: 12px; font-size: 18px; }
.username { color: var(--text-secondary); font-size: 13px; margin: 4px 0; }

.quick-cards { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; align-content: start; }
.quick-card {
  background: #fff;
  border-radius: var(--radius-md);
  padding: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: var(--shadow-sm);
}
.quick-card:hover { transform: translateY(-4px); box-shadow: var(--shadow-md); }
.quick-card span { font-size: 14px; }

@media (max-width: 768px) {
  .profile-grid { grid-template-columns: 1fr; }
  .quick-cards { grid-template-columns: repeat(3, 1fr); }
}
</style>
