<!-- 个人中心 -->
<template>
  <view class="page-user">
    <!-- 头部区域 -->
    <view class="user-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="header-bg"></view>
      <view class="user-info" v-if="userStore.isLoggedIn" @click="goProfile">
        <view class="avatar-wrap">
          <image
            :src="userStore.userInfo.avatar || defaultAvatar"
            class="avatar"
            mode="aspectFill"
          />
        </view>
        <view class="info-right">
          <text class="nickname">{{ userStore.userInfo.nickname || '用户' }}</text>
          <text class="user-id">ID: {{ userStore.userInfo.id || '--' }}</text>
        </view>
        <text class="arrow">›</text>
      </view>
      <view class="user-info" v-else @click="goLogin">
        <view class="avatar-wrap">
          <image :src="defaultAvatar" class="avatar" mode="aspectFill" />
        </view>
        <view class="info-right">
          <text class="nickname">点击登录</text>
          <text class="user-id">登录享受更多优惠</text>
        </view>
        <text class="arrow">›</text>
      </view>
    </view>

    <!-- 订单卡片 -->
    <view class="order-card">
      <view class="order-header">
        <text class="order-title">我的订单</text>
        <text class="order-all" @click="goOrderList('')">全部订单 ›</text>
      </view>
      <view class="order-grid">
        <view class="order-item" @click="goOrderList('pending')">
          <view class="order-icon-wrap">
            <text class="order-icon">📋</text>
            <view v-if="orderCounts.pending" class="badge">{{ orderCounts.pending }}</view>
          </view>
          <text class="order-label">待付款</text>
        </view>
        <view class="order-item" @click="goOrderList('shipped')">
          <view class="order-icon-wrap">
            <text class="order-icon">📦</text>
            <view v-if="orderCounts.shipped" class="badge">{{ orderCounts.shipped }}</view>
          </view>
          <text class="order-label">待发货</text>
        </view>
        <view class="order-item" @click="goOrderList('delivered')">
          <view class="order-icon-wrap">
            <text class="order-icon">🚚</text>
            <view v-if="orderCounts.delivered" class="badge">{{ orderCounts.delivered }}</view>
          </view>
          <text class="order-label">待收货</text>
        </view>
        <view class="order-item" @click="goOrderList('completed')">
          <view class="order-icon-wrap">
            <text class="order-icon">✅</text>
          </view>
          <text class="order-label">已完成</text>
        </view>
        <view class="order-item" @click="goOrderList('refund')">
          <view class="order-icon-wrap">
            <text class="order-icon">🔄</text>
          </view>
          <text class="order-label">退款/售后</text>
        </view>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-card">
      <view
        v-for="(item, i) in menuList"
        :key="i"
        class="menu-item"
        @click="handleMenu(item)"
      >
        <view class="menu-left">
          <text class="menu-icon">{{ item.icon }}</text>
          <text class="menu-name">{{ item.name }}</text>
        </view>
        <view class="menu-right">
          <text v-if="item.extra" class="menu-extra">{{ item.extra }}</text>
          <text class="menu-arrow">›</text>
        </view>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-wrap" v-if="userStore.isLoggedIn">
      <view class="logout-btn" @click="handleLogout">退出登录</view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { showToast, showDialog } from '@/utils'

const userStore = useUserStore()
const statusBarHeight = ref(20)
const defaultAvatar = 'https://via.placeholder.com/120x120/1989FA/fff?text=User'

const orderCounts = reactive({ pending: 0, shipped: 0, delivered: 0 })

const menuList = ref([
  { icon: '❤️', name: '我的收藏', action: 'favorite' },
  { icon: '🏠', name: '收货地址', action: 'address' },
  { icon: '🎫', name: '优惠券', action: 'coupon', extra: '3张可用' },
  { icon: '👣', name: '浏览记录', action: 'history' },
  { icon: '⚙️', name: '设置', action: 'settings' },
  { icon: '📞', name: '联系客服', action: 'service' }
])

onMounted(() => {
  const sysInfo = uni.getSystemInfoSync()
  statusBarHeight.value = sysInfo.statusBarHeight || 20
})

