<template>
  <div class="cart">
    <div class="page-head">
      <div>
        <h1 class="page-title">购物车</h1>
        <p class="page-desc">管理待结算的商品清单。</p>
      </div>
    </div>

    <div class="card">
      <table v-if="cartStore.items.length > 0" class="data-table">
        <thead>
          <tr>
            <th>商品名称</th>
            <th>单价</th>
            <th>数量</th>
            <th>小计</th>
            <th class="col-action">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in cartStore.items" :key="item.productId">
            <td>{{ item.productName }}</td>
            <td>¥{{ item.price }}</td>
            <td>
              <div class="qty-control">
                <button class="btn-default btn-sm" @click="cartStore.updateQuantity(item.productId, item.quantity - 1)">-</button>
                <span>{{ item.quantity }}</span>
                <button class="btn-default btn-sm" @click="cartStore.updateQuantity(item.productId, item.quantity + 1)">+</button>
              </div>
            </td>
            <td>¥{{ (item.price * item.quantity).toFixed(2) }}</td>
            <td class="col-action">
              <button class="btn-link danger" @click="cartStore.removeItem(item.productId)">移除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-else class="empty">购物车为空</div>

      <div v-if="cartStore.items.length > 0" class="footer">
        <div class="total">
          共 {{ cartStore.totalCount }} 件商品，合计：<span class="price">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
        </div>
        <div class="actions">
          <button class="btn-default" @click="cartStore.clear()">清空购物车</button>
          <button class="btn-primary" @click="handleCheckout">结算</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { createOrder } from '@/api/order'

const cartStore = useCartStore()
const router = useRouter()

async function handleCheckout() {
  if (!confirm(`确认下单？合计 ¥${cartStore.totalPrice.toFixed(2)}`)) return
  try {
    await createOrder({
      items: cartStore.items.map((i) => ({
        productId: i.productId,
        quantity: i.quantity
      }))
    })
    alert('下单成功')
    cartStore.clear()
    router.push('/orders')
  } catch (e) {
    // 错误已在拦截器处理
  }
}
</script>

<style lang="scss" scoped>
.cart {
  .qty-control {
    display: inline-flex;
    align-items: center;
    gap: 8px;

    span {
      min-width: 24px;
      text-align: center;
    }
  }

  .empty {
    text-align: center;
    padding: 48px;
    color: $text-color-placeholder;
  }

  .footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 16px;
    margin-top: 16px;
    border-top: 1px solid $border-color;

    .total {
      font-size: 14px;
      color: $text-color-secondary;

      .price {
        color: $danger-color;
        font-size: 20px;
        font-weight: 700;
        margin-left: 4px;
      }
    }

    .actions {
      display: flex;
      gap: 12px;
    }
  }
}
</style>
