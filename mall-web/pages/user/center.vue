<template>
  <div class="user-center-page">
    <h1 class="page-title">个人中心</h1>

    <div class="user-content">
      <!-- 用户信息卡片 -->
      <div class="user-card">
        <div class="user-avatar">
          <el-avatar :size="80">
            <el-icon :size="40"><User /></el-icon>
          </el-avatar>
        </div>
        <div class="user-info">
          <h2>{{ userStore.userInfo?.nickname || '用户' }}</h2>
          <p class="user-phone">{{ maskPhone(userStore.userInfo?.phone) }}</p>
        </div>
        <div class="user-actions">
          <el-button @click="handleLogout">退出登录</el-button>
        </div>
      </div>

      <!-- 订单状态 -->
      <div class="order-status-card">
        <div class="card-header">
          <h3>我的订单</h3>
          <el-link type="primary" @click="navigateTo('/user/orders')">查看全部</el-link>
        </div>
        <div class="status-grid">
          <div class="status-item" @click="navigateTo('/user/orders?status=0')">
            <el-icon :size="28"><Wallet /></el-icon>
            <span>待付款</span>
          </div>
          <div class="status-item" @click="navigateTo('/user/orders?status=1')">
            <el-icon :size="28"><Box /></el-icon>
            <span>待发货</span>
          </div>
          <div class="status-item" @click="navigateTo('/user/orders?status=2')">
            <el-icon :size="28"><Van /></el-icon>
            <span>待收货</span>
          </div>
          <div class="status-item" @click="navigateTo('/user/orders?status=3')">
            <el-icon :size="28"><Check /></el-icon>
            <span>已完成</span>
          </div>
        </div>
      </div>

      <!-- 功能菜单 -->
      <div class="menu-card">
        <div class="menu-item" @click="navigateTo('/user/orders')">
          <el-icon :size="24"><List /></el-icon>
          <span>我的订单</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item" @click="navigateTo('/cart')">
          <el-icon :size="24"><ShoppingCart /></el-icon>
          <span>购物车</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item" @click="ElMessage.info('功能开发中...')">
          <el-icon :size="24"><Location /></el-icon>
          <span>收货地址</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item" @click="ElMessage.info('功能开发中...')">
          <el-icon :size="24"><Setting /></el-icon>
          <span>账户设置</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  User, Wallet, Box, Van, Check, List,
  ShoppingCart, Location, Setting, ArrowRight
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

definePageMeta({
  layout: 'default'
})

useHead({
  title: '个人中心'
})

const userStore = useUserStore()
const router = useRouter()

// 手机号脱敏
const maskPhone = (phone?: string) => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/')
    ElMessage.success('已退出登录')
  }).catch(() => {})
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    router.push('/login?redirect=/user/center')
  }
})
</script>

<style lang="scss" scoped>
.user-center-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 20px;
}

.user-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 用户信息卡片 */
.user-card {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 30px;
  background: linear-gradient(135deg, #409eff, #67c23a);
  border-radius: 12px;
  color: #fff;
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.3);
}

.user-info {
  flex: 1;

  h2 {
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 4px;
  }

  .user-phone {
    font-size: 14px;
    opacity: 0.9;
  }
}

/* 订单状态卡片 */
.order-status-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;

  h3 {
    font-size: 18px;
    font-weight: 600;
  }
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.status-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  color: #606266;

  &:hover {
    background: #f5f5f5;
    color: #409eff;
  }

  span {
    font-size: 14px;
  }
}

/* 功能菜单 */
.menu-card {
  background: #fff;
  border-radius: 12px;
  padding: 8px 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 0;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: color 0.3s;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    color: #409eff;
  }

  span {
    flex: 1;
    font-size: 16px;
  }

  .arrow {
    color: #c0c4cc;
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .status-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .user-card {
    flex-direction: column;
    text-align: center;
  }
}
</style>
