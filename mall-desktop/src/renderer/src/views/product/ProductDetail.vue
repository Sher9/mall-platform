<template>
  <div class="product-detail">
    <div v-if="product" class="card">
      <button class="btn-default back-btn" @click="$router.back()">← 返回</button>
      <h2 class="detail-title">{{ product.productName }}</h2>
      <div class="info-grid">
        <div class="info-item"><span class="label">商品ID</span><span class="value">{{ product.productId }}</span></div>
        <div class="info-item"><span class="label">价格</span><span class="value price">¥{{ product.price }}</span></div>
        <div class="info-item"><span class="label">库存</span><span class="value">{{ product.stock }}</span></div>
        <div class="info-item">
          <span class="label">状态</span>
          <span :class="['status-tag', product.status === 1 ? 'on' : 'off']">
            {{ product.status === 1 ? '上架' : '下架' }}
          </span>
        </div>
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
import { useRoute } from 'vue-router'
import { getProductById } from '@/api/product'
import { useCartStore } from '@/stores/cart'

const route = useRoute()
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

  .detail-title {
    margin-bottom: 24px;
    font-size: 22px;
    font-weight: 600;
    color: $text-color;
  }

  .info-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px 24px;
    margin-bottom: 28px;

    .info-item {
      display: flex;
      align-items: center;
      gap: 12px;

      .label {
        color: $text-color-secondary;
        font-size: 13px;
        min-width: 56px;
      }

      .value {
        font-size: 15px;
        color: $text-color;

        &.price {
          color: $danger-color;
          font-weight: 600;
          font-size: 18px;
        }
      }
    }
  }

  .actions {
    margin-top: 8px;
  }

  .loading {
    text-align: center;
    padding: 40px;
    color: $text-color-placeholder;
  }
}
</style>
