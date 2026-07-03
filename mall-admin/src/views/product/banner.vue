<template>
  <div class="banner-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>轮播图管理</span>
          <el-button type="primary" @click="handleAdd">新增轮播图</el-button>
        </div>
      </template>

      <!-- 轮播图列表 -->
      <el-table :data="bannerList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" width="200" />
        <el-table-column label="图片" width="200">
          <template #default="{ row }">
            <el-image
              style="width: 150px; height: 80px"
              :src="row.imageUrl"
              :preview-src-list="[row.imageUrl]"
              fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column prop="linkUrl" label="跳转链接" width="200" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'info'">
              {{ row.isActive ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button
              :type="row.isActive ? 'warning' : 'success'"
              size="small"
              @click="handleStatus(row)"
            >
              {{ row.isActive ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="图片URL" prop="imageUrl">
          <el-input v-model="form.imageUrl" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="跳转链接" prop="linkUrl">
          <el-input v-model="form.linkUrl" placeholder="请输入跳转链接" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="isActive">
          <el-switch v-model="form.isActive" />
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getBannerListApi,
  addBannerApi,
  updateBannerApi,
  deleteBannerApi,
  updateBannerStatusApi
} from '@/api/banner'

interface Banner {
  id?: number
  title: string
  imageUrl: string
  linkUrl: string
  sort: number
  isActive: boolean
}

const bannerList = ref<Banner[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增轮播图')
const isEdit = ref(false)
const formRef = ref()

const form = ref<Banner>({
  title: '',
  imageUrl: '',
  linkUrl: '',
  sort: 0,
  isActive: true
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  imageUrl: [{ required: true, message: '请输入图片URL', trigger: 'blur' }]
}

// 获取轮播图列表
const getList = async () => {
  try {
    const data = await getBannerListApi()
    //对数据进行处理，把文件服务器地址拼接上
    const fileServerUrl = import.meta.env.VITE_FILE_SERVER_URL
    data.forEach((item: Banner) => {
      item.imageUrl = fileServerUrl + item.imageUrl
    })
    bannerList.value = data || []
    console.log(bannerList)
  } catch (error) {
    console.error('获取轮播图列表失败', error)
  }
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增轮播图'
  isEdit.value = false
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: Banner) => {
  dialogTitle.value = '编辑轮播图'
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

// 删除
const handleDelete = (row: Banner) => {
  ElMessageBox.confirm('确定删除该轮播图吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteBannerApi(row.id!)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败', error)
    }
  })
}

// 启用/禁用
const handleStatus = (row: Banner) => {
  const action = row.isActive ? '禁用' : '启用'
  ElMessageBox.confirm(`确定${action}该轮播图吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await updateBannerStatusApi(row.id!, !row.isActive)
      ElMessage.success(`${action}成功`)
      getList()
    } catch (error) {
      console.error(`${action}失败`, error)
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    if (isEdit.value) {
      await updateBannerApi(form.value)
      ElMessage.success('更新成功')
    } else {
      await addBannerApi(form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('提交失败', error)
  }
}

// 重置表单
const resetForm = () => {
  form.value = {
    title: '',
    imageUrl: '',
    linkUrl: '',
    sort: 0,
    isActive: true
  }
  formRef.value?.resetFields()
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.banner-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
