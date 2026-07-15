<template>
  <div class="login-page">
    <div class="login-card">
      <div class="brand">
        <div class="logo">商城</div>
        <h1 class="title">商城桌面端管理系统</h1>
        <p class="subtitle">Mall Desktop Admin Console</p>
      </div>

      <form class="form" @submit.prevent="handleLogin">
        <div class="form-item">
          <label>用户名</label>
          <input
            v-model="form.username"
            placeholder="请输入用户名"
            autocomplete="username"
            @keyup.enter="handleLogin"
          />
        </div>
        <div class="form-item">
          <label>密码</label>
          <input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            autocomplete="current-password"
            @keyup.enter="handleLogin"
          />
        </div>
        <button class="btn-primary login-btn" type="submit" :disabled="loading">
          {{ loading ? '登录中...' : '登 录' }}
        </button>
      </form>
    </div>

    <div class="footer-tip">mall-platform · 企业级商城管理桌面端</div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
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
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (e) {
    // 错误已在拦截器统一处理
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: $bg-color;
  background-image: radial-gradient(1200px 600px at 50% -10%, #e8f0ff 0%, $bg-color 60%);

  .login-card {
    width: 380px;
    padding: 40px 36px 32px;
    background: $bg-color-light;
    border: 1px solid $border-color;
    border-radius: $radius-lg;
    box-shadow: $shadow-md;
  }

  .brand {
    text-align: center;
    margin-bottom: 32px;

    .logo {
      width: 52px;
      height: 52px;
      margin: 0 auto 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: $primary-color;
      color: #fff;
      font-size: 16px;
      font-weight: 600;
      border-radius: $radius-md;
      letter-spacing: 1px;
    }

    .title {
      font-size: 20px;
      font-weight: 600;
      color: $text-color;
    }

    .subtitle {
      margin-top: 6px;
      font-size: 12px;
      color: $text-color-placeholder;
      letter-spacing: 0.5px;
    }
  }

  .form-item {
    margin-bottom: 18px;

    label {
      display: block;
      margin-bottom: 8px;
      font-size: 13px;
      color: $text-color-secondary;
    }

    input {
      height: 40px;
    }
  }

  .login-btn {
    width: 100%;
    height: 40px;
    font-size: 15px;
    letter-spacing: 2px;
    margin-top: 4px;

    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  }

  .footer-tip {
    margin-top: 24px;
    font-size: 12px;
    color: $text-color-placeholder;
  }
}
</style>
