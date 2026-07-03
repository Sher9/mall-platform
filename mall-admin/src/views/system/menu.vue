<template>
  <div class="page-container">
    <div class="search-bar">
      <el-form :model="queryForm" inline>
        <el-form-item label="菜单名称">
          <el-input v-model="queryForm.menuName" placeholder="请输入" clearable style="width: 200px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-bar">
      <div class="table-toolbar">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增菜单</el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        border
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="menuName" label="菜单名称" min-width="180" />
        <el-table-column prop="path" label="路径" width="180" show-overflow-tooltip />
        <el-table-column prop="icon" label="图标" width="80" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" align="center" />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" :icon="Plus" @click="handleAddChild(row)">新增子菜单</el-button>
            <el-button link type="primary" size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑菜单' : '新增菜单'" width="540px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="form.parentId"
            :data="menuTreeData"
            :props="{ label: 'menuName', value: 'id', children: 'children' }"
            check-strictly
            placeholder="顶级菜单"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="menuName">
              <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="路径" prop="path">
              <el-input v-model="form.path" placeholder="如 /system" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="图标">
              <el-input v-model="form.icon" placeholder="如 Setting" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number v-model="form.sort" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">显示</el-radio>
            <el-radio :value="0">隐藏</el-radio>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { get, post, put, del } from '@/utils/request'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'

interface Menu {
  menuId?: number
  parentId: number
  menuName: string
  path: string
  icon?: string
  sort: number
  status: 0 | 1
  children?: Menu[]
  createTime: string
}

const loading = ref(false)
const tableData = ref<Menu[]>([])

const queryForm = reactive({ menuName: '' })

async function fetchData() {
  loading.value = true
  try {
    const data = await get<Menu[]>('/menu/all')
    tableData.value = buildTree(data)
  } finally {
    loading.value = false
  }
}

function buildTree(list: Menu[]): Menu[] {
  const map = new Map<number, Menu>()
  const roots: Menu[] = []
  list.forEach((item) => map.set(item.menuId!, { ...item, children: [] }))
  list.forEach((item) => {
    const node = map.get(item.menuId!)!
    if (item.parentId === 0 || !map.has(item.parentId)) {
      roots.push(node)
    } else {
      map.get(item.parentId)?.children?.push(node)
    }
  })
  return roots
}

function handleSearch() { fetchData() }
function handleReset() { queryForm.menuName = ''; fetchData() }

// 弹窗
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const defaultForm = () => ({
  menuId: undefined as number | undefined,
  parentId: 0,
  menuName: '',
  path: '',
  icon: '',
  sort: 0,
  status: 1 as 0 | 1
})
const form = ref<Menu>(defaultForm())

const rules: FormRules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  path: [{ required: true, message: '请输入路径', trigger: 'blur' }]
}

const menuTreeData = computed(() => {
  const top = { menuId: 0, menuName: '顶级菜单', children: tableData.value }
  return [top]
})

function handleAdd() {
  isEdit.value = false
  dialogVisible.value = true
}

function handleAddChild(row: Menu) {
  isEdit.value = false
  form.value.parentId = row.menuId || 0
  dialogVisible.value = true
}

function handleEdit(row: Menu) {
  isEdit.value = true
  Object.assign(form.value, {
    menuId: row.menuId, parentId: row.parentId, menuName: row.menuName,
    path: row.path, icon: row.icon || '', sort: row.sort, status: row.status
  })
  dialogVisible.value = true
}

function resetForm() { Object.assign(form.value, defaultForm()); formRef.value?.resetFields() }

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value && form.value.menuId) {
      await put('/menu', form.value); ElMessage.success('更新成功')
    } else {
      await post('/menu', form.value); ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row: Menu) {
  await ElMessageBox.confirm(`确认删除菜单"${row.menuName}"吗？`, '提示', { type: 'warning' })
  await del(`/menu/${row.menuId}`)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(() => fetchData())
</script>

<style lang="scss" scoped>
.total-tip { font-size: $font-size-md; color: $text-secondary; }
</style>
