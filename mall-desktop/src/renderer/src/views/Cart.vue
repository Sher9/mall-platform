<template>
  <div class="cart">
    <div class="card">
      <h2>购物车</h2>
      <table v-if="cartStore.items.length > 0">
        <thead>
          <tr>
            <th>商品名称</th>
            <th>单价</th>
            <th>数量</th>
            <th>小计</th>
            <th>操作</th>
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
            <td>
              <button class="btn-danger btn-sm" @click="cartStore.removeItem(item.productId)">移除</button>
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
  h2 {
    margin-bottom: 16px;
  }

  table {
    width: 100%;
    border-collapse: collapse;

    th, td {
      padding: 12px;
      text-align: left;
      border-bottom: 1px solid $border-color;
    }

    th {
      background: $bg-color;
      font-weight: 600;
      color: $text-color-secondary;
    }
  }

  .qty-control {
    display: flex;
    align-items: center;
    gap: 8px;

    span {
      min-width: 24px;
      text-align: center;
    }
  }

  .empty {
    text-align: center;
    padding: 40px;
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
      font-size: 16px;

      .price {
        color: $danger-color;
        font-size: 20px;
        font-weight: 700;
      }
    }

    .actions {
      display: flex;
      gap: 12px;
    }
  }

  .btn-sm {
    padding: 2px 10px;
    font-size: 12px;
  }
}
</style>
