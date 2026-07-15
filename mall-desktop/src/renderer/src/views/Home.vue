<template>
  <div class="home">
    <div class="page-head">
      <h1 class="page-title">数据概览</h1>
      <p class="page-desc">欢迎使用商城桌面端管理系统，以下是平台核心运营数据。</p>
    </div>

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
      <div>
        <h2>欢迎回来，{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '管理员' }}</h2>
        <p>通过左侧导航栏管理商品、客户与订单，或使用顶栏登录以同步账户数据。</p>
      </div>
      <div class="quick-actions">
        <router-link to="/products" class="qa-item">商品管理 →</router-link>
        <router-link to="/orders" class="qa-item">订单管理 →</router-link>
        <router-link to="/customers" class="qa-item">客户管理 →</router-link>
      </div>
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
  .page-head {
    margin-bottom: 20px;

    .page-title {
      font-size: 20px;
      font-weight: 600;
      color: $text-color;
    }

    .page-desc {
      margin-top: 6px;
      font-size: 13px;
      color: $text-color-secondary;
    }
  }

  .stats {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    margin-bottom: 20px;

    .stat-card {
      display: flex;
      align-items: center;
      gap: 16px;
      transition: box-shadow 0.2s, transform 0.2s;

      &:hover {
        box-shadow: $shadow-md;
        transform: translateY(-2px);
      }

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
          line-height: 1.2;
        }

        .stat-label {
          font-size: 13px;
          color: $text-color-secondary;
        }
      }
    }
  }

  .welcome {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 24px;

    h2 {
      margin-bottom: 8px;
      font-size: 18px;
      color: $text-color;
    }

    p {
      color: $text-color-secondary;
      font-size: 14px;
    }

    .quick-actions {
      display: flex;
      gap: 12px;
      flex-shrink: 0;

      .qa-item {
        padding: 8px 14px;
        background: $bg-color;
        border: 1px solid $border-color;
        border-radius: $radius-sm;
        font-size: 13px;
        color: $text-color-secondary;
        transition: all 0.2s;

        &:hover {
          border-color: $primary-color;
          color: $primary-color;
        }
      }
    }
  }
}
</style>
