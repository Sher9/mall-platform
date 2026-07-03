<template>
  <div class="cart-page">
    <h1 class="page-title">购物车</h1>

    <!-- 购物车有商品 -->
    <div v-if="cartStore.items.length > 0" class="cart-content">
      <!-- 购物车表头 -->
      <div class="cart-header">
        <el-checkbox
          :model-value="isAllChecked"
          @change="cartStore.toggleCheckAll(!isAllChecked)"
        >
          全选
        </el-checkbox>
        <span class="header-product">商品信息</span>
        <span class="header-price">单价</span>
        <span class="header-quantity">数量</span>
        <span class="header-total">小计</span>
        <span class="header-action">操作</span>
      </div>

      <!-- 购物车列表 -->
      <div class="cart-list">
        <div v-for="item in cartStore.items" :key="item.id" class="cart-item">
          <div class="item-check">
            <el-checkbox
              :model-value="item.checked"
              @change="cartStore.toggleCheck(item.id)"
            />
          </div>
          <div class="item-product" @click="navigateTo(`/product/${item.productId}`)">
            <img :src="item.imageUrl || '/placeholder.png'" :alt="item.productName" />
            <span class="product-name">{{ item.productName }}</span>
          </div>
          <div class="item-price">
            ¥{{ item.price?.toFixed(2) }}
          </div>
          <div class="item-quantity">
            <el-input-number
              :model-value="item.quantity"
              :min="1"
              :max="99"
              size="small"
              @change="(val: number) => cartStore.updateQuantity(item.id, val)"
            />
          </div>
          <div class="item-total">
            ¥{{ (item.price * item.quantity).toFixed(2) }}
          </div>
          <div class="item-action">
            <el-button text type="danger" @click="handleRemove(item.id)">
              删除
            </el-button>
          </div>
        </div>
      </div>

      <!-- 底部结算栏 -->
      <div class="cart-footer">
        <div class="footer-left">
          <el-checkbox
            :model-value="isAllChecked"
            @change="cartStore.toggleCheckAll(!isAllChecked)"
          >
            全选
          </el-checkbox>
          <el-button text type="danger" @click="handleClearChecked">
            删除选中商品
          </el-button>
        </div>
        <div class="footer-right">
          <div class="total-info">
            已选 <span class="count">{{ cartStore.checkedItems.length }}</span> 件商品
            &nbsp;&nbsp;合计：<span class="total-price">¥{{ cartStore.checkedTotalPrice.toFixed(2) }}</span>
          </div>
          <el-button
            type="danger"
            size="large"
            :disabled="cartStore.checkedItems.length === 0"
            @click="handleCheckout"
          >
            去结算
          </el-button>
        </div>
      </div>
    </div>

    <!-- 购物车为空 -->
    <div v-else class="empty-cart">
      <el-icon :size="80"><ShoppingCart /></el-icon>
      <p>购物车是空的</p>
      <el-button type="primary" size="large" @click="navigateTo('/product')">
        去逛逛
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ShoppingCart } from '@element-plus/icons-vue'
import { useCartStore } from '@/store/cart'

definePageMeta({
  layout: 'default'
})

useHead({
  title: '购物车'
})

const cartStore = useCartStore()

// 是否全选
const isAllChecked = computed(() => {
  return cartStore.items.length > 0 && cartStore.items.every(item => item.checked)
})

// 删除商品
const handleRemove = (id: string) => {
  ElMessageBox.confirm('确定要删除这件商品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    cartStore.removeFromCart(id)
    ElMessage.success('删除成功')
  }).catch(() => {})
}

// 删除选中商品
const handleClearChecked = () => {
  const checkedIds = cartStore.checkedItems.map(item => item.id)
  if (checkedIds.length === 0) {
    ElMessage.warning('请先选择商品')
    return
  }

  ElMessageBox.confirm(`确定要删除选中的 ${checkedIds.length} 件商品吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    checkedIds.forEach(id => cartStore.removeFromCart(id))
    ElMessage.success('删除成功')
  }).catch(() => {})
}

// 去结算
const handleCheckout = () => {
  ElMessage.info('结算功能开发中...')
}

onMounted(() => {
  cartStore.initCart()
})
</script>

<style lang="scss" scoped>
.cart-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 20px;
}

/* 购物车内容 */
.cart-content {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.cart-header {
  display: grid;
  grid-template-columns: 60px 1fr 120px 150px 120px 100px;
  align-items: center;
  padding: 16px 20px;
  background: #fafafa;
  border-bottom: 1px solid #eee;
  font-size: 14px;
  color: #909399;
}

.header-product {
  padding-left: 12px;
}

/* 购物车列表 */
.cart-list {
  .cart-item {
    display: grid;
    grid-template-columns: 60px 1fr 120px 150px 120px 100px;
    align-items: center;
    padding: 20px;
    border-bottom: 1px solid #f5f5f5;
    transition: background 0.3s;

    &:hover {
      background: #fafafa;
    }
  }
}

.item-product {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-left: 12px;
  cursor: pointer;

  img {
    width: 80px;
    height: 80px;
    object-fit: cover;
    border-radius: 8px;
    background: #f5f5f5;
  }

  .product-name {
    font-size: 14px;
    color: #303133;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;

    &:hover {
      color: #409eff;
    }
  }
}

.item-price {
  font-size: 16px;
  color: #303133;
}

.item-total {
  font-size: 16px;
  font-weight: 600;
  color: #f56c6c;
}

/* 底部结算栏 */
.cart-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-top: 1px solid #eee;
  background: #fafafa;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.total-info {
  font-size: 14px;
  color: #606266;

  .count {
    color: #f56c6c;
    font-weight: 600;
  }

  .total-price {
    font-size: 24px;
    font-weight: 700;
    color: #f56c6c;
  }
}

/* 空购物车 */
.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  color: #909399;

  p {
    font-size: 18px;
    margin: 16px 0 24px;
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .cart-header,
  .cart-item {
    grid-template-columns: 40px 1fr 80px 100px 80px 60px;
    font-size: 12px;
  }

  .item-product img {
    width: 50px;
    height: 50px;
  }
}
</style>
