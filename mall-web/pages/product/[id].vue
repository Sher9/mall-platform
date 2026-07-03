<template>
  <div class="product-detail-page" v-if="product">
    <!-- 面包屑 -->
    <div class="breadcrumb">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/product' }">商品列表</el-breadcrumb-item>
        <el-breadcrumb-item>{{ product.productName }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 商品信息 -->
    <div class="product-main">
      <!-- 左侧图片 -->
      <div class="product-gallery">
        <div class="main-image">
          <img :src="product.imageUrl || '/placeholder.png'" :alt="product.productName" />
        </div>
      </div>

      <!-- 右侧信息 -->
      <div class="product-detail-info">
        <h1 class="product-title">{{ product.productName }}</h1>

        <div class="product-brief">
          <span class="brand">品牌：{{ product.brand || '暂无' }}</span>
          <span class="category">分类：{{ product.category || '暂无' }}</span>
        </div>

        <!-- 价格区 -->
        <div class="price-section">
          <div class="current-price">
            <span class="price-symbol">¥</span>
            <span class="price-num">{{ product.price?.toFixed(2) }}</span>
          </div>
          <div class="unit">/{{ product.unit || '件' }}</div>
        </div>

        <!-- 商品描述 -->
        <div class="description" v-if="product.description">
          <h3>商品描述</h3>
          <p>{{ product.description }}</p>
        </div>

        <!-- 购买操作 -->
        <div class="purchase-section">
          <div class="quantity-selector">
            <span class="label">数量</span>
            <el-input-number
              v-model="quantity"
              :min="1"
              :max="99"
              size="large"
            />
          </div>

          <div class="action-buttons">
            <el-button
              type="primary"
              size="large"
              @click="handleAddToCart"
              :icon="Plus"
            >
              加入购物车
            </el-button>
            <el-button
              type="danger"
              size="large"
              @click="handleBuyNow"
            >
              立即购买
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 商品详情 Tabs -->
    <div class="product-tabs">
      <el-tabs v-model="activeTab" type="card">
        <el-tab-pane label="商品详情" name="detail">
          <div class="tab-content">
            <div v-if="product.description">
              <h3>商品描述</h3>
              <p>{{ product.description }}</p>
            </div>
            <div class="detail-table">
              <table>
                <tbody>
                <tr>
                  <td class="label">商品名称</td>
                  <td>{{ product.productName }}</td>
                </tr>
                <tr>
                  <td class="label">商品编号</td>
                  <td>{{ product.productNo }}</td>
                </tr>
                <tr>
                  <td class="label">品牌</td>
                  <td>{{ product.brand || '暂无' }}</td>
                </tr>
                <tr>
                  <td class="label">分类</td>
                  <td>{{ product.category || '暂无' }}</td>
                </tr>
                <tr>
                  <td class="label">单位</td>
                  <td>{{ product.unit || '件' }}</td>
                </tr>
                <tr>
                  <td class="label">价格</td>
                  <td class="price">¥{{ product.price?.toFixed(2) }}</td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="规格参数" name="specs">
          <div class="tab-content">
            <p>规格参数正在完善中...</p>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>

  <!-- 加载中 -->
  <div v-else class="loading-container">
    <el-skeleton :rows="10" animated />
  </div>
</template>

<script setup lang="ts">
import { Plus } from '@element-plus/icons-vue'
import { useProduct } from '~/composables/useProduct'
import { useCartStore } from '@/store/cart'
import type { Product } from '~/types'

definePageMeta({
  layout: 'default'
})

const route = useRoute()
const router = useRouter()
const { getProductDetail } = useProduct()
const cartStore = useCartStore()

const product = ref<Product | null>(null)
const quantity = ref(1)
const activeTab = ref('detail')

// 获取商品详情
const fetchProduct = async () => {
  const id = Number(route.params.id)
  if (isNaN(id)) {
    ElMessage.error('商品ID无效')
    router.push('/product')
    return
  }

  try {
    product.value = await getProductDetail(id)
    useHead({
      title: product.value?.productName || '商品详情'
    })
  } catch (error) {
    console.error('获取商品详情失败', error)
    ElMessage.error('获取商品详情失败')
  }
}

// 添加到购物车
const handleAddToCart = () => {
  if (!product.value) return

  cartStore.addToCart({
    productId: product.value.productId,
    productName: product.value.productName,
    price: product.value.price,
    imageUrl: product.value.imageUrl
  }, quantity.value)
  ElMessage.success('已添加到购物车')
}

// 立即购买
const handleBuyNow = () => {
  if (!product.value) return

  // 这里跳转到确认订单页
  ElMessage.info('立即购买功能开发中...')
}

onMounted(() => {
  fetchProduct()
  cartStore.initCart()
})

// 监听路由变化
watch(
  () => route.params.id,
  () => {
    fetchProduct()
  }
)
</script>

<style lang="scss" scoped>
.product-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.breadcrumb {
  margin-bottom: 20px;
}

/* 商品主体 */
.product-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  margin-bottom: 40px;
  background: #fff;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

/* 图片区 */
.product-gallery {
  .main-image {
    width: 100%;
    aspect-ratio: 1;
    border-radius: 8px;
    overflow: hidden;
    background: #f8f8f8;
    display: flex;
    align-items: center;
    justify-content: center;

    img {
      max-width: 100%;
      max-height: 100%;
      object-fit: cover;
    }
  }
}

/* 信息区 */
.product-detail-info {
  .product-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 12px;
  }
}

