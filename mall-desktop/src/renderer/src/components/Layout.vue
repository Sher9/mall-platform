<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="logo">
        <span class="logo-mark">商城</span>
        <span class="logo-text">管理控制台</span>
      </div>
      <nav class="menu">
        <router-link to="/home" class="menu-item" active-class="active">
          <span class="dot"></span>首页
        </router-link>
        <router-link to="/products" class="menu-item" active-class="active">
          <span class="dot"></span>商品管理
        </router-link>
        <router-link to="/customers" class="menu-item" active-class="active">
          <span class="dot"></span>客户管理
        </router-link>
        <router-link to="/orders" class="menu-item" active-class="active">
          <span class="dot"></span>订单管理
        </router-link>
        <router-link to="/cart" class="menu-item" active-class="active">
          <span class="dot"></span>购物车
        </router-link>
      </nav>
    </aside>

    <div class="main">
      <header class="header">
        <span class="title">{{ $route.meta.title }}</span>
        <div class="user-area">
          <template v-if="userStore.isLoggedIn">
            <span class="avatar">{{ avatarText }}</span>
            <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '管理员' }}</span>
            <button class="btn-default logout-btn" @click="handleLogout">退出</button>
          </template>
          <button v-else class="btn-primary login-btn" @click="goLogin">登录</button>
        </div>
      </header>

      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { computed } from 'vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const avatarText = computed(() => {
  const name = userStore.userInfo?.nickname || userStore.userInfo?.username || '管'
  return name.charAt(0)
})

function goLogin() {
  router.push({ path: '/login', query: { redirect: route.fullPath } })
}

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
    background: $bg-color-light;
    border-right: 1px solid $border-color;
    display: flex;
    flex-direction: column;

    .logo {
      height: $header-height;
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 0 20px;
      border-bottom: 1px solid $border-color;

      .logo-mark {
        padding: 4px 8px;
        background: $primary-color;
        color: #fff;
        font-size: 13px;
        font-weight: 600;
        border-radius: $radius-sm;
        letter-spacing: 1px;
      }

      .logo-text {
        font-size: 15px;
        font-weight: 600;
        color: $text-color;
      }
    }

    .menu {
      flex: 1;
      padding: 12px 12px;

      .menu-item {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 4px;
        padding: 10px 14px;
        color: $text-color-secondary;
        font-size: 14px;
        border-radius: $radius-sm;
        transition: all 0.2s;

        .dot {
          width: 6px;
          height: 6px;
          border-radius: 50%;
          background: transparent;
          transition: all 0.2s;
        }

        &:hover {
          background: $bg-color;
          color: $text-color;
        }

        &.active {
          background: #e8f3ff;
          color: $primary-color;
          font-weight: 500;

          .dot {
            background: $primary-color;
          }
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
      border-bottom: 1px solid $border-color;
      z-index: 10;

      .title {
        font-size: 16px;
        font-weight: 600;
        color: $text-color;
      }

      .user-area {
        display: flex;
        align-items: center;
        gap: 12px;

        .avatar {
          width: 30px;
          height: 30px;
          display: flex;
          align-items: center;
          justify-content: center;
          background: $primary-color;
          color: #fff;
          font-size: 13px;
          border-radius: 50%;
        }

        .username {
          font-size: 14px;
          color: $text-color;
        }

        .login-btn {
          height: 32px;
          padding: 0 18px;
        }

        .logout-btn {
          height: 32px;
          padding: 0 14px;
        }
      }
    }

    .content {
      flex: 1;
      overflow-y: auto;
      padding: 20px 24px;
      background: $bg-color;
    }
  }
}
</style>
