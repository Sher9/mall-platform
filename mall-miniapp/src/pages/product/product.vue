<!-- 商品详情 -->
<template>
  <view class="page-product">
    <!-- 顶部导航 -->
    <view class="nav-bar" :style="{ paddingTop: statusBarHeight + 'px', background: navBg }">
      <view class="nav-content">
        <view class="nav-btn" @click="goBack">
          <text class="nav-icon">←</text>
        </view>
        <text class="nav-title" v-if="scrolled">商品详情</text>
        <view class="nav-right">
          <text class="nav-icon">☆</text>
          <text class="nav-icon">↗</text>
        </view>
      </view>
    </view>

    <!-- 商品图片轮播 -->
    <swiper
      class="product-swiper"
      :autoplay="true"
      :interval="3000"
      :circular="true"
      indicator-dots
      indicator-color="rgba(255,255,255,0.4)"
      indicator-active-color="#ffffff"
    >
      <swiper-item v-for="(img, i) in product.images" :key="i">
        <image :src="img" class="swiper-img" mode="aspectFill" />
      </swiper-item>
    </swiper>

    <!-- 价格区域 -->
    <view class="price-section">
      <view class="price-main">
        <text class="price-symbol">¥</text>
        <text class="price-int">{{ priceInt }}</text>
        <text class="price-dec">.{{ priceDec }}</text>
        <view v-if="product.originalPrice" class="price-tag">限时折扣</view>
      </view>
      <view v-if="product.originalPrice" class="price-original">
        <text class="original-label">原价</text>
        <text class="original-num">¥{{ product.originalPrice }}</text>
      </view>
      <view class="price-extra">
        <text class="extra-item">🚚 包邮</text>
        <text class="extra-item">✅ 正品保障</text>
        <text class="extra-item">🔄 7天退换</text>
      </view>
    </view>

    <!-- 商品标题 -->
    <view class="title-section">
      <view class="title-tags">
        <text v-if="product.tag" class="title-tag">{{ product.tag }}</text>
      </view>
      <text class="title-text">{{ product.name }}</text>
      <text class="title-desc">{{ product.description }}</text>
    </view>

    <!-- 规格选择 -->
    <view class="sku-section" @click="showSku = true">
      <text class="sku-label">规格</text>
      <view class="sku-value">
        <text>{{ selectedSku || '请选择规格' }}</text>
      </view>
      <text class="sku-arrow">›</text>
    </view>

    <!-- 商品详情 -->
    <view class="detail-section">
      <view class="detail-tab">
        <text class="tab-item active">商品详情</text>
        <text class="tab-item">规格参数</text>
      </view>
      <view class="detail-content">
        <image
          v-for="(img, i) in product.detailImages"
          :key="i"
          :src="img"
          class="detail-img"
          mode="widthFix"
        />
        <view v-if="!product.detailImages?.length" class="detail-empty">
          <text class="empty-text">商品详情加载中...</text>
        </view>
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <view class="bar-actions">
        <view class="bar-action-item" @click="goService">
          <text class="action-icon">💬</text>
          <text class="action-text">客服</text>
        </view>
        <view class="bar-action-item" @click="goCart">
          <view class="cart-badge-wrap">
            <text class="action-icon">🛒</text>
            <view v-if="cartStore.cartCount" class="cart-badge">{{ cartStore.cartCount }}</view>
          </view>
          <text class="action-text">购物车</text>
        </view>
        <view class="bar-action-item" @click="handleFavorite">
          <text class="action-icon">{{ isFavorite ? '❤️' : '🤍' }}</text>
          <text class="action-text">收藏</text>
        </view>
      </view>
      <view class="bar-buttons">
        <view class="btn-cart" @click="handleAddCart">加入购物车</view>
        <view class="btn-buy" @click="handleBuyNow">立即购买</view>
      </view>
    </view>

    <!-- SKU 弹窗（自定义实现） -->
    <view v-if="showSku" class="sku-mask" @click="showSku = false">
      <view class="sku-popup" @click.stop>
        <view class="sku-header">
          <image :src="product.images?.[0]" class="sku-img" mode="aspectFill" />
          <view class="sku-info">
            <view class="sku-price">
              <text class="sku-symbol">¥</text>
              <text class="sku-num">{{ product.price }}</text>
            </view>
            <text class="sku-stock">库存充足</text>
            <text class="sku-selected">已选：{{ selectedSku || '未选择' }}</text>
          </view>
          <view class="sku-close" @click="showSku = false">✕</view>
        </view>
        <scroll-view class="sku-body" scroll-y>
          <text class="sku-label">规格</text>
          <view class="sku-options">
            <view
              v-for="(sku, i) in product.skus"
              :key="i"
              class="sku-option"
              :class="{ active: selectedSku === sku }"
              @click="selectedSku = sku"
            >{{ sku }}</view>
          </view>
        </scroll-view>
        <view class="sku-footer">
          <view class="sku-btn" @click="confirmSku">确认</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useCartStore } from '@/store/cart'
