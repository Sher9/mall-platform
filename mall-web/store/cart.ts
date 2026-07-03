/**
 * 购物车状态管理
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { CartItem } from '~/types'

export const useCartStore = defineStore('cart', () => {
  // 状态
  const items = ref<CartItem[]>([])

  // 计算属性
  const totalCount = computed(() => items.value.reduce((sum, item) => sum + item.quantity, 0))
  const totalPrice = computed(() => items.value.reduce((sum, item) => sum + item.price * item.quantity, 0))
  const checkedItems = computed(() => items.value.filter(item => item.checked))
  const checkedTotalPrice = computed(() => checkedItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0))

  // 从本地存储初始化
  const initCart = () => {
    if (process.client) {
      const saved = localStorage.getItem('cart')
      if (saved) {
        try {
          items.value = JSON.parse(saved)
        } catch (e) {
          items.value = []
        }
      }
    }
  }

  // 保存到本地存储
  const saveCart = () => {
    if (process.client) {
      localStorage.setItem('cart', JSON.stringify(items.value))
    }
  }

  // 添加商品到购物车
  const addToCart = (product: {
    productId: number
    productName: string
    price: number
    imageUrl: string
  }, quantity: number = 1) => {
    const existIndex = items.value.findIndex(item => item.productId === product.productId)

    if (existIndex > -1) {
      items.value[existIndex].quantity += quantity
    } else {
      items.value.push({
        id: `${product.productId}_${Date.now()}`,
        productId: product.productId,
        productName: product.productName,
        price: product.price,
        quantity,
        imageUrl: product.imageUrl,
        checked: true
      })
    }

    saveCart()
  }

  // 移除商品
  const removeFromCart = (id: string) => {
    items.value = items.value.filter(item => item.id !== id)
    saveCart()
  }

  // 更新数量
  const updateQuantity = (id: string, quantity: number) => {
    const item = items.value.find(item => item.id === id)
    if (item) {
      item.quantity = Math.max(1, quantity)
      saveCart()
    }
  }

  // 切换选中状态
  const toggleCheck = (id: string) => {
    const item = items.value.find(item => item.id === id)
    if (item) {
      item.checked = !item.checked
      saveCart()
    }
  }

  // 全选/取消全选
  const toggleCheckAll = (checked: boolean) => {
    items.value.forEach(item => {
      item.checked = checked
    })
    saveCart()
  }

  // 清空购物车
  const clearCart = () => {
    items.value = []
    saveCart()
  }

  return {
    items,
    totalCount,
    totalPrice,
    checkedItems,
    checkedTotalPrice,
    initCart,
    addToCart,
    removeFromCart,
    updateQuantity,
    toggleCheck,
    toggleCheckAll,
    clearCart
  }
})