.product-brief {
  display: flex;
  gap: 24px;
  margin-bottom: 20px;
  color: #909399;
  font-size: 14px;
}

/* 价格区 */
.price-section {
  display: flex;
  align-items: baseline;
  gap: 8px;
  padding: 20px;
  background: linear-gradient(135deg, #fff5f5, #fff0f0);
  border-radius: 8px;
  margin-bottom: 24px;

  .current-price {
    .price-symbol {
      font-size: 20px;
      color: #f56c6c;
      font-weight: 600;
    }

    .price-num {
      font-size: 36px;
      color: #f56c6c;
      font-weight: 700;
    }
  }

  .unit {
    color: #909399;
    font-size: 14px;
  }
}

/* 描述 */
.description {
  margin-bottom: 24px;

  h3 {
    font-size: 16px;
    margin-bottom: 8px;
    color: #606266;
  }

  p {
    color: #909399;
    line-height: 1.8;
    font-size: 14px;
  }
}

/* 购买操作 */
.purchase-section {
  .quantity-selector {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 24px;

    .label {
      font-size: 15px;
      color: #606266;
    }
  }
}

.action-buttons {
  display: flex;
  gap: 16px;

  .el-button {
    flex: 1;
    height: 50px;
    font-size: 16px;
    border-radius: 8px;
  }
}

/* Tabs */
.product-tabs {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.tab-content {
  padding: 20px 0;

  h3 {
    font-size: 18px;
    margin-bottom: 16px;
    color: #303133;
  }

  p {
    color: #606266;
    line-height: 1.8;
  }
}

.detail-table {
  margin-top: 20px;

  table {
    width: 100%;
    border-collapse: collapse;

    tr {
      border-bottom: 1px solid #eee;
    }

    td {
      padding: 12px 16px;
      font-size: 14px;

      &.label {
        width: 120px;
        background: #fafafa;
        color: #909399;
        text-align: right;
        padding-right: 20px;
      }

      &.price {
        color: #f56c6c;
        font-weight: 600;
        font-size: 16px;
      }
    }
  }
}

.loading-container {
  padding: 40px;
}

/* 响应式 */
@media (max-width: 768px) {
  .product-main {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 16px;
  }

  .action-buttons {
    flex-direction: column;
  }
}
</style>