import { showToast } from '@/utils'

const cartStore = useCartStore()
const statusBarHeight = ref(20)
const navBg = ref('transparent')
const scrolled = ref(false)
const showSku = ref(false)
const selectedSku = ref('')
const isFavorite = ref(false)

const product = ref({
  id: '',
  name: 'Apple iPhone 15 Pro Max 256GB 原色钛金属',
  price: '8999.00',
  originalPrice: '9999.00',
  description: 'A17 Pro芯片｜钛金属设计｜4800万主摄',
  tag: '热销',
  images: [
    'https://via.placeholder.com/750x750/1a1a1a/fff?text=iPhone+15+Pro',
    'https://via.placeholder.com/750x750/f5f5f7/333?text=Side+View',
    'https://via.placeholder.com/750x750/e8f4fd/1989FA?text=Blue+Titanium'
  ],
  detailImages: [
    'https://via.placeholder.com/750x400/1a1a1a/fff?text=Detail+1',
    'https://via.placeholder.com/750x400/f5f5f7/333?text=Detail+2'
  ],
  skus: ['256GB 原色钛金属', '256GB 蓝色钛金属', '512GB 原色钛金属', '512GB 蓝色钛金属']
})

const priceInt = computed(() => {
  const p = product.value.price?.toString() || '0.00'
  return p.split('.')[0]
})
const priceDec = computed(() => {
  const p = product.value.price?.toString() || '0.00'
  return (p.split('.')[1] || '00').padEnd(2, '0')
})

onMounted(() => {
  const info = uni.getSystemInfoSync()
  statusBarHeight.value = info.statusBarHeight || 20
})

function goBack() { uni.navigateBack() }
function confirmSku() {
  if (!selectedSku.value) return showToast('请选择规格')
  showSku.value = false
  showToast('已选择 ' + selectedSku.value)
}
function handleAddCart() {
  if (product.value.skus.length && !selectedSku.value) {
    showSku.value = true
    return
  }
  cartStore.addToCart({
    id: product.value.id || '1',
    name: product.value.name,
    price: product.value.price,
    image: product.value.images?.[0],
    sku: selectedSku.value
  })
  showToast('已加入购物车', 'success')
}
function handleBuyNow() {
  showToast('立即购买开发中')
}
function handleFavorite() {
  isFavorite.value = !isFavorite.value
  showToast(isFavorite.value ? '已收藏' : '已取消收藏', 'success')
}
function goService() { showToast('客服功能开发中') }
function goCart() { showToast('购物车开发中') }
</script>

<style lang="scss" scoped>
.page-product {
  padding-bottom: 120rpx;
  background: #f5f6fa;
}

