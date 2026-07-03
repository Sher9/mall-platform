<template>
  <div class="home">
    <div class="stats">
      <div class="stat-card card">
        <div class="stat-icon products">📦</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.productCount }}</div>
          <div class="stat-label">商品总数</div>
        </div>
      </div>
      <div class="stat-card card">
        <div class="stat-icon customers">👥</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.customerCount }}</div>
          <div class="stat-label">客户总数</div>
        </div>
      </div>
      <div class="stat-card card">
        <div class="stat-icon orders">📋</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.orderCount }}</div>
          <div class="stat-label">订单总数</div>
        </div>
      </div>
      <div class="stat-card card">
        <div class="stat-icon revenue">💰</div>
        <div class="stat-info">
          <div class="stat-value">¥{{ stats.totalRevenue }}</div>
          <div class="stat-label">总销售额</div>
        </div>
      </div>
    </div>
    <div class="welcome card">
      <h2>欢迎使用商城桌面端管理系统</h2>
      <p>当前用户：{{ userStore.userInfo?.username || '管理员' }}</p>
      <p>通过左侧导航栏管理商品、客户和订单。</p>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getAllProducts } from '@/api/product'
import { getAllCustomers } from '@/api/customer'

const userStore = useUserStore()

const stats = reactive({
  productCount: 0,
  customerCount: 0,
  orderCount: 0,
  totalRevenue: '0.00'
})

onMounted(async () => {
  try {
    const [productRes, customerRes] = await Promise.all([
      getAllProducts(),
      getAllCustomers()
    ])
    stats.productCount = productRes.data?.length || 0
    stats.customerCount = customerRes.data?.length || 0
  } catch (e) {
    // 接口可能未就绪
  }
})
</script>

<style lang="scss" scoped>
.home {
  .stats {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    margin-bottom: 20px;

    .stat-card {
      display: flex;
      align-items: center;
      gap: 16px;

      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: $radius-md;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;

        &.products { background: #e8f3ff; }
        &.customers { background: #e8ffea; }
        &.orders { background: #fff7e8; }
        &.revenue { background: #ffe8e8; }
      }

      .stat-info {
        .stat-value {
          font-size: 24px;
          font-weight: 700;
          color: $text-color;
        }
        .stat-label {
          font-size: 13px;
          color: $text-color-secondary;
        }
      }
    }
  }

  .welcome {
    h2 {
      margin-bottom: 12px;
      color: $primary-color;
    }
    p {
      margin-bottom: 8px;
      color: $text-color-secondary;
    }
  }
}
</style>
