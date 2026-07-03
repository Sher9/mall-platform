<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="logo">商城管理</div>
      <nav class="menu">
        <router-link to="/home" class="menu-item" active-class="active">首页</router-link>
        <router-link to="/products" class="menu-item" active-class="active">商品管理</router-link>
        <router-link to="/customers" class="menu-item" active-class="active">客户管理</router-link>
        <router-link to="/orders" class="menu-item" active-class="active">订单管理</router-link>
        <router-link to="/cart" class="menu-item" active-class="active">购物车</router-link>
      </nav>
    </aside>
    <div class="main">
      <header class="header">
        <span class="title">{{ $route.meta.title }}</span>
        <div class="user-area">
          <span class="username">{{ userStore.userInfo?.username || '管理员' }}</span>
          <button class="btn-default" @click="handleLogout">退出</button>
        </div>
      </header>
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

async function handleLogout() {
  await userStore.logout()
  router.push('/login')
}
</script>

<style lang="scss" scoped>
.layout {
  display: flex;
  height: 100%;

  .sidebar {
    width: $sidebar-width;
    background: #1d2129;
    display: flex;
    flex-direction: column;

    .logo {
      height: $header-height;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 18px;
      font-weight: 600;
    }

    .menu {
      flex: 1;
      padding: 8px 0;

      .menu-item {
        display: block;
        padding: 12px 24px;
        color: #c9cdd4;
        font-size: 14px;
        transition: all 0.2s;

        &:hover {
          color: #fff;
          background: rgba(255, 255, 255, 0.08);
        }

        &.active {
          color: #fff;
          background: $primary-color;
        }
      }
    }
  }

  .main {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .header {
      height: $header-height;
      background: $bg-color-light;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 24px;
      box-shadow: $shadow-sm;
      z-index: 10;

      .title {
        font-size: 16px;
        font-weight: 600;
      }

      .user-area {
        display: flex;
        align-items: center;
        gap: 12px;

        .username {
          color: $text-color-secondary;
        }
      }
    }

    .content {
      flex: 1;
      overflow-y: auto;
      padding: 20px;
    }
  }
}
</style>
