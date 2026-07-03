<template>
  <div class="product-list-page">
    <div class="page-header">
      <h1>全部商品</h1>
      <div class="sort-options">
        <el-radio-group v-model="sortBy" size="default" @change="handleSortChange">
          <el-radio-button value="default">综合</el-radio-button>
          <el-radio-button value="price-asc">价格↑</el-radio-button>
          <el-radio-button value="price-desc">价格↓</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 商品列表 -->
    <div v-if="products.length > 0" class="product-grid">
      <div
        v-for="product in products"
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
        </div>
        <div class="product-info">
          <h3 class="product-name">{{ product.productName }}</h3>
          <div class="product-meta">
            <span class="product-brand">{{ product.brand }}</span>
            <span class="product-category">{{ product.category }}</span>
          </div>
          <div class="product-price-row">
            <span class="product-price">¥{{ product.price?.toFixed(2) }}</span>
            <el-button type="primary" size="small" @click.stop="handleAddToCart(product)">
              <el-icon><Plus /></el-icon>
              加入购物车
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="!loading" class="empty-state">
      <el-icon :size="64"><Box /></el-icon>
      <p>暂无商品</p>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>

    <!-- 分页 -->
    <div v-if="total > pageSize" class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next, total"
        background
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { Plus, Box } from '@element-plus/icons-vue'
import { useProduct } from '~/composables/useProduct'
import { useCartStore } from '@/store/cart'
import type { Product } from '~/types'

definePageMeta({
  layout: 'default'
})

useHead({
  title: '商品列表'
})

const route = useRoute()
const { getProductList } = useProduct()
const cartStore = useCartStore()

const products = ref<Product[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const sortBy = ref('default')

// 获取商品列表
const fetchProducts = async () => {
  loading.value = true
  try {
    const params: any = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }

    // 搜索关键词
    if (route.query.keyword) {
      params.productName = route.query.keyword as string
    }

    // 分类
    if (route.query.category) {
      params.category = route.query.category as string
    }

    const result = await getProductList(params)
    products.value = result.records || []
    total.value = result.total || 0
  } catch (error) {
    console.error('获取商品失败', error)
    ElMessage.error('获取商品数据失败')
  } finally {
    loading.value = false
  }
}

// 排序变化
const handleSortChange = () => {
  // 实际项目中这里应该调用后端排序接口
  // 这里简单做前端排序演示
  if (sortBy.value === 'price-asc') {
    products.value.sort((a, b) => (a.price || 0) - (b.price || 0))
  } else if (sortBy.value === 'price-desc') {
    products.value.sort((a, b) => (b.price || 0) - (a.price || 0))
  }
}

// 页码变化
const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchProducts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
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

// 监听路由变化
watch(
  () => route.query,
  () => {
    currentPage.value = 1
    fetchProducts()
  },
  { deep: true }
)

onMounted(() => {
  fetchProducts()
})
</script>

<style lang="scss" scoped>
.product-list-page {
  min-height: 60vh;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;

  h1 {
    font-size: 24px;
    font-weight: 600;
  }
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.product-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
  cursor: pointer;
  border: 1px solid #eee;

  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
  }
}

.product-image-wrapper {
  width: 100%;
  aspect-ratio: 1;
  overflow: hidden;
  background: #f8f8f8;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

.product-info {
  padding: 16px;
}

.product-name {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
  height: 45px;
  margin-bottom: 8px;
}

.product-meta {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;

  span {
    font-size: 12px;
    color: #909399;
    padding: 2px 8px;
    background: #f5f5f5;
    border-radius: 4px;
  }
}

.product-price-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.product-price {
  font-size: 20px;
  font-weight: 700;
  color: #f56c6c;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #909399;

  p {
    margin-top: 16px;
    font-size: 16px;
  }
}

.loading-container {
  padding: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

@media (max-width: 992px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .page-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style>
