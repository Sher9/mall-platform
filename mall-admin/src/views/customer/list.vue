<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :model="queryForm" inline>
        <el-form-item label="关键词">
          <el-input
            v-model="queryForm.keyword"
            placeholder="用户名/昵称/手机号"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
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

      <el-table v-loading="loading" :data="tableData" border stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="avatar" label="头像" width="70" align="center">
          <template #default="{ row }">
            <el-avatar :size="36" :src="row.avatar">
              <el-icon :size="20"><UserFilled /></el-icon>
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" min-width="120" show-overflow-tooltip />
        <el-table-column label="性别" width="70" align="center">
          <template #default="{ row }">
            <el-tag :type="genderTag(row.gender)" size="small">
              {{ genderLabel(row.gender) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" align="center">
          <template #default="{ row }">{{ row.phone || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 1"
              size="small"
              @change="handleToggleStatus(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="registerTime" label="注册时间" width="170" align="center">
          <template #default="{ row }">{{ row.registerTime || row.createTime || '-' }}</template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="170" align="center">
          <template #default="{ row }">
            <span v-if="row.lastLoginTime">{{ row.lastLoginTime }}</span>
            <el-tag v-else type="info" size="small">从未登录</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" :icon="View" @click="handleDetail(row)">详情</el-button>
            <el-button link type="primary" size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" :icon="Delete" @click="handleDelete(row)">删除</el-button>
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
    <el-drawer v-model="detailVisible" title="客户详情" :size="480" direction="rtl">
      <template v-if="detailData">
        <div class="detail-header">
          <el-avatar :size="72" :src="detailData.avatar">
            <el-icon :size="36"><UserFilled /></el-icon>
          </el-avatar>
          <div class="detail-header-info">
            <h3>{{ detailData.nickname }}</h3>
            <p class="detail-username">@{{ detailData.username }}</p>
            <el-tag :type="detailData.status === 1 ? 'success' : 'danger'" size="small">
              {{ detailData.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </div>
        </div>

        <el-divider />

        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="用户 ID">{{ detailData.id }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ detailData.username }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ detailData.nickname }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ genderLabel(detailData.gender) }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ detailData.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ detailData.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="生日">{{ detailData.birthday || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="detailData.status === 1 ? 'success' : 'danger'" size="small">
              {{ detailData.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ detailData.registerTime || detailData.createTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="最后登录时间">{{ detailData.lastLoginTime || '从未登录' }}</el-descriptions-item>
          <el-descriptions-item label="最后登录 IP">{{ detailData.lastLoginIp || '-' }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ detailData.updateTime || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="drawer-footer">
          <el-button type="primary" @click="detailVisible = false; handleEdit(detailData!)">编辑客户</el-button>
        </div>
      </template>
    </el-drawer>

    <!-- 编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑客户"
      width="560px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" placeholder="请输入昵称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="性别">
              <el-radio-group v-model="form.gender">
                <el-radio :value="1">男</el-radio>
                <el-radio :value="2">女</el-radio>
                <el-radio :value="0">未知</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生日">
              <el-date-picker
                v-model="form.birthday"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import {
  getCustomerListApi,
  getCustomerDetailApi,
  updateCustomerApi,
  deleteCustomerApi,
  updateCustomerStatusApi,
  exportCustomerApi
} from '@/api/customer'
import { downloadBlob } from '@/utils/download'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, View, Edit, Delete, UserFilled, Download } from '@element-plus/icons-vue'
import type { Customer } from '@/types'

// ==================== 表格数据 ====================
const loading = ref(false)
const tableData = ref<Customer[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])

const queryForm = reactive({
  page: 1,
  size: 10,
  keyword: '',
  status: undefined as number | undefined
})

async function fetchData() {
  loading.value = true
  try {
    const data = await getCustomerListApi(queryForm)
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

function handleSelectionChange(selection: Customer[]) {
  selectedIds.value = selection.map(item => item.id)
}

async function handleExport() {
  try {
    const blob = await exportCustomerApi()
    downloadBlob(blob, '客户数据.xlsx')
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

async function handleExportSelected() {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要导出的客户')
    return
  }
  try {
    const blob = await exportCustomerApi(selectedIds.value)
    downloadBlob(blob, '客户数据.xlsx')
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

function handleReset() {
  queryForm.keyword = ''
  queryForm.status = undefined
  handleSearch()
}

// ==================== 工具函数 ====================
function genderLabel(gender?: number): string {
  if (gender === 1) return '男'
  if (gender === 2) return '女'
  return '未知'
}

function genderTag(gender?: number): 'primary' | 'danger' | 'info' {
  if (gender === 1) return 'primary'
  if (gender === 2) return 'danger'
  return 'info'
}

// ==================== 详情抽屉 ====================
const detailVisible = ref(false)
const detailData = ref<Customer | null>(null)

async function handleDetail(row: Customer) {
  try {
    detailData.value = await getCustomerDetailApi(row.id)
  } catch {
    detailData.value = row
  }
  detailVisible.value = true
}

// ==================== 编辑弹窗 ====================
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const defaultForm = () => ({
  id: undefined as number | undefined,
  username: '',
  nickname: '',
  phone: '',
  email: '',
  gender: 0 as 0 | 1 | 2,
  birthday: '',
  status: 1 as 0 | 1
})

const form = reactive(defaultForm())

const rules: FormRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
}

function handleEdit(row: Customer) {
  Object.assign(form, {
    id: row.id,
    username: row.username,
    nickname: row.nickname,
    phone: row.phone || '',
    email: row.email || '',
    gender: row.gender ?? 0,
    birthday: row.birthday || '',
    status: row.status
  })
  dialogVisible.value = true
}

function resetForm() {
  Object.assign(form, defaultForm())
  formRef.value?.resetFields()
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    await updateCustomerApi(form)
    ElMessage.success('更新成功')
    dialogVisible.value = false
    fetchData()
  } finally {
    submitLoading.value = false
  }
}

// ==================== 状态切换 ====================
async function handleToggleStatus(row: Customer) {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 0 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确认${action}客户"${row.nickname}"吗？`, '提示', { type: 'warning' })
    await updateCustomerStatusApi(row.id, newStatus as 0 | 1)
    ElMessage.success(`已${action}`)
    fetchData()
  } catch { /* 取消 */ }
}

// ==================== 删除 ====================
async function handleDelete(row: Customer) {
  await ElMessageBox.confirm(
    `确认删除客户"${row.nickname}"(${row.username})吗？删除后不可恢复。`,
    '删除确认',
    { type: 'warning', confirmButtonText: '确定删除', cancelButtonText: '取消' }
  )
  await deleteCustomerApi(row.id)
  ElMessage.success('删除成功')
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

.detail-header {
  display: flex;
  align-items: center;
  gap: $spacing-lg;
  padding-bottom: $spacing-md;

  &-info {
    h3 {
      font-size: $font-size-xl;
      margin-bottom: $spacing-xs;
    }

    .detail-username {
      font-size: $font-size-sm;
      color: $text-secondary;
      margin-bottom: $spacing-xs;
    }
  }
}

.drawer-footer {
  margin-top: $spacing-xl;
  text-align: center;
}
</style>
