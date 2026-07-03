<!-- 商城首页 -->
<template>
  <view class="page-index">
    <!-- 自定义导航栏 -->
    <view class="nav-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="nav-content">
        <view class="search-box" @click="goSearch">
          <nut-icon name="search" size="28rpx" color="#999" />
          <text class="search-placeholder">搜索商品</text>
        </view>
      </view>
    </view>

    <!-- 轮播图 -->
    <view class="swiper-wrap">
      <swiper
        class="banner-swiper"
        :autoplay="true"
        :interval="3000"
        :circular="true"
        indicator-dots
        indicator-color="rgba(255,255,255,0.5)"
        indicator-active-color="#ffffff"
      >
        <swiper-item v-for="(item, index) in banners" :key="index">
          <image :src="item.image" class="banner-img" mode="aspectFill" />
        </swiper-item>
      </swiper>
    </view>

    <!-- 分类入口 -->
    <view class="category-grid">
      <view
        v-for="(cat, i) in categories"
        :key="i"
        class="category-item"
        @click="goCategory(cat)"
      >
        <view class="cat-icon" :style="{ background: cat.bg }">
          <text class="cat-emoji">{{ cat.icon }}</text>
        </view>
        <text class="cat-name">{{ cat.name }}</text>
      </view>
    </view>

    <!-- 热门推荐 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">热门推荐</text>
        <text class="section-more" @click="goMore">查看更多 ></text>
      </view>
      <view class="product-grid">
        <view
          v-for="(item, i) in hotProducts"
          :key="i"
          class="product-card"
          @click="goProduct(item)"
        >
          <view class="card-img-wrap">
            <image :src="item.image" class="card-img" mode="aspectFill" />
            <view v-if="item.tag" class="card-tag">{{ item.tag }}</view>
          </view>
          <view class="card-info">
            <text class="card-name">{{ item.name }}</text>
            <text class="card-desc">{{ item.desc }}</text>
            <view class="card-price">
              <text class="price-symbol">¥</text>
              <text class="price-num">{{ item.price }}</text>
              <text v-if="item.originalPrice" class="price-original">¥{{ item.originalPrice }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 新品上市 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">新品上市</text>
      </view>
      <view class="new-list">
        <view
          v-for="(item, i) in newProducts"
          :key="i"
          class="new-item"
          @click="goProduct(item)"
        >
          <image :src="item.image" class="new-img" mode="aspectFill" />
          <view class="new-info">
            <text class="new-name">{{ item.name }}</text>
            <text class="new-desc">{{ item.desc }}</text>
            <text class="new-price">¥{{ item.price }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getBannerList, getCategoryList, getHotProducts, getNewProducts } from '@/api/index'

const statusBarHeight = ref(20)

// 轮播图数据
interface Banner {
  id: number
  imageUrl: string
  link: string
}
const banners = ref<Banner[]>([])

// 分类数据
interface Category {
  id: number
  name: string
  icon: string
}
const categories = ref<Category[]>([])

// 热门商品
interface Product {
  id: number
  name: string
  price: number
  image: string
}
const hotProducts = ref<Product[]>([])

// 新品
const newProducts = ref<Product[]>([])

// 获取轮播图
async function fetchBanners() {
  try {
    const res = await getBannerList()
    console.log('res', res)
    if (res.code === 200) {
      banners.value = res.data || []
    }
  } catch (error) {
    console.error('获取轮播图失败:', error)
    // 使用默认数据
    banners.value = [
      { image: 'https://via.placeholder.com/750x360/1989FA/fff?text=Banner+1' },
      { image: 'https://via.placeholder.com/750x360/07C160/fff?text=Banner+2' },
      { image: 'https://via.placeholder.com/750x360/FF976A/fff?text=Banner+3' }
    ]
  }
}

// 获取分类列表
async function fetchCategories() {
  try {
    const res = await getCategoryList()
    if (res.code === 200) {
      // 将后台返回的分类数据转换为前端需要的格式
      categories.value = (res.data || []).map((item: any) => ({
        id: item.categoryId,
        name: item.categoryName,
        icon: item.icon || '📦',
        bg: item.bgColor || '#ecf5ff'
      }))
    }
  } catch (error) {
    console.error('获取分类失败:', error)
    // 使用默认数据
    categories.value = [
      { name: '数码电子', icon: '📱', bg: '#ecf5ff' },
      { name: '服装配饰', icon: '👗', bg: '#fdf2ec' },
      { name: '食品饮料', icon: '🍔', bg: '#fef7e6' },
      { name: '家居生活', icon: '🏠', bg: '#ecf9f2' },
      { name: '美妆护肤', icon: '💄', bg: '#fcebf0' },
      { name: '运动户外', icon: '⚽', bg: '#e8f5e9' },
      { name: '图书文具', icon: '📚', bg: '#f3e8f5' },
      { name: '全部商品', icon: '📦', bg: '#e8f4fd' }
    ]
  }
}

// 获取热门商品
async function fetchHotProducts() {
  try {
    const res = await getHotProducts()
    if (res.code === 200) {
      // 将后台返回的商品数据转换为前端需要的格式
      hotProducts.value = (res.data || []).map((item: any) => ({
        id: item.productId,
        name: item.productName,
        desc: item.description || '',
        price: item.price?.toFixed(2) || '0.00',
        originalPrice: item.originalPrice?.toFixed(2) || '',
        image: item.imageUrl || 'https://via.placeholder.com/300x300',
        tag: item.sales > 100 ? '热销' : ''
      }))
    }
  } catch (error) {
    console.error('获取热门商品失败:', error)
  }
}

// 获取新品
async function fetchNewProducts() {
  try {
    const res = await getNewProducts()
    if (res.code === 200) {
      // 将后台返回的商品数据转换为前端需要的格式
      newProducts.value = (res.data || []).map((item: any) => ({
        id: item.productId,
        name: item.productName,
        desc: item.description || '',
        price: item.price?.toFixed(2) || '0.00',
        image: item.imageUrl || 'https://via.placeholder.com/200x200'
      }))
    }
  } catch (error) {
    console.error('获取新品失败:', error)
  }
}

onMounted(() => {
  console.log('首页 onMounted 触发')
  const sysInfo = uni.getSystemInfoSync()
  statusBarHeight.value = sysInfo.statusBarHeight || 20
  
  // 获取后台数据
  console.log('开始获取轮播图...')
  fetchBanners()
  console.log('开始获取分类...')
  fetchCategories()
  console.log('开始获取热门商品...')
  fetchHotProducts()
  console.log('开始获取新品...')
  fetchNewProducts()
})

function goSearch() {
  uni.showToast({ title: '搜索功能开发中', icon: 'none' })
}

function goCategory(cat) {
  uni.showToast({ title: `进入${cat.name}`, icon: 'none' })
}

function goProduct(item) {
  uni.navigateTo({ url: `/pages/product/product?id=${item.id}` })
}

function goMore() {
  uni.showToast({ title: '更多商品开发中', icon: 'none' })
}
</script>

<style lang="scss" scoped>
.page-index {
  min-height: 100vh;
  background: #f5f6fa;
  padding-bottom: 20rpx;
}

// 自定义导航栏
.nav-bar {
  background: linear-gradient(135deg, #1989FA, #4facfe);
  padding-bottom: 20rpx;
  .nav-content {
    padding: 10rpx 24rpx;
  }
  .search-box {
    display: flex;
    align-items: center;
    background: rgba(255,255,255,0.95);
    border-radius: 36rpx;
    padding: 14rpx 24rpx;
    .search-placeholder {
      margin-left: 12rpx;
      font-size: 26rpx;
      color: #999;
    }
  }
}

// 轮播图
.swiper-wrap {
  padding: 0 24rpx;
  margin-top: -10rpx;
  border-radius: 16rpx;
  overflow: hidden;
  .banner-swiper {
    height: 340rpx;
    border-radius: 16rpx;
  }
  .banner-img {
    width: 100%;
    height: 340rpx;
    border-radius: 16rpx;
  }
}

// 分类网格
.category-grid {
  display: flex;
  flex-wrap: wrap;
  background: #fff;
  margin: 20rpx 24rpx;
  border-radius: 16rpx;
  padding: 24rpx 0 8rpx;
  .category-item {
    width: 25%;
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 20rpx;
    .cat-icon {
      width: 88rpx;
      height: 88rpx;
      border-radius: 24rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      .cat-emoji {
        font-size: 40rpx;
      }
    }
    .cat-name {
      margin-top: 10rpx;
      font-size: 22rpx;
      color: #333;
    }
  }
}

// 区块
.section {
  margin: 20rpx 24rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;
    .section-title {
      font-size: 32rpx;
      font-weight: 700;
      color: #323233;
    }
    .section-more {
      font-size: 24rpx;
      color: #969799;
    }
  }
}

// 商品网格
.product-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  .product-card {
    width: calc(50% - 8rpx);
    background: #fff;
    border-radius: 12rpx;
    overflow: hidden;
    box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
    .card-img-wrap {
      position: relative;
      .card-img {
        width: 100%;
        height: 320rpx;
      }
      .card-tag {
        position: absolute;
        top: 12rpx;
        left: 12rpx;
        background: linear-gradient(135deg, #EE0A24, #FF6B6B);
        color: #fff;
        font-size: 20rpx;
        padding: 4rpx 12rpx;
        border-radius: 6rpx;
      }
    }
    .card-info {
      padding: 16rpx;
      .card-name {
        font-size: 26rpx;
        color: #323233;
        font-weight: 600;
        display: -webkit-box;
        -webkit-line-clamp: 1;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }
      .card-desc {
        display: block;
        font-size: 22rpx;
        color: #969799;
        margin-top: 6rpx;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      .card-price {
        margin-top: 10rpx;
        .price-symbol {
          font-size: 22rpx;
          color: #EE0A24;
          font-weight: 600;
        }
        .price-num {
          font-size: 34rpx;
          color: #EE0A24;
          font-weight: 700;
        }
        .price-original {
          margin-left: 8rpx;
          font-size: 22rpx;
          color: #c8c9cc;
          text-decoration: line-through;
        }
      }
    }
  }
}

// 新品列表
.new-list {
  .new-item {
    display: flex;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f5f5f5;
    &:last-child { border-bottom: none; }
    .new-img {
      width: 180rpx;
      height: 180rpx;
      border-radius: 12rpx;
      flex-shrink: 0;
    }
    .new-info {
      margin-left: 20rpx;
      display: flex;
      flex-direction: column;
      justify-content: center;
      .new-name {
        font-size: 28rpx;
        color: #323233;
        font-weight: 600;
      }
      .new-desc {
        font-size: 24rpx;
        color: #969799;
        margin-top: 8rpx;
      }
      .new-price {
        font-size: 32rpx;
        color: #EE0A24;
        font-weight: 700;
        margin-top: 12rpx;
      }
    }
  }
}
</style>
