<template>
  <div class="page-container">
    <div class="search-bar">
      <el-form :model="queryForm" inline>
        <el-form-item label="角色名称">
          <el-input v-model="queryForm.roleName" placeholder="请输入" clearable style="width: 200px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-bar">
      <div class="table-toolbar">
        <span class="total-tip">共 {{ total }} 条记录</span>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增角色</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="roleName" label="角色名称" width="160" />
        <el-table-column prop="roleCode" label="角色编码" width="160" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" align="center" />
        <el-table-column label="操作" width="260" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" size="small" @click="handlePermission(row)">分配权限</el-button>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="500px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入描述" />
        </el-form-item>
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

    <!-- 分配权限抽屉 -->
    <el-drawer v-model="permissionDrawerVisible" title="分配权限" width="400px">
      <div style="margin-bottom: 16px; font-weight: 600;">菜单权限</div>
      <el-tree
        ref="menuTreeRef"
        :data="menuTree"
        :props="{ children: 'children', label: 'menuName' }"
        show-checkbox
        node-key="menuId"
        default-expand-all
      />
      <template #footer>
        <el-button @click="permissionDrawerVisible = false">取消</el-button>
        <el-button type="primary" :loading="permissionLoading" @click="savePermission">保存</el-button>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, post, put, del } from '@/utils/request'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import type { Role } from '@/api/user'
import { getMenuListApi, getRoleMenusApi, saveRoleMenusApi } from '@/api/menu'
import type { Menu } from '@/types'

const loading = ref(false)
const tableData = ref<Role[]>([])
const total = ref(0)

// 分配权限抽屉
const permissionDrawerVisible = ref(false)
const permissionRoleId = ref<number>()
const menuTree = ref<any[]>([])
const menuTreeRef = ref()
const permissionLoading = ref(false)

const queryForm = reactive({ page: 1, size: 10, roleName: '' })

async function fetchData() {
  loading.value = true
  try {
    const data = await get<{ records: Role[]; total: number }>('/role', {
      pageNum: queryForm.page,
      pageSize: queryForm.size,
      roleName: queryForm.roleName
    })
    tableData.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() { queryForm.page = 1; fetchData() }
function handleReset() { queryForm.roleName = ''; handleSearch() }

// 弹窗
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const defaultForm = () => ({
  id: undefined as number | undefined,
  roleName: '',
  roleCode: '',
  description: '',
  status: 1 as 0 | 1
})
const form = reactive(defaultForm())

const rules: FormRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

function handleAdd() { isEdit.value = false; dialogVisible.value = true }

function handleEdit(row: Role) {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

function resetForm() { Object.assign(form, defaultForm()); formRef.value?.resetFields() }

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value && form.id) {
      await put('/role', form); ElMessage.success('更新成功')
    } else {
      await post('/role', form); ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row: Role) {
  await ElMessageBox.confirm(`确认删除角色"${row.roleName}"吗？`, '提示', { type: 'warning' })
  await del(`/role/${row.id}`)
  ElMessage.success('删除成功')
  fetchData()
}

// 分配权限
async function handlePermission(row: Role) {
  permissionRoleId.value = row.id
  permissionDrawerVisible.value = true
  // 获取菜单树
  const menus = await getMenuListApi()
  menuTree.value = buildMenuTree(menus)
  // 获取已分配的菜单ID
  const checkedIds = await getRoleMenusApi(row.id)
  // 等待 tree 渲染完成后设置选中状态
  setTimeout(() => {
    menuTreeRef.value?.setCheckedKeys(checkedIds)
  }, 100)
}

// 保存权限
async function savePermission() {
  permissionLoading.value = true
  try {
    const checkedKeys = menuTreeRef.value?.getCheckedKeys() || []
    const halfCheckedKeys = menuTreeRef.value?.getHalfCheckedKeys() || []
    const allChecked = [...checkedKeys, ...halfCheckedKeys] as number[]
    await saveRoleMenusApi(permissionRoleId.value!, allChecked)
    ElMessage.success('权限分配成功')
    permissionDrawerVisible.value = false
  } finally {
    permissionLoading.value = false
  }
}

// 将扁平菜单列表转换为树形结构
function buildMenuTree(menus: any[]): any[] {
  const map = new Map<number, any>()
  const roots: any[] = []
  menus.forEach(m => {
    map.set(m.menuId, { ...m, children: [] })
  })
  menus.forEach(m => {
    const node = map.get(m.menuId)!
    if (m.parentId === 0 || !map.has(m.parentId)) {
      roots.push(node)
    } else {
      map.get(m.parentId)!.children.push(node)
    }
  })
  return roots
}

onMounted(() => fetchData())
</script>

<style lang="scss" scoped>
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: $spacing-xl; }
.total-tip { font-size: $font-size-md; color: $text-secondary; }
</style>
