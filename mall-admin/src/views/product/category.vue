<template>
  <div class="category-container">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="分类名称">
          <el-input v-model="searchForm.categoryName" placeholder="请输入分类名称" clearable />
        </el-form-item>
        <el-form-item label="父分类">
          <el-select v-model="searchForm.parentId" placeholder="请选择父分类" clearable>
            <el-option label="一级分类" :value="0" />
            <el-option v-for="item in parentCategories" :key="item.categoryId" :label="item.categoryName" :value="item.categoryId" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card">
      <div class="table-header">
        <el-button type="primary" @click="handleAdd">新增分类</el-button>
      </div>
      
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="categoryId" label="ID" width="80" />
        <el-table-column prop="categoryName" label="分类名称" width="200" />
        <el-table-column prop="parentId" label="父分类" width="150">
          <template #default="{ row }">
            {{ row.parentId === 0 ? '一级分类' : getParentName(row.parentId) }}
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">
              {{ row.status === 0 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button 
              :type="row.status === 0 ? 'danger' : 'success'" 
              size="small" 
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 0 ? '停用' : '启用' }}
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="父分类" prop="parentId">
          <el-select v-model="form.parentId" placeholder="请选择父分类" style="width: 100%">
            <el-option label="一级分类" :value="0" />
            <el-option v-for="item in parentCategories" :key="item.categoryId" :label="item.categoryName" :value="item.categoryId" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCategoryPageApi, getCategoryAllApi ,updateCategoryStatusApi,deleteCategoryApi,updateCategoryApi,addCategoryApi} from '@/api/category'

const searchForm = reactive({
  categoryName: '',
  parentId: null as number | null
})

const tableData = ref<Category[]>([])
const parentCategories = ref<Category[]>([])

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const isEdit = ref(false)

const form = reactive<Category>({
  categoryName: '',
  parentId: 0,
  sortOrder: 0,
  status: 0
})

const rules = {
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const getParentName = (parentId: number) => {
  const parent = parentCategories.value.find(item => item.categoryId === parentId)
  return parent ? parent.categoryName : '-'
}

const loadData = async () => {
  try {
    const data = await getCategoryPageApi({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      categoryName: searchForm.categoryName || undefined,
      parentId: searchForm.parentId || undefined
    })
    tableData.value = data.records
    pagination.total = data.total
    
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const loadParentCategories = async () => {
  try {
    const data = await getCategoryAllApi()
    parentCategories.value = data
  } catch (error) {
    console.error('加载分类失败', error)
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const resetSearch = () => {
  searchForm.categoryName = ''
  searchForm.parentId = null
  pagination.pageNum = 1
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增分类'
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row: Category) => {
  dialogTitle.value = '编辑分类'
  isEdit.value = true
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

const handleToggleStatus = async (row: Category) => {
  const newStatus = row.status === 0 ? 1 : 0
  const action = newStatus === 0 ? '启用' : '停用'
  try {
    await ElMessageBox.confirm(`确定要${action}该分类吗？`, '提示', { type: 'warning' })
    await updateCategoryStatusApi(row.categoryId!, newStatus)
    ElMessage.success(`${action}成功`)
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const handleDelete = async (row: Category) => {
  try {
    await ElMessageBox.confirm('确定要删除该分类吗？', '提示', { type: 'warning' })
    await deleteCategoryApi(row.categoryId!)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        if (isEdit.value) {
          await updateCategoryApi(form)
          ElMessage.success('更新成功')
        } else {
          await addCategoryApi(form)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

const resetForm = () => {
  Object.assign(form, {
    categoryName: '',
    parentId: 0,
    sortOrder: 0,
    status: 0
  })
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

const handleSizeChange = (val: number) => {
  pagination.pageSize = val
  loadData()
}

const handleCurrentChange = (val: number) => {
  pagination.pageNum = val
  loadData()
}

onMounted(() => {
  loadData()
  loadParentCategories()
})
</script>

<style scoped>
.category-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-header {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
