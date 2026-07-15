<template>
  <div class="order-list">
    <div class="page-head">
      <div>
        <h1 class="page-title">订单管理</h1>
        <p class="page-desc">跟踪订单状态，处理支付与发货。</p>
      </div>
    </div>

    <div class="card">
      <div class="toolbar">
        <div class="search">
          <input v-model="query.orderNo" placeholder="搜索订单号" @keyup.enter="loadData" />
          <button class="btn-primary" @click="loadData">查询</button>
          <button class="btn-default" @click="resetQuery">重置</button>
        </div>
      </div>

      <table class="data-table">
        <thead>
          <tr>
            <th>订单号</th>
            <th>客户</th>
            <th>金额</th>
            <th>状态</th>
            <th>创建时间</th>
            <th class="col-action">操作</th>
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
            <td class="col-action">
              <button v-if="item.status === 0" class="btn-link" @click="handlePay(item)">支付</button>
              <button v-if="item.status === 1" class="btn-link" @click="handleShip(item)">发货</button>
              <button class="btn-link danger" @click="handleDelete(item)">删除</button>
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
import { getOrderList, payOrder, shipOrder, deleteOrder } from '@/api/order'

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
  await payOrder(item.orderId)
  loadData()
}

async function handleShip(item) {
  await shipOrder(item.orderId)
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
