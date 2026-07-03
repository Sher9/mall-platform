<template>
  <div class="home-page">
    <!-- 错误提示 -->
    <el-alert 
      v-if="error" 
      :title="errorMessage || '加载数据失败'" 
      type="error" 
      show-icon 
      :closable="false"
      style="margin-bottom: 20px;"
    />

    <!-- 轮播图 -->
    <el-carousel v-if="banners.length > 0" height="320px" type="card" :interval="4000" arrow="hover" class="banner-carousel">
      <el-carousel-item v-for="banner in banners" :key="banner.id || banner.bannerId">
        <div class="banner-item">
          <div class="banner-image" v-if="banner.imageUrl">
            <img :src="banner.imageUrl" :alt="banner.title" />
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 无轮播图时的占位符 -->
    <div v-else class="banner-placeholder">
      <el-carousel height="320px" :interval="4000" arrow="hover" class="banner-carousel">
        <el-carousel-item>
          <div class="banner-item" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
            <div class="banner-content">
              <h2>欢迎来到商城</h2>
              <p>精选好物，品质生活</p>
              <el-button type="primary" size="large" round @click="navigateTo('/product')">
                立即查看
              </el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 热门商品 -->
    <section class="section">
      <div class="section-header">
        <h2 class="section-title">
          <el-icon><Flag /></el-icon>
          热门推荐
        </h2>
        <el-button text type="primary" @click="navigateTo('/product')">
          查看更多 <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>

      <div class="product-grid">
        <div
          v-for="product in hotProducts"
          :key="product.productId"
          class="product-card"
          @click="navigateTo(`/product/${product.productId}`)"
        >
          <div class="product-image-wrapper">
            <img
              :src="product.imageUrl || '/placeholder.png'"
              :alt="product.productName"
              class="product-image"
            />
            <div class="product-tags">
              <span class="tag hot">热卖</span>
            </div>
          </div>
          <div class="product-info">
            <h3 class="product-name">{{ product.productName }}</h3>
            <div class="product-meta">
              <span class="product-brand">{{ product.brand }}</span>
              <span class="product-category">{{ product.category }}</span>
            </div>
            <div class="product-price-row">
              <span class="product-price">¥{{ product.price?.toFixed(2) }}</span>
              <el-button type="primary" size="small" round @click.stop="handleAddToCart(product)">
                <el-icon><Plus /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>
    </section>

    <!-- 新品上市 -->
    <section class="section">
      <div class="section-header">
        <h2 class="section-title">
          <el-icon><ShoppingBag /></el-icon>
          新品上市
        </h2>
      </div>

      <div class="product-grid">
        <div
          v-for="product in newProducts"
          :key="product.productId"
          class="product-card"
          @click="navigateTo(`/product/${product.productId}`)"
        >
          <div class="product-image-wrapper">
            <img
              :src="product.imageUrl || '/placeholder.png'"
              :alt="product.productName"
              class="product-image"
            />
            <div class="product-tags">
              <span class="tag new">新品</span>
            </div>
          </div>
          <div class="product-info">
            <h3 class="product-name">{{ product.productName }}</h3>
            <div class="product-meta">
              <span class="product-brand">{{ product.brand }}</span>
            </div>
            <div class="product-price-row">
              <span class="product-price">¥{{ product.price?.toFixed(2) }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { Flag, ArrowRight, Plus, ShoppingBag } from '@element-plus/icons-vue'
import { useProduct } from '~/composables/useProduct'
import { useCartStore } from '@/store/cart'
import { getBannerList, getHotProducts, getNewProducts } from '~/api/index'
import type { Product } from '~/types'

definePageMeta({
  layout: 'default'
})

useHead({
  title: '首页'
})

// 轮播图数据
const banners = ref<any[]>([])

const { getProductList } = useProduct()
const cartStore = useCartStore()

const loading = ref(false)
const hotProducts = ref<Product[]>([])
const newProducts = ref<Product[]>([])
const error = ref(false)
const errorMessage = ref('')

// 获取轮播图
const fetchBanners = async () => {
  try {
    const data = await getBannerList()
    //遍历data,imageUrl地址需要拼接上服务端地址
    data.forEach((item: any) => {
      item.imageUrl = import.meta.env.VITE_FILE_SERVER_URL + item.imageUrl
    })
    banners.value = data || []
    console.log('获取轮播图成功', banners.value)
  } catch (err: any) {
    console.error('获取轮播图失败', err)
  }
}

// 获取商品数据
const fetchProducts = async () => {
  loading.value = true
  error.value = false
  try {
    const [hotRes, newRes] = await Promise.all([
      getHotProducts(),
      getNewProducts()
    ])
    hotProducts.value = hotRes || []
    newProducts.value = newRes || []
    console.log('获取商品成功', hotProducts.value, newProducts.value)
  } catch (err: any) {
    console.error('获取商品失败', err)
    error.value = true
    errorMessage.value = err.message || '获取商品数据失败'
    ElMessage.error('获取商品数据失败')
  } finally {
    loading.value = false
  }
}

// 添加到购物车
const handleAddToCart = (product: Product) => {
  cartStore.addToCart({
    productId: product.productId,
    productName: product.productName,
    price: product.price,
    imageUrl: product.imageUrl
  })
  ElMessage.success('已添加到购物车')
}

onMounted(() => {
  fetchBanners()
  fetchProducts()
  cartStore.initCart()
})

// 页面跳转
function navigateTo(path: string) {
  const router = useRouter()
  router.push(path)
}
</script>

<style lang="scss" scoped>
.home-page {
  padding-bottom: 60px;
  background: linear-gradient(180deg, #f5f7fa 0%, #ffffff 100%);
}

/* 轮播图 */
.banner-carousel {
  margin-bottom: 40px;
  border-radius: 16px;
  overflow: hidden;

  :deep(.el-carousel__container) {
    height: 420px !important;
  }

  :deep(.el-carousel__indicator button) {
    width: 12px;
    height: 12px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.6);
    transition: all 0.3s;
  }

  :deep(.el-carousel__indicator.is-active button) {
    background: #fff;
    width: 32px;
    border-radius: 6px;
  }

  :deep(.el-carousel__arrow) {
    width: 44px;
    height: 44px;
    background: rgba(255, 255, 255, 0.9);
    color: #333;
    border: none;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    transition: all 0.3s;

    &:hover {
      background: #fff;
      transform: scale(1.1);
    }
  }
}

.banner-item {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 100px;
  color: #fff;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    right: -20%;
    width: 600px;
    height: 600px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    animation: float 6s ease-in-out infinite;
  }

  &::after {
    content: '';
    position: absolute;
    bottom: -30%;
    left: -10%;
    width: 400px;
    height: 400px;
    background: rgba(255, 255, 255, 0.08);
    border-radius: 50%;
    animation: float 8s ease-in-out infinite reverse;
  }
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(20px, -20px);
  }
}

