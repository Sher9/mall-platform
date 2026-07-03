import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// 购物车商品接口
export interface CartItem {
  id: number
  productId: number
  productName: string
  price: number
  quantity: number
  imageUrl: string
  sku?: string
  checked?: boolean
}

export const useCartStore = defineStore('cart', () => {
  const cartList = ref<CartItem[]>(
    uni.getStorageSync('cartList')
      ? JSON.parse(uni.getStorageSync('cartList'))
      : []
  )

  const cartCount = computed(() =>
    cartList.value.reduce((sum, item) => sum + (item.quantity || 1), 0)
  )

  const cartTotal = computed(() =>
    cartList.value.reduce((sum, item) =>
      sum + (item.price || 0) * (item.quantity || 1), 0
    )
  )

  function saveCart() {
    uni.setStorageSync('cartList', JSON.stringify(cartList.value))
  }

  function addToCart(product: CartItem) {
    const exist = cartList.value.find(
      i => i.id === product.id && i.sku === product.sku
    )
    if (exist) {
      exist.quantity = (exist.quantity || 1) + 1
    } else {
      cartList.value.push({ ...product, quantity: 1 })
    }
    saveCart()
  }

  function removeFromCart(index: number) {
    cartList.value.splice(index, 1)
    saveCart()
  }

  function updateQuantity(index: number, quantity: number) {
    if (quantity < 1) return
    cartList.value[index].quantity = quantity
    saveCart()
  }

  function clearCart() {
    cartList.value = []
    saveCart()
  }

  return {
    cartList,
    cartCount,
    cartTotal,
    addToCart,
    removeFromCart,
    updateQuantity,
    clearCart
  }
})
