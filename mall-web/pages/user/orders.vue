<template>
  <div class="orders-page">
    <h1 class="page-title">我的订单</h1>

    <!-- 订单状态筛选 -->
    <div class="order-tabs">
      <div
        v-for="tab in tabs"
        :key="tab.value"
        class="tab-item"
        :class="{ active: currentTab === tab.value }"
        @click="handleTabChange(tab.value)"
      >
        {{ tab.label }}
      </div>
    </div>

    <!-- 订单列表 -->
    <div v-if="orders.length > 0" class="order-list">
      <div v-for="order in orders" :key="order.orderId" class="order-card">
        <!-- 订单头部 -->
        <div class="order-header">
          <span class="order-time">{{ order.createTime }}</span>
          <span class="order-no">订单号：{{ order.orderNo }}</span>
          <span class="order-status" :class="getStatusClass(order.status)">
            {{ getStatusText(order.status) }}
          </span>
        </div>

        <!-- 订单商品 -->
        <div class="order-body">
          <div class="order-product">
            <img
              :src="order.product?.imageUrl || '/placeholder.png'"
              :alt="order.product?.productName"
              class="product-img"
            />
            <div class="product-info">
              <h3>{{ order.product?.productName || '商品已下架' }}</h3>
              <p class="product-price">¥{{ order.product?.price?.toFixed(2) || '0.00' }}</p>
            </div>
            <div class="order-amount">
              <p>数量：{{ order.quantity }}</p>
              <p class="total-amount">合计：¥{{ order.totalAmount?.toFixed(2) }}</p>
            </div>
          </div>
        </div>

        <!-- 订单操作 -->
        <div class="order-footer">
          <el-button
            v-if="order.status === 0"
            type="primary"
            size="small"
            @click="handlePay(order)"
          >
            去支付
          </el-button>
          <el-button
            v-if="order.status === 1"
            size="small"
            disabled
          >
            等待发货
          </el-button>
          <el-button
            v-if="order.status === 2"
            type="success"
            size="small"
            @click="handleReceive(order)"
          >
            确认收货
          </el-button>
          <el-button
            v-if="order.status === 0"
            size="small"
            @click="handleCancel(order)"
          >
            取消订单
          </el-button>
          <el-button
            size="small"
            @click="navigateTo(`/product/${order.productId}`)"
          >
            再次购买
          </el-button>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="!loading" class="empty-state">
      <el-icon :size="64"><List /></el-icon>
      <p>暂无订单</p>
      <el-button type="primary" @click="navigateTo('/product')">去逛逛</el-button>
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
import { List } from '@element-plus/icons-vue'
import { useOrder } from '~/composables/useOrder'
import { useUserStore } from '@/store/user'
import type { OrderInfo } from '~/types'

definePageMeta({
  layout: 'default'
})

useHead({
  title: '我的订单'
})

const userStore = useUserStore()
const router = useRouter()
const { getOrderList, payOrder, cancelOrder, receiveOrder } = useOrder()

const loading = ref(false)
const orders = ref<OrderInfo[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentTab = ref(-1)

const tabs = [
  { label: '全部', value: -1 },
  { label: '待付款', value: 0 },
  { label: '待发货', value: 1 },
  { label: '待收货', value: 2 },
  { label: '已完成', value: 3 }
]

// 获取订单列表
const fetchOrders = async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login?redirect=/user/orders')
    return
  }

  loading.value = true
  try {
    const params: any = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }

    // 这里需要根据状态筛选，实际项目中后端应该支持
    // 这里简单演示

    const result = await getOrderList(params)
    orders.value = result.records || []
    total.value = result.total || 0
  } catch (error) {
    console.error('获取订单失败', error)
    ElMessage.error('获取订单失败')
  } finally {
    loading.value = false
  }
}

// 切换 Tab
const handleTabChange = (status: number) => {
  currentTab.value = status
  currentPage.value = 1
  fetchOrders()
}

// 页码变化
const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchOrders()
}

// 获取状态文本
const getStatusText = (status: number) => {
  const map: Record<number, string> = {
    0: '待付款',
    1: '待发货',
    2: '待收货',
    3: '已完成',
    4: '已取消'
  }
  return map[status] || '未知'
}

// 获取状态样式类
const getStatusClass = (status: number) => {
  const map: Record<number, string> = {
    0: 'status-pending',
    1: 'status-shipped',
    2: 'status-delivered',
    3: 'status-completed',
    4: 'status-cancelled'
  }
  return map[status] || ''
}

// 支付
const handlePay = async (order: OrderInfo) => {
  try {
    await payOrder(order.orderId)
    ElMessage.success('支付成功')
    fetchOrders()
  } catch (error: any) {
    ElMessage.error(error.message || '支付失败')
  }
}

// 取消订单
const handleCancel = async (order: OrderInfo) => {
  ElMessageBox.confirm('确定要取消这个订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await cancelOrder(order.orderId)
      ElMessage.success('订单已取消')
      fetchOrders()
    } catch (error: any) {
      ElMessage.error(error.message || '取消失败')
    }
  }).catch(() => {})
}

// 确认收货
const handleReceive = async (order: OrderInfo) => {
  try {
    await receiveOrder(order.orderId)
    ElMessage.success('确认收货成功')
    fetchOrders()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    router.push('/login?redirect=/user/orders')
    return
  }
  fetchOrders()
})
</script>

<style lang="scss" scoped>
.orders-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 20px;
}

/* 订单 Tab */
.order-tabs {
  display: flex;
  gap: 0;
  margin-bottom: 20px;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.tab-item {
  flex: 1;
  padding: 14px 20px;
  text-align: center;
  cursor: pointer;
  font-size: 15px;
  color: #606266;
  transition: all 0.3s;
  border-bottom: 2px solid transparent;

  &:hover {
    color: #409eff;
    background: rgba(64, 158, 255, 0.05);
  }

  &.active {
    color: #409eff;
    font-weight: 600;
    border-bottom-color: #409eff;
    background: rgba(64, 158, 255, 0.05);
  }
}

/* 订单列表 */
.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.order-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 16px 20px;
  background: #fafafa;
  font-size: 14px;
  color: #909399;

  .order-status {
    margin-left: auto;
    font-weight: 600;

    &.status-pending { color: #e6a23c; }
    &.status-shipped { color: #409eff; }
    &.status-delivered { color: #67c23a; }
    &.status-completed { color: #909399; }
    &.status-cancelled { color: #f56c6c; }
  }
}

.order-body {
  padding: 20px;
}

.order-product {
  display: flex;
  align-items: center;
  gap: 20px;

  .product-img {
    width: 80px;
    height: 80px;
    object-fit: cover;
    border-radius: 8px;
    background: #f5f5f5;
  }

  .product-info {
    flex: 1;

    h3 {
      font-size: 16px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 8px;
    }

    .product-price {
      color: #f56c6c;
      font-size: 15px;
    }
  }

  .order-amount {
    text-align: right;
    font-size: 14px;
    color: #606266;

    .total-amount {
      font-size: 16px;
      font-weight: 600;
      color: #f56c6c;
      margin-top: 4px;
    }
  }
}

.order-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #f5f5f5;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  background: #fff;
  border-radius: 12px;
  color: #909399;

  p {
    margin: 16px 0 24px;
    font-size: 16px;
  }
}

.loading-container {
  padding: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

/* 响应式 */
@media (max-width: 768px) {
  .order-tabs {
    overflow-x: auto;
  }

  .tab-item {
    flex: none;
    padding: 12px 16px;
    font-size: 14px;
  }

  .order-product {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
