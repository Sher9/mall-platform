<template>
  <div class="customer-list">
    <div class="page-head">
      <div>
        <h1 class="page-title">客户管理</h1>
        <p class="page-desc">查看与维护商城注册客户信息。</p>
      </div>
      <button class="btn-primary" @click="openDialog()">新增客户</button>
    </div>

    <div class="card">
      <div class="toolbar">
        <div class="search">
          <input v-model="query.name" placeholder="搜索客户名称" @keyup.enter="loadData" />
          <button class="btn-primary" @click="loadData">查询</button>
          <button class="btn-default" @click="resetQuery">重置</button>
        </div>
      </div>

      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>昵称</th>
            <th>手机号</th>
            <th>状态</th>
            <th class="col-action">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.customerId">
            <td>{{ item.customerId }}</td>
            <td>{{ item.username }}</td>
            <td>{{ item.nickname }}</td>
            <td>{{ item.phone }}</td>
            <td>
              <span :class="['status-tag', item.status === 1 ? 'on' : 'off']">
                {{ item.status === 1 ? '正常' : '禁用' }}
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

    <div v-if="dialogVisible" class="modal" @click.self="dialogVisible = false">
      <div class="modal-content card">
        <h3 class="modal-title">{{ editForm.customerId ? '编辑客户' : '新增客户' }}</h3>
        <div class="form-item">
          <label>用户名</label>
          <input v-model="editForm.username" placeholder="请输入用户名" />
        </div>
        <div class="form-item">
          <label>昵称</label>
          <input v-model="editForm.nickname" placeholder="请输入昵称" />
        </div>
        <div class="form-item">
          <label>手机号</label>
          <input v-model="editForm.phone" placeholder="请输入手机号" />
        </div>
        <div class="form-item">
          <label>状态</label>
          <select v-model="editForm.status">
            <option :value="1">正常</option>
            <option :value="0">禁用</option>
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
import { getCustomerList, createCustomer, updateCustomer, deleteCustomer } from '@/api/customer'

const list = ref([])
const dialogVisible = ref(false)

const query = reactive({ name: '', pageNum: 1, pageSize: 10 })
const editForm = reactive({ customerId: null, username: '', nickname: '', phone: '', status: 1 })

async function loadData() {
  const res = await getCustomerList(query)
  list.value = res.data?.records || res.data || []
}

function resetQuery() {
  query.name = ''
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
    Object.assign(editForm, { customerId: null, username: '', nickname: '', phone: '', status: 1 })
  }
  dialogVisible.value = true
}

async function handleSave() {
  if (editForm.customerId) {
    await updateCustomer(editForm.customerId, editForm)
  } else {
    await createCustomer(editForm)
  }
  dialogVisible.value = false
  loadData()
}

async function handleDelete(item) {
  if (confirm(`确认删除客户「${item.username}」？`)) {
    await deleteCustomer(item.customerId)
    loadData()
  }
}

onMounted(loadData)
</script>
