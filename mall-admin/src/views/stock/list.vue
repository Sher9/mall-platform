<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :model="queryForm" inline>
        <el-form-item label="商品名称">
          <el-input v-model="queryForm.productName" placeholder="请输入" clearable style="width: 200px" @keyup.enter="handleSearch" />
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
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增库存</el-button>
          <el-button type="success" :icon="Download" @click="handleExport">导出所有</el-button>
          <el-button type="warning" :icon="Download" @click="handleExportSelected" :disabled="selectedIds.length === 0">导出选中</el-button>
        </div>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe @selection-change="handleSelectionChange">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="productName" label="商品名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="stockCount" label="总库存" width="90" align="center" />
        <el-table-column prop="availableCount" label="可用库存" width="90" align="center">
          <template #default="{ row }">
            <span :class="{ 'text-danger': row.availableCount <= 10 }">{{ row.availableCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="lockedCount" label="锁定库存" width="90" align="center" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'info'" size="small">
              {{ row.status === 0 ? '正常' : '冻结' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="170" align="center" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="success" size="small" :icon="Top" @click="handleStockOp(row, 'increase')">入库</el-button>
            <el-button link type="warning" size="small" :icon="Bottom" @click="handleStockOp(row, 'decrease')">出库</el-button>
            <el-button link type="primary" size="small" :icon="Document" @click="handleViewLog(row)">日志</el-button>
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

    <!-- 入库/出库弹窗 -->
    <el-dialog v-model="opVisible" :title="opType === 'increase' ? '入库' : '出库'" width="420px">
      <el-form ref="opFormRef" :model="opForm" :rules="opRules" label-width="80px">
        <el-form-item label="商品">
          <span>{{ currentStock?.productName }}</span>
        </el-form-item>
        <el-form-item label="数量" prop="count">
          <el-input-number v-model="opForm.count" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="opForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="opVisible = false">取消</el-button>
        <el-button type="primary" :loading="opLoading" @click="handleSubmitOp">确定</el-button>
      </template>
    </el-dialog>

    <!-- 库存日志抽屉 -->
    <el-drawer v-model="logVisible" title="库存变动日志" :size="600" direction="rtl">
      <el-timeline v-if="logData.length">
        <el-timeline-item
          v-for="log in logData"
          :key="log.id"
          :timestamp="log.createTime"
          :type="log.changeType === 0 ? 'success' : log.changeType === 1 ? 'danger' : 'warning'"
        >
          <h4>{{ changeTypeLabel(log.changeType) }} - {{ log.productName }}</h4>
          <p>变动数量: {{ log.changeCount }} | 变动前: {{ log.beforeCount }} → 变动后: {{ log.afterCount }}</p>
          <p v-if="log.orderNo">关联订单: {{ log.orderNo }}</p>
          <p v-if="log.remark">备注: {{ log.remark }}</p>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无日志" />
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getStockListApi, increaseStockApi, decreaseStockApi, getStockLogsApi, exportStockApi } from '@/api/stock'
import { downloadBlob } from '@/utils/download'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Top, Bottom, Document, Download } from '@element-plus/icons-vue'

interface StockInfo {
  id: number
  productId: number
  productName: string
  stockCount: number
  availableCount: number
  lockedCount: number
  status: number
  createTime: string
  updateTime?: string
}

interface StockLog {
  id: number
  productId: number
  productName: string
  changeType: number
  changeCount: number
  beforeCount: number
  afterCount: number
  orderNo?: string
  remark?: string
  createTime: string
}

const loading = ref(false)
const tableData = ref<StockInfo[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])

const queryForm = reactive({
  page: 1,
  size: 10,
  productName: ''
})

async function fetchData() {
  loading.value = true
  try {
    const data = await getStockListApi(queryForm)
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

function handleReset() {
  queryForm.productName = ''
  handleSearch()
}

function handleSelectionChange(selection: StockInfo[]) {
  selectedIds.value = selection.map(item => item.id)
}

async function handleExport() {
  try {
    const blob = await exportStockApi()
    downloadBlob(blob, '库存数据.xlsx')
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

async function handleExportSelected() {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要导出的库存')
    return
  }
  try {
    const blob = await exportStockApi(selectedIds.value)
    downloadBlob(blob, '库存数据.xlsx')
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

function handleAdd() {
  ElMessage.info('请通过商品管理创建库存')
}

// 入库/出库
const opVisible = ref(false)
const opLoading = ref(false)
const opType = ref<'increase' | 'decrease'>('increase')
const currentStock = ref<StockInfo | null>(null)
const opFormRef = ref<FormInstance>()
const opForm = reactive({ count: 1, remark: '' })
const opRules: FormRules = {
  count: [{ required: true, message: '请输入数量', trigger: 'blur' }]
}

function handleStockOp(row: StockInfo, type: 'increase' | 'decrease') {
  currentStock.value = row
  opType.value = type
  opForm.count = 1
  opForm.remark = ''
  opVisible.value = true
}

async function handleSubmitOp() {
  const valid = await opFormRef.value?.validate().catch(() => false)
  if (!valid) return
  if (!currentStock.value) return

  opLoading.value = true
  try {
    if (opType.value === 'increase') {
      await increaseStockApi({
        productId: currentStock.value.productId,
        count: opForm.count,
        operator: 'admin',
        remark: opForm.remark || '管理后台入库'
      })
      ElMessage.success('入库成功')
    } else {
      await decreaseStockApi({
        productId: currentStock.value.productId,
        count: opForm.count,
        orderNo: null,
        remark: opForm.remark || '管理后台出库'
      })
      ElMessage.success('出库成功')
    }
    opVisible.value = false
    fetchData()
  } finally {
    opLoading.value = false
  }
}

// 库存日志
const logVisible = ref(false)
const logData = ref<StockLog[]>([])

async function handleViewLog(row: StockInfo) {
  try {
    logData.value = await getStockLogsApi(row.productId)
  } catch {
    logData.value = []
  }
  logVisible.value = true
}

function changeTypeLabel(type: number): string {
  const map: Record<number, string> = { 0: '入库', 1: '出库', 2: '锁定', 3: '解锁' }
  return map[type] || '未知'
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

.text-danger {
  color: $danger-color;
  font-weight: 600;
}
</style>
