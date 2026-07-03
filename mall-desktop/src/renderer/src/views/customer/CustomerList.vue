<template>
  <div class="customer-list">
    <div class="toolbar card">
      <div class="search">
        <input v-model="query.name" placeholder="搜索客户名称" @keyup.enter="loadData" />
        <button class="btn-primary" @click="loadData">查询</button>
        <button class="btn-default" @click="resetQuery">重置</button>
      </div>
      <button class="btn-primary" @click="openDialog()">新增客户</button>
    </div>

    <div class="table card">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>昵称</th>
            <th>手机号</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.customerId">
            <td>{{ item.customerId }}</td>
            <td>{{ item.username }}</td>
            <td>{{ item.nickname }}</td>
            <td>{{ item.phone }}</td>
            <td>{{ item.status === 1 ? '正常' : '禁用' }}</td>
            <td>
              <button class="btn-default btn-sm" @click="openDialog(item)">编辑</button>
              <button class="btn-danger btn-sm" @click="handleDelete(item)">删除</button>
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
        <h3>{{ editForm.customerId ? '编辑客户' : '新增客户' }}</h3>
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

<style lang="scss" scoped>
.customer-list {
  .toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .search {
      display: flex;
      gap: 8px;
      input { width: 240px; }
    }
  }

  .table {
    table {
      width: 100%;
      border-collapse: collapse;
      th, td {
        padding: 10px 12px;
        text-align: left;
        border-bottom: 1px solid $border-color;
      }
      th {
        background: $bg-color;
        font-weight: 600;
        color: $text-color-secondary;
      }
      .empty {
        text-align: center;
        color: $text-color-placeholder;
        padding: 32px;
      }
    }
    .pagination {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 12px;
      padding: 16px;
    }
  }

  .btn-sm {
    padding: 4px 12px;
    font-size: 12px;
    margin-right: 4px;
  }

  .modal {
    position: fixed;
    top: 0; left: 0; right: 0; bottom: 0;
    background: rgba(0, 0, 0, 0.45);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 100;

    .modal-content {
      width: 440px;
      padding: 24px;
      h3 { margin-bottom: 20px; }
      .form-item {
        margin-bottom: 16px;
        label {
          display: block;
          margin-bottom: 6px;
          color: $text-color-secondary;
        }
        select {
          width: 100%;
          padding: 8px 12px;
          border: 1px solid $border-color;
          border-radius: $radius-sm;
        }
      }
      .modal-actions {
        display: flex;
        justify-content: flex-end;
        gap: 12px;
        margin-top: 20px;
      }
    }
  }
}
</style>
