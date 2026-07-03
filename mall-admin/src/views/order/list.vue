<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :model="queryForm" inline>
        <el-form-item label="订单号">
          <el-input v-model="queryForm.orderNo" placeholder="请输入订单号" clearable style="width: 200px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格区域 -->
    <div class="table-bar">
      <div class="table-toolbar">
        <span class="total-tip">共 {{ total }} 条记录</span>
        <div class="toolbar-buttons">
          <el-button type="success" :icon="Download" @click="handleExport">导出所有</el-button>
          <el-button type="warning" :icon="Download" @click="handleExportSelected" :disabled="selectedIds.length === 0">导出选中</el-button>
        </div>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="orderNo" label="订单号" width="180" show-overflow-tooltip />
        <el-table-column prop="username" label="客户" width="120" show-overflow-tooltip>
          <template #default="{ row }">{{ row.username || '-' }}</template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="金额" width="100" align="center">
          <template #default="{ row }">¥{{ row.totalAmount?.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" align="center" />
        <el-table-column prop="payTime" label="支付时间" width="170" align="center">
          <template #default="{ row }">{{ row.payTime || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" :icon="View" @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === 0" link type="success" size="small" @click="handleAction(row, 'pay')">支付</el-button>
            <el-button v-if="row.status === 1" link type="warning" size="small" @click="handleAction(row, 'ship')">发货</el-button>
            <el-button v-if="row.status === 2" link type="primary" size="small" @click="handleAction(row, 'receive')">收货</el-button>
            <el-button v-if="[0, 1].includes(row.status)" link type="danger" size="small" @click="handleAction(row, 'cancel')">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.size"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @change="fetchData"
        />
      </div>
    </div>

    <!-- 详情抽屉 -->
    <el-drawer v-model="detailVisible" title="订单详情" :size="500" direction="rtl">
      <template v-if="detailData">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="订单 ID">{{ detailData.id }}</el-descriptions-item>
          <el-descriptions-item label="订单号">{{ detailData.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="客户">{{ detailData.username || '-' }}</el-descriptions-item>
          <el-descriptions-item label="总金额">¥{{ detailData.totalAmount?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusTagType(detailData.status)" size="small">{{ statusLabel(detailData.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="收货地址">{{ detailData.address || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ detailData.payTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="发货时间">{{ detailData.shipTime || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-divider>订单明细</el-divider>
        <el-table :data="detailData.items || []" border size="small">
          <el-table-column prop="productName" label="商品" min-width="120" />
          <el-table-column prop="quantity" label="数量" width="80" align="center" />
          <el-table-column prop="price" label="单价" width="100" align="center">
            <template #default="{ row }">¥{{ row.price?.toFixed(2) }}</template>
          </el-table-column>
        </el-table>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getOrderListApi, getOrderDetailApi, payOrderApi, shipOrderApi, receiveOrderApi, cancelOrderApi, exportOrderApi } from '@/api/order'
import { downloadBlob } from '@/utils/download'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View, Download } from '@element-plus/icons-vue'
import type { Order } from '@/types'

const loading = ref(false)
const tableData = ref<Order[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])

const statusOptions = [
  { label: '待支付', value: 0 },
  { label: '待发货', value: 1 },
  { label: '待收货', value: 2 },
  { label: '已完成', value: 3 },
  { label: '已取消', value: 4 }
]

const queryForm = reactive({
  page: 1,
  size: 10,
  orderNo: '',
  status: undefined as number | undefined
})

async function fetchData() {
  loading.value = true
  try {
    const data = await getOrderListApi(queryForm)
    tableData.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryForm.page = 1
  fetchData()
}

function handleSelectionChange(selection: Order[]) {
  selectedIds.value = selection.map(item => item.id)
}

async function handleExport() {
  try {
    const blob = await exportOrderApi()
    downloadBlob(blob, '订单数据.xlsx')
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

async function handleExportSelected() {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要导出的订单')
    return
  }
  try {
    const blob = await exportOrderApi(selectedIds.value)
    downloadBlob(blob, '订单数据.xlsx')
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

function handleReset() {
  queryForm.orderNo = ''
  queryForm.status = undefined
  handleSearch()
}

function statusLabel(status: number): string {
  return statusOptions.find((s) => s.value === status)?.label || '未知'
}

function statusTagType(status: number): 'info' | 'warning' | 'primary' | 'success' | 'danger' {
  const map: Record<number, 'info' | 'warning' | 'primary' | 'success' | 'danger'> = {
    0: 'warning', 1: 'primary', 2: 'info', 3: 'success', 4: 'danger', 5: 'info'
  }
  return map[status] || 'info'
}

// 详情
const detailVisible = ref(false)
const detailData = ref<Order | null>(null)

async function handleDetail(row: Order) {
  try {
    detailData.value = await getOrderDetailApi(row.id)
  } catch {
    detailData.value = row
  }
  detailVisible.value = true
}

// 订单操作
const actionMap: Record<string, { api: (id: number) => Promise<any>, label: string }> = {
  pay: { api: payOrderApi, label: '支付' },
  ship: { api: shipOrderApi, label: '发货' },
  receive: { api: receiveOrderApi, label: '确认收货' },
  cancel: { api: cancelOrderApi, label: '取消' }
}

async function handleAction(row: Order, action: string) {
  const config = actionMap[action]
  if (!config) return
  await ElMessageBox.confirm(`确认${config.label}订单"${row.orderNo}"吗？`, '提示', { type: 'warning' })
  await config.api(row.id)
  ElMessage.success(`${config.label}成功`)
  fetchData()
}

onMounted(() => fetchData())
</script>

<style lang="scss" scoped>
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: $spacing-xl;
}

.total-tip {
  font-size: $font-size-md;
  color: $text-secondary;
}

.table-bar {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;
}

.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.toolbar-buttons {
  display: flex;
  gap: $spacing-sm;
}
</style>
