<template>
  <div class="product-list">
    <div class="page-head">
      <div>
        <h1 class="page-title">商品管理</h1>
        <p class="page-desc">维护商城在售商品信息、价格与库存。</p>
      </div>
      <button class="btn-primary" @click="openDialog()">新增商品</button>
    </div>

    <div class="card">
      <div class="toolbar">
        <div class="search">
          <input v-model="query.productName" placeholder="搜索商品名称" @keyup.enter="loadData" />
          <button class="btn-primary" @click="loadData">查询</button>
          <button class="btn-default" @click="resetQuery">重置</button>
        </div>
      </div>

      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>商品名称</th>
            <th>价格</th>
            <th>库存</th>
            <th>状态</th>
            <th class="col-action">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.productId">
            <td>{{ item.productId }}</td>
            <td>{{ item.productName }}</td>
            <td>¥{{ item.price }}</td>
            <td>{{ item.stock }}</td>
            <td>
              <span :class="['status-tag', item.status === 1 ? 'on' : 'off']">
                {{ item.status === 1 ? '上架' : '下架' }}
              </span>
            </td>
            <td class="col-action">
              <button class="btn-link" @click="openDialog(item)">编辑</button>
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

    <!-- 新增/编辑弹窗 -->
    <div v-if="dialogVisible" class="modal" @click.self="dialogVisible = false">
      <div class="modal-content card">
        <h3 class="modal-title">{{ editForm.productId ? '编辑商品' : '新增商品' }}</h3>
        <div class="form-item">
          <label>商品名称</label>
          <input v-model="editForm.productName" placeholder="请输入商品名称" />
        </div>
        <div class="form-item">
          <label>价格</label>
          <input v-model.number="editForm.price" type="number" placeholder="请输入价格" />
        </div>
        <div class="form-item">
          <label>库存</label>
          <input v-model.number="editForm.stock" type="number" placeholder="请输入库存" />
        </div>
        <div class="form-item">
          <label>状态</label>
          <select v-model="editForm.status">
            <option :value="1">上架</option>
            <option :value="0">下架</option>
          </select>
        </div>
        <div class="modal-actions">
          <button class="btn-default" @click="dialogVisible = false">取消</button>
          <button class="btn-primary" @click="handleSave">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { getProductList, createProduct, updateProduct, deleteProduct } from '@/api/product'

const list = ref([])
const dialogVisible = ref(false)

const query = reactive({
  productName: '',
  pageNum: 1,
  pageSize: 10
})

const editForm = reactive({
  productId: null,
  productName: '',
  price: 0,
  stock: 0,
  status: 1
})

async function loadData() {
  const res = await getProductList(query)
  list.value = res.data?.records || res.data || []
}

function resetQuery() {
  query.productName = ''
  query.pageNum = 1
  loadData()
}

function changePage(delta) {
  query.pageNum += delta
  loadData()
}

function openDialog(item) {
  if (item) {
    Object.assign(editForm, item)
  } else {
    Object.assign(editForm, { productId: null, productName: '', price: 0, stock: 0, status: 1 })
  }
  dialogVisible.value = true
}

async function handleSave() {
  if (editForm.productId) {
    await updateProduct(editForm.productId, editForm)
  } else {
    await createProduct(editForm)
  }
  dialogVisible.value = false
  loadData()
}

async function handleDelete(item) {
  if (confirm(`确认删除商品「${item.productName}」？`)) {
    await deleteProduct(item.productId)
    loadData()
  }
}

onMounted(loadData)
</script>