function goLogin() {
  uni.navigateTo({ url: '/pages/login/login' })
}

function goProfile() {
  showToast('个人资料页开发中')
}

function goOrderList(tab) {
  if (!userStore.isLoggedIn) return goLogin()
  showToast(`查看${tab || '全部'}订单`)
}

async function handleLogout() {
  const res = await showDialog('提示', '确认退出登录？')
  if (res) {
    userStore.logoutAction()
    showToast('已退出登录', 'success')
  }
}

function handleMenu(item) {
  if (!userStore.isLoggedIn && item.action !== 'service') {
    return goLogin()
  }
  showToast(`${item.name}开发中`)
}
</script>

<style lang="scss" scoped>
.page-user {
  min-height: 100vh;
  background: #f5f6fa;
}

// 头部
.user-header {
  position: relative;
  padding-bottom: 40rpx;
  .header-bg {
    position: absolute;
    top: 0; left: 0; right: 0;
    height: 320rpx;
    background: linear-gradient(135deg, #1989FA 0%, #4facfe 50%, #7dcdff 100%);
    border-radius: 0 0 40rpx 40rpx;
  }
  .user-info {
    position: relative;
    z-index: 1;
    display: flex;
    align-items: center;
    padding: 40rpx 32rpx 0;
    .avatar-wrap {
      width: 120rpx;
      height: 120rpx;
      border-radius: 50%;
      overflow: hidden;
      border: 4rpx solid rgba(255,255,255,0.8);
      box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.15);
      flex-shrink: 0;
      .avatar { width: 100%; height: 100%; }
    }
    .info-right {
      margin-left: 24rpx;
      flex: 1;
      display: flex;
      flex-direction: column;
      .nickname { font-size: 36rpx; font-weight: 700; color: #fff; }
      .user-id { font-size: 22rpx; color: rgba(255,255,255,0.8); margin-top: 6rpx; }
    }
    .arrow { font-size: 36rpx; color: rgba(255,255,255,0.8); }
  }
}

// 订单卡片
.order-card {
  margin: -20rpx 24rpx 20rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx;
  box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.06);
  position: relative;
  z-index: 2;
  .order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24rpx;
    .order-title { font-size: 30rpx; font-weight: 700; color: #323233; }
    .order-all { font-size: 24rpx; color: #969799; }
  }
  .order-grid {
    display: flex;
    justify-content: space-around;
    .order-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      .order-icon-wrap {
        position: relative;
        .order-icon { font-size: 44rpx; }
        .badge {
          position: absolute;
          top: -8rpx; right: -16rpx;
          background: #EE0A24;
          color: #fff;
          font-size: 18rpx;
          min-width: 28rpx;
          height: 28rpx;
          border-radius: 14rpx;
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 0 6rpx;
        }
      }
      .order-label { font-size: 22rpx; color: #646566; margin-top: 8rpx; }
    }
  }
}

// 菜单卡片
.menu-card {
  margin: 20rpx 24rpx;
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.04);
  .menu-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 28rpx 28rpx;
    border-bottom: 1rpx solid #f5f5f5;
    &:last-child { border-bottom: none; }
    &:active { background: #f9f9f9; }
    .menu-left {
      display: flex;
      align-items: center;
      .menu-icon { font-size: 36rpx; margin-right: 20rpx; }
      .menu-name { font-size: 28rpx; color: #323233; }
    }
    .menu-right {
      display: flex;
      align-items: center;
      .menu-extra { font-size: 22rpx; color: #969799; margin-right: 8rpx; }
      .menu-arrow { font-size: 28rpx; color: #c8c9cc; }
    }
  }
}

// 退出登录
.logout-wrap {
  padding: 40rpx 24rpx 60rpx;
  .logout-btn {
    text-align: center;
    padding: 22rpx 0;
    border: 2rpx solid #EE0A24;
    border-radius: 40rpx;
    color: #EE0A24;
    font-size: 28rpx;
    font-weight: 600;
    &:active { background: #fff5f5; }
  }
}
</style>
