<template>
  <div class="order-list">
    <div class="toolbar card">
      <div class="search">
        <input v-model="query.orderNo" placeholder="搜索订单号" @keyup.enter="loadData" />
        <button class="btn-primary" @click="loadData">查询</button>
        <button class="btn-default" @click="resetQuery">重置</button>
      </div>
    </div>

    <div class="table card">
      <table>
        <thead>
          <tr>
            <th>订单号</th>
            <th>客户</th>
            <th>金额</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.orderId">
            <td>{{ item.orderNo }}</td>
            <td>{{ item.customerName || item.customerId }}</td>
            <td>¥{{ item.totalAmount }}</td>
            <td>
              <span :class="['status-tag', `status-${item.status}`]">{{ statusMap[item.status] || '未知' }}</span>
            </td>
            <td>{{ item.createTime }}</td>
            <td>
              <button v-if="item.status === 0" class="btn-primary btn-sm" @click="handlePay(item)">支付</button>
              <button v-if="item.status === 1" class="btn-default btn-sm" @click="handleShip(item)">发货</button>
              <button class="btn-danger btn-sm" @click="handleDelete(item)">删除</button>
            </td>
          </tr>
          <tr v-if="list.length === 0">
            <td colspan="6" class="empty">暂无数据</td>
          </tr>
        </tbody>
      </table>
      <div class="pagination">
        <button class="btn-default btn-sm" :disabled="query.pageNum <= 1" @click="changePage(-1)">上一页</button>
        <span>第 {{ query.pageNum }} 页</span>
        <button class="btn-default btn-sm" :disabled="list.length < query.pageSize" @click="changePage(1)">下一页</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { getOrderList, updateOrderStatus, deleteOrder } from '@/api/order'

const list = ref([])
const statusMap = { 0: '待支付', 1: '已支付', 2: '已发货', 3: '已完成', 4: '已取消' }
const query = reactive({ orderNo: '', pageNum: 1, pageSize: 10 })

async function loadData() {
  try {
    const res = await getOrderList(query)
    list.value = res.data?.records || res.data || []
  } catch (e) {
    list.value = []
  }
}

function resetQuery() {
  query.orderNo = ''
  query.pageNum = 1
  loadData()
}

function changePage(delta) {
  query.pageNum += delta
  loadData()
}

async function handlePay(item) {
  await updateOrderStatus(item.orderId, 1)
  loadData()
}

async function handleShip(item) {
  await updateOrderStatus(item.orderId, 2)
  loadData()
}

async function handleDelete(item) {
  if (confirm(`确认删除订单「${item.orderNo}」？`)) {
    await deleteOrder(item.orderId)
    loadData()
  }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.order-list {
  .toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .search {
      display: flex;
      gap: 8px;
      input { width: 240px; }
    }
  }

  .table {
    table {
      width: 100%;
      border-collapse: collapse;
      th, td {
        padding: 10px 12px;
        text-align: left;
        border-bottom: 1px solid $border-color;
      }
      th {
        background: $bg-color;
        font-weight: 600;
        color: $text-color-secondary;
      }
      .empty {
        text-align: center;
        color: $text-color-placeholder;
        padding: 32px;
      }
    }

    .status-tag {
      padding: 2px 8px;
      border-radius: $radius-sm;
      font-size: 12px;

      &.status-0 { background: #fff7e8; color: $warning-color; }
      &.status-1 { background: #e8f3ff; color: $primary-color; }
      &.status-2 { background: #e8ffea; color: $success-color; }
      &.status-3 { background: $bg-color; color: $text-color-secondary; }
      &.status-4 { background: #ffe8e8; color: $danger-color; }
    }

    .pagination {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 12px;
      padding: 16px;
    }
  }

  .btn-sm {
    padding: 4px 12px;
    font-size: 12px;
    margin-right: 4px;
  }
}
</style>
