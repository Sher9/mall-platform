<template>
  <div class="login-page">
    <div class="login-card">
      <h1 class="title">商城桌面端</h1>
      <div class="form">
        <div class="form-item">
          <label>用户名</label>
          <input v-model="form.username" placeholder="请输入用户名" @keyup.enter="handleLogin" />
        </div>
        <div class="form-item">
          <label>密码</label>
          <input v-model="form.password" type="password" placeholder="请输入密码" @keyup.enter="handleLogin" />
        </div>
        <button class="btn-primary login-btn" :disabled="loading" @click="handleLogin">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

async function handleLogin() {
  if (!form.username || !form.password) {
    alert('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    await userStore.login(form)
    await userStore.fetchUserInfo(form.username)
    router.push('/')
  } catch (e) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

  .login-card {
    width: 380px;
    padding: 40px;
    background: #fff;
    border-radius: $radius-lg;
    box-shadow: $shadow-md;

    .title {
      text-align: center;
      margin-bottom: 32px;
      font-size: 24px;
      color: $text-color;
    }

    .form-item {
      margin-bottom: 20px;

      label {
        display: block;
        margin-bottom: 6px;
        font-size: 14px;
        color: $text-color-secondary;
      }
    }

    .login-btn {
      width: 100%;
      padding: 10px;
      font-size: 16px;
      margin-top: 8px;

      &:disabled {
        opacity: 0.6;
        cursor: not-allowed;
      }
    }
  }
}
</style>