// 导航栏
.nav-bar {
  position: fixed;
  top: 0; left: 0; right: 0;
  z-index: 100;
  transition: all 0.3s;
  .nav-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 88rpx;
    padding: 0 20rpx;
  }
  .nav-btn {
    width: 60rpx; height: 60rpx;
    display: flex; align-items: center; justify-content: center;
    background: rgba(0,0,0,0.3); border-radius: 50%;
  }
  .nav-icon { font-size: 32rpx; color: #fff; }
  .nav-title { font-size: 30rpx; font-weight: 600; color: #333; }
  .nav-right { display: flex; gap: 20rpx; }
}

// 轮播图
.product-swiper {
  width: 100%; height: 750rpx;
  .swiper-img { width: 100%; height: 100%; }
}

// 价格区域
.price-section {
  background: linear-gradient(135deg, #EE0A24, #FF6B6B);
  padding: 24rpx 28rpx;
  .price-main {
    display: flex; align-items: baseline;
    .price-symbol { font-size: 28rpx; color: #fff; font-weight: 600; }
    .price-int { font-size: 56rpx; color: #fff; font-weight: 800; }
    .price-dec { font-size: 28rpx; color: #fff; font-weight: 600; }
    .price-tag {
      margin-left: 16rpx; background: #fff; color: #EE0A24;
      font-size: 20rpx; padding: 4rpx 12rpx; border-radius: 6rpx; font-weight: 600;
    }
  }
  .price-original {
    margin-top: 8rpx;
    .original-label { font-size: 22rpx; color: rgba(255,255,255,0.7); margin-right: 8rpx; }
    .original-num { font-size: 22rpx; color: rgba(255,255,255,0.7); text-decoration: line-through; }
  }
  .price-extra { margin-top: 12rpx; display: flex; gap: 20rpx;
    .extra-item { font-size: 20rpx; color: rgba(255,255,255,0.85); }
  }
}

// 标题区域
.title-section {
  background: #fff; padding: 24rpx 28rpx;
  .title-tags { margin-bottom: 12rpx;
    .title-tag {
      background: linear-gradient(135deg, #EE0A24, #FF6B6B); color: #fff;
      font-size: 20rpx; padding: 4rpx 12rpx; border-radius: 6rpx;
    }
  }
  .title-text { font-size: 32rpx; font-weight: 700; color: #1a1a1a; line-height: 1.5; }
  .title-desc { display: block; margin-top: 8rpx; font-size: 24rpx; color: #969799; }
}

// 规格选择
.sku-section {
  margin-top: 16rpx; background: #fff; padding: 28rpx;
  display: flex; align-items: center;
  .sku-label { font-size: 26rpx; color: #969799; margin-right: 20rpx; }
  .sku-value { flex: 1; font-size: 26rpx; color: #323233; }
  .sku-arrow { font-size: 28rpx; color: #c8c9cc; }
}

// 商品详情
.detail-section {
  margin-top: 16rpx; background: #fff;
  .detail-tab {
    display: flex; border-bottom: 1rpx solid #f5f5f5;
    .tab-item {
      flex: 1; text-align: center; padding: 24rpx 0; font-size: 28rpx; color: #969799;
      position: relative;
      &.active { color: #1989FA; font-weight: 600; }
    }
  }
  .detail-content {
    .detail-img { width: 100%; display: block; }
    .detail-empty { padding: 60rpx 0; text-align: center;
      .empty-text { font-size: 24rpx; color: #c8c9cc; }
    }
  }
}

// 底部操作栏
.bottom-bar {
  position: fixed; bottom: 0; left: 0; right: 0; background: #fff;
  display: flex; align-items: center; padding: 12rpx 20rpx;
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
  box-shadow: 0 -2rpx 12rpx rgba(0,0,0,0.06); z-index: 100;
  .bar-actions { display: flex;
    .bar-action-item {
      display: flex; flex-direction: column; align-items: center; margin-right: 24rpx;
      position: relative;
      .action-icon { font-size: 36rpx; }
      .action-text { font-size: 18rpx; color: #646566; margin-top: 2rpx; }
      .cart-badge-wrap { position: relative; }
      .cart-badge {
        position: absolute; top: -8rpx; right: -12rpx; background: #EE0A24; color: #fff;
        font-size: 18rpx; min-width: 28rpx; height: 28rpx; border-radius: 14rpx;
        display: flex; align-items: center; justify-content: center; padding: 0 6rpx;
      }
    }
  }
  .bar-buttons {
    flex: 1; display: flex; border-radius: 40rpx; overflow: hidden;
    .btn-cart {
      flex: 1; text-align: center; padding: 20rpx 0;
      background: linear-gradient(135deg, #FF976A, #FFAE5C); color: #fff;
      font-size: 28rpx; font-weight: 600;
    }
    .btn-buy {
      flex: 1; text-align: center; padding: 20rpx 0;
      background: linear-gradient(135deg, #EE0A24, #FF6B6B); color: #fff;
      font-size: 28rpx; font-weight: 600;
    }
  }
}

// SKU 弹窗
.sku-mask {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5); z-index: 999;
  display: flex; align-items: flex-end; justify-content: center;
  .sku-popup {
    width: 100%; max-height: 80vh; background: #fff; border-radius: 24rpx 24rpx 0 0;
    display: flex; flex-direction: column;
    .sku-header {
      display: flex; padding: 28rpx;
      .sku-img { width: 160rpx; height: 160rpx; border-radius: 12rpx; flex-shrink: 0; }
      .sku-info { margin-left: 20rpx; flex: 1;
        .sku-symbol { font-size: 24rpx; color: #EE0A24; font-weight: 600; }
        .sku-num { font-size: 40rpx; color: #EE0A24; font-weight: 800; }
        .sku-stock { display: block; font-size: 22rpx; color: #07C160; margin-top: 8rpx; }
        .sku-selected { display: block; font-size: 22rpx; color: #646566; margin-top: 4rpx; }
      }
      .sku-close { font-size: 32rpx; color: #c8c9cc; padding: 8rpx; }
    }
    .sku-body {
      max-height: 50vh; padding: 0 28rpx;
      .sku-label { font-size: 26rpx; color: #323233; font-weight: 600; }
      .sku-options {
        display: flex; flex-wrap: wrap; gap: 16rpx; margin-top: 16rpx;
        .sku-option {
          padding: 12rpx 24rpx; border: 2rpx solid #ebedf0; border-radius: 8rpx;
          font-size: 24rpx; color: #323233;
          &.active { border-color: #1989FA; color: #1989FA; background: #ecf5ff; }
        }
      }
    }
    .sku-footer { padding: 28rpx;
      .sku-btn {
        text-align: center; padding: 22rpx 0; background: linear-gradient(135deg, #1989FA, #4facfe);
        color: #fff; font-size: 30rpx; font-weight: 600; border-radius: 40rpx;
      }
    }
  }
}
</style>
