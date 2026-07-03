// 提示框
export function showToast(title: string, icon: 'success' | 'error' | 'loading' | 'none' = 'none'): void {
  uni.showToast({ title, icon, duration: 2000 })
}

// 加载动画
export function showLoading(title: string = '加载中...'): void {
  uni.showLoading({ title, mask: true })
}

export function hideLoading(): void {
  uni.hideLoading()
}

// 格式化价格
export function formatPrice(price: string | number): string {
  const numPrice = typeof price === 'string' ? parseFloat(price) : price
  return isNaN(numPrice) ? '0.00' : numPrice.toFixed(2)
}

// 防抖
export function debounce<T extends (...args: any[]) => any>(fn: T, delay: number = 300): (...args: Parameters<T>) => void {
  let timer: ReturnType<typeof setTimeout> | null = null
  return function (...args: Parameters<T>) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => fn.apply(this, args), delay)
  }
}

// 节流
export function throttle<T extends (...args: any[]) => any>(fn: T, delay: number = 300): (...args: Parameters<T>) => void {
  let last = 0
  return function (...args: Parameters<T>) {
    const now = Date.now()
    if (now - last >= delay) {
      last = now
      fn.apply(this, args)
    }
  }
}

// 确认对话框（返回 Promise<boolean>）
export function showDialog(title: string, message: string): Promise<boolean> {
  return new Promise((resolve) => {
    uni.showModal({
      title,
      content: message,
      confirmText: '确认',
      cancelText: '取消',
      success: (res) => resolve(res.confirm),
      fail: () => resolve(false)
    })
  })
}
