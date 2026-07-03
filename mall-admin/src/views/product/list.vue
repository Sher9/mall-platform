<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :model="queryForm" inline>
        <el-form-item label="商品名称">
          <el-input v-model="queryForm.productName" placeholder="请输入" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="上架" :value="0" />
            <el-option label="下架" :value="1" />
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
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增商品</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="productId" label="ID" width="70" align="center" />
        <el-table-column prop="productNo" label="商品编号" width="120" />
        <el-table-column prop="productName" label="商品名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="brand" label="品牌" width="100" />
        <el-table-column prop="price" label="价格" width="100" align="center">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="sales" label="销量" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'info'" size="small">
              {{ row.status === 0 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" align="center" />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="queryForm.pageNum"
          v-model:page-size="queryForm.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑商品' : '新增商品'"
      width="600px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="商品编号" prop="productNo" v-show="isEdit">
          <el-input v-model="form.productNo" placeholder="请输入商品编号" disabled/>
        </el-form-item>
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="item in categories"
              :key="item.categoryId"
              :label="item.categoryName"
              :value="item.categoryName"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="品牌" prop="brand">
          <el-input v-model="form.brand" placeholder="请输入品牌" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="请输入单位" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存" prop="stockCount">
          <el-input-number v-model="form.stockCount" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="商品图片" prop="imageUrl">
          <el-upload
            class="upload-demo"
            :action="uploadUrl"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
            :show-file-list="false"
            accept="image/*"
            :on-preview="handlePreview"
          >
            <el-button type="primary">点击上传</el-button>
            <template #tip>
              <div class="el-upload__tip">
                只能上传图片文件，且不超过2MB
              </div>
            </template>
          </el-upload>
          <div v-if="form.imageUrl" class="image-preview">
            <img :src="form.imageUrl" alt="商品图片" style="max-width: 200px; max-height: 200px;" />
            <el-button type="primary" size="small" @click="handlePreview">预览</el-button>
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">上架</el-radio>
            <el-radio :label="1">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入商品描述" />
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
import { getProductListApi, addProductApi, updateProductApi, deleteProductApi } from '@/api/product'
import { getCategoryAllApi } from '@/api/category'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import type { Product } from '@/types'

// 分类数据
const categories = ref<Category[]>([])

// 加载分类数据
const loadCategories = async () => {
  try {
    const data = await getCategoryAllApi()
    categories.value = data||[]
  } catch (error) {
    console.error('加载分类数据失败', error)
  }
}

const loading = ref(false)
const tableData = ref<Product[]>([])
const total = ref(0)

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  productName: '',
  status: undefined as number | undefined
})

const uploadUrl = `${import.meta.env.VITE_API_BASE_URL}/upload/single`

async function fetchData() {
  loading.value = true
  try {
    const data = await getProductListApi(queryForm)
    tableData.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryForm.pageNum = 1
  fetchData()
}

function handleReset() {
  queryForm.productName = ''
  queryForm.status = undefined
  handleSearch()
}

function handleSizeChange(val: number) {
  queryForm.pageSize = val
  fetchData()
}

function handleCurrentChange(val: number) {
  queryForm.pageNum = val
  fetchData()
}

// 弹窗
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const defaultForm = () => ({
  productId: undefined as number | undefined,
  productNo: '',
  productName: '',
  category: '',
  brand: '',
  price: 0,
  unit: '件',
  stockCount: 0,
  imageUrl: '',
  status: 0 as number,
  description: '',
  sales: 0
})

const form = reactive<Partial<Product>>(defaultForm())

const rules: FormRules = {
  productName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  brand: [{ required: true, message: '请输入品牌', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stockCount: [{ required: true, message: '请输入库存数量', trigger: 'blur' }]
}

function handleAdd() {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: Product) {
  isEdit.value = true
  Object.assign(form, {
    productId: row.productId,
    productNo: row.productNo,
    productName: row.productName,
    category: row.category,
    brand: row.brand,
    price: row.price,
    unit: row.unit,
    stockCount: row.stockCount,
    imageUrl: row.imageUrl,
    status: row.status,
    description: row.description
  })
  dialogVisible.value = true
}

function resetForm() {
  Object.assign(form, defaultForm())
  formRef.value?.resetFields()
}

function beforeUpload(file: File) {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

function handleUploadSuccess(response: any) {
  if (response.code === 200) {
    form.imageUrl = response.data.url
    ElMessage.success('上传成功')
  } else {
    ElMessage.error('上传失败')
  }
}

// 图片预览
function handlePreview() {
  if (form.imageUrl) {
    window.open(form.imageUrl, '_blank')
  }
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value && form.productId) {
      await updateProductApi(form)
      ElMessage.success('更新成功')
    } else {
      // 新增时，先确保图片已上传
      if (!form.imageUrl) {
        ElMessage.warning('请先上传商品图片')
        submitLoading.value = false
        return
      }
      // sales默认为0
      form.sales = 0
      await addProductApi(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row: Product) {
  await ElMessageBox.confirm(`确认删除"${row.productName}"吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })
  await deleteProductApi(row.productId!)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(() => {
  fetchData()
  loadCategories()
})
</script>

<style lang="scss" scoped>
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.image-preview {
  margin-top: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
  display: inline-block;
}
</style>
