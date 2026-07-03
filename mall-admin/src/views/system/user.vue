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
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增系统用户</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="username" label="用户名" width="130" />
        <el-table-column prop="nickname" label="昵称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="130" align="center">
          <template #default="{ row }">{{ row.phone || '-' }}</template>
        </el-table-column>
        <el-table-column label="角色" min-width="140">
          <template #default="{ row }">
            <el-tag
              v-for="role in row.roleNames || row.roles"
              :key="role"
              size="small"
              type="warning"
              style="margin-right: 4px"
            >
              {{ role }}
            </el-tag>
            <span v-if="!row.roles?.length" class="text-placeholder">未分配</span>
          </template>
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
        <el-table-column prop="lastLoginTime" label="最后登录" width="170" align="center">
          <template #default="{ row }">
            <span v-if="row.lastLoginTime">{{ row.lastLoginTime }}</span>
            <el-tag v-else type="info" size="small">从未登录</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" align="center" />
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="warning" size="small" :icon="Key" @click="handleAssignRole(row)">角色</el-button>
            <el-button link type="primary" size="small" :icon="Unlock" @click="handleResetPwd(row)">重置密码</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑系统用户' : '新增系统用户'"
      width="520px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" placeholder="请输入昵称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
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

    <!-- 角色分配弹窗 -->
    <el-dialog v-model="roleVisible" title="分配角色" width="450px">
      <el-checkbox-group v-model="selectedRoles">
        <el-checkbox v-for="role in roleList" :key="role.id" :label="role.id">
          {{ role.name }}
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveRole">保存</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码弹窗 -->
    <el-dialog v-model="pwdVisible" title="重置密码" width="400px">
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules">
        <el-form-item label="新密码" prop="password">
          <el-input v-model="pwdForm.password" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdVisible = false">取消</el-button>
        <el-button type="primary" :loading="pwdLoading" @click="handleConfirmResetPwd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import {
  getAdminUserListApi,
  addAdminUserApi,
  updateAdminUserApi,
  deleteAdminUserApi,
  updateAdminUserStatusApi,
  resetAdminUserPasswordApi,
  assignAdminUserRoleApi
} from '@/api/admin-user'
import { getRoleListApi } from '@/api/user'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Key, Unlock } from '@element-plus/icons-vue'
import type { SystemUser } from '@/types'

// ==================== 表格数据 ====================
const loading = ref(false)
const tableData = ref<SystemUser[]>([])
const total = ref(0)

const queryForm = reactive({
  page: 1,
  size: 10,
  keyword: '',
  status: undefined as number | undefined
})

async function fetchData() {
  loading.value = true
  try {
    const data = await getAdminUserListApi(queryForm)
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
  queryForm.keyword = ''
  queryForm.status = undefined
  handleSearch()
}

// ==================== 新增/编辑弹窗 ====================
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const defaultForm = () => ({
  id: undefined as number | undefined,
  username: '',
  password: '',
  nickname: '',
  phone: '',
  email: '',
  status: 1 as 0 | 1
})

const form = reactive(defaultForm())

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [{
    validator: (_rule, _value, callback) => {
      if (!isEdit.value && !form.password) {
        callback(new Error('请输入密码'))
      } else {
        callback()
      }
    },
    trigger: 'blur'
  }]
}

function handleAdd() {
  isEdit.value = false
  dialogVisible.value = true
}

function handleEdit(row: SystemUser) {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    username: row.username,
    password: '',
    nickname: row.nickname,
    phone: row.phone || '',
    email: row.email || '',
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
    if (isEdit.value && form.id) {
      await updateAdminUserApi(form)
      ElMessage.success('更新成功')
    } else {
      await addAdminUserApi(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } finally {
    submitLoading.value = false
  }
}

// ==================== 状态切换 ====================
async function handleToggleStatus(row: SystemUser) {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 0 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确认${action}系统用户"${row.nickname}"吗？`, '提示', { type: 'warning' })
    await updateAdminUserStatusApi(row.id, newStatus as 0 | 1)
    ElMessage.success(`已${action}`)
    fetchData()
  } catch { /* 取消 */ }
}

// ==================== 删除 ====================
async function handleDelete(row: SystemUser) {
  await ElMessageBox.confirm(
    `确认删除系统用户"${row.nickname}"(${row.username})吗？`,
    '删除确认',
    { type: 'warning', confirmButtonText: '确定删除', cancelButtonText: '取消' }
  )
  await deleteAdminUserApi(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

// ==================== 角色分配 ====================
const roleVisible = ref(false)
const roleList = ref<any[]>([])
const selectedRoles = ref<number[]>([])
const editUserId = ref(0)

async function handleAssignRole(row: SystemUser) {
  editUserId.value = row.id
  try {
    roleList.value = await getRoleListApi()
  } catch {
    roleList.value = []
  }
  selectedRoles.value = []
  roleVisible.value = true
}

async function handleSaveRole() {
  await assignAdminUserRoleApi(editUserId.value, selectedRoles.value)
  ElMessage.success('角色分配成功')
  roleVisible.value = false
}

// ==================== 重置密码 ====================
const pwdVisible = ref(false)
const pwdLoading = ref(false)
const pwdFormRef = ref<FormInstance>()
const pwdUserId = ref(0)

const pwdForm = reactive({ password: '', confirmPassword: '' })

const validateConfirmPwd = (_rule: any, value: string, callback: Function) => {
  if (value !== pwdForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const pwdRules: FormRules = {
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPwd, trigger: 'blur' }
  ]
}

function handleResetPwd(row: SystemUser) {
  pwdUserId.value = row.id
  pwdForm.password = ''
  pwdForm.confirmPassword = ''
  pwdVisible.value = true
}

async function handleConfirmResetPwd() {
  const valid = await pwdFormRef.value?.validate().catch(() => false)
  if (!valid) return

  pwdLoading.value = true
  try {
    await resetAdminUserPasswordApi(pwdUserId.value, pwdForm.password)
    ElMessage.success('密码重置成功')
    pwdVisible.value = false
  } finally {
    pwdLoading.value = false
  }
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

.text-placeholder {
  color: $text-placeholder;
  font-size: $font-size-sm;
}
</style>