.banner-content {
  max-width: 50%;
  position: relative;
  z-index: 1;

  h2 {
    font-size: 48px;
    font-weight: 800;
    margin-bottom: 20px;
    text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
    letter-spacing: 1px;
  }

  p {
    font-size: 22px;
    margin-bottom: 36px;
    opacity: 0.95;
    line-height: 1.6;
  }

  .el-button {
    padding: 14px 36px;
    font-size: 16px;
    font-weight: 600;
    border: 2px solid #fff;
    background: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(10px);
    transition: all 0.3s;

    &:hover {
      background: #fff;
      color: #333;
      transform: translateY(-2px);
      box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
    }
  }
}

.banner-image {
  position: relative;
  z-index: 1;
  
  img {
    max-height: 400px;
    width:100%;
    border-radius: 16px;
    box-shadow: 0 16px 48px rgba(0, 0, 0, 0.3);
    transition: transform 0.5s;

    &:hover {
      transform: rotate(-3deg) scale(1.05);
    }
  }
}

/* 区块 */
.section {
  margin-bottom: 50px;
  padding: 0 20px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 28px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f0f0f0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    bottom: -16px;
    left: 0;
    width: 60px;
    height: 3px;
    background: linear-gradient(90deg, #409eff, #67c23a);
    border-radius: 2px;
  }

  .el-icon {
    color: #409eff;
    font-size: 32px;
  }
}

/* 商品网格 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.product-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border: 1px solid #ebeef5;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, #409eff, #67c23a);
    opacity: 0;
    transition: opacity 0.3s;
  }

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 16px 48px rgba(0, 0, 0, 0.12);
    border-color: transparent;

    &::before {
      opacity: 1;
    }
  }
}

.product-image-wrapper {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  overflow: hidden;
  background: linear-gradient(135deg, #f5f7fa, #e8ecf1);
  padding: 20px;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.1));
}

.product-card:hover .product-image {
  transform: scale(1.08) rotate(-2deg);
}

.product-tags {
  position: absolute;
  top: 16px;
  left: 16px;
  display: flex;
  gap: 8px;
  z-index: 1;
}

.tag {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  color: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(10px);

  &.hot {
    background: linear-gradient(135deg, #f56c6c, #e6a23c);
    animation: pulse 2s ease-in-out infinite;
  }

  &.new {
    background: linear-gradient(135deg, #409eff, #67c23a);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.product-info {
  padding: 20px;
  background: #fff;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.6;
  height: 50px;
  margin-bottom: 12px;
  transition: color 0.3s;

  .product-card:hover & {
    color: #409eff;
  }
}

.product-meta {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  flex-wrap: wrap;

  span {
    font-size: 12px;
    color: #909399;
    padding: 4px 10px;
    background: #f5f7fa;
    border-radius: 6px;
    transition: all 0.3s;

    &:hover {
      background: #409eff;
      color: #fff;
    }
  }
}

.product-price-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.product-price {
  font-size: 24px;
  font-weight: 800;
  color: #f56c6c;
  text-shadow: 0 2px 4px rgba(245, 108, 108, 0.2);
}

.product-price-row .el-button {
  padding: 8px 16px;
  border-radius: 20px;
  transition: all 0.3s;

  &:hover {
    transform: scale(1.1);
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  }
}

.loading-container {
  padding: 40px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
}

.banner-placeholder {
  margin-bottom: 40px;
}

/* 响应式 */
@media (max-width: 1200px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;
  }

  .banner-item {
    padding: 0 60px;
  }

  .banner-content {
    max-width: 60%;

    h2 {
      font-size: 36px;
    }
  }
}

@media (max-width: 768px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .banner-content {
    max-width: 100%;
    text-align: center;

    h2 {
      font-size: 28px;
    }

    p {
      font-size: 16px;
    }
  }

  .banner-image {
    display: none;
  }

  .section {
    padding: 0 10px;
  }

  .section-title {
    font-size: 22px;
  }
}
</style>
