<template>
  <div class="product-detail">
    <div v-if="product" class="card">
      <button class="btn-default back-btn" @click="$router.back()">返回</button>
      <h2>{{ product.productName }}</h2>
      <div class="info-grid">
        <div class="info-item"><span class="label">商品ID：</span>{{ product.productId }}</div>
        <div class="info-item"><span class="label">价格：</span>¥{{ product.price }}</div>
        <div class="info-item"><span class="label">库存：</span>{{ product.stock }}</div>
        <div class="info-item"><span class="label">状态：</span>{{ product.status === 1 ? '上架' : '下架' }}</div>
      </div>
      <div class="actions">
        <button class="btn-primary" @click="addToCart">加入购物车</button>
      </div>
    </div>
    <div v-else class="card loading">加载中...</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductById } from '@/api/product'
import { useCartStore } from '@/stores/cart'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const product = ref(null)

onMounted(async () => {
  const res = await getProductById(route.params.id)
  product.value = res.data
})

function addToCart() {
  cartStore.addItem(product.value, 1)
  alert('已加入购物车')
}
</script>

<style lang="scss" scoped>
.product-detail {
  .back-btn {
    margin-bottom: 16px;
  }

  h2 {
    margin-bottom: 20px;
  }

  .info-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    margin-bottom: 24px;

    .info-item {
      .label {
        color: $text-color-secondary;
      }
    }
  }

  .actions {
    margin-top: 20px;
  }

  .loading {
    text-align: center;
    padding: 40px;
    color: $text-color-placeholder;
  }
}
</style>
