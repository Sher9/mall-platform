import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCartStore = defineStore('cart', () => {
  const items = ref(JSON.parse(localStorage.getItem('cart') || '[]'))

  const totalCount = computed(() => items.value.length)
  const totalPrice = computed(() =>
    items.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
  )

  function addItem(product, quantity = 1) {
    const existing = items.value.find((i) => i.productId === product.productId)
    if (existing) {
      existing.quantity += quantity
    } else {
      items.value.push({
        productId: product.productId,
        productName: product.productName,
        price: product.price,
        quantity
      })
    }
    saveCart()
  }

  function removeItem(productId) {
    items.value = items.value.filter((i) => i.productId !== productId)
    saveCart()
  }

  function updateQuantity(productId, quantity) {
    const item = items.value.find((i) => i.productId === productId)
    if (item) {
      item.quantity = Math.max(1, quantity)
      saveCart()
    }
  }

  function clear() {
    items.value = []
    saveCart()
  }

  function saveCart() {
    localStorage.setItem('cart', JSON.stringify(items.value))
  }

  return { items, totalCount, totalPrice, addItem, removeItem, updateQuantity, clear }
})
