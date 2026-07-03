<template>
  <div class="login-page">
    <div class="login-container">
      <!-- 左侧装饰 -->
      <div class="login-decoration">
        <div class="decoration-content">
          <el-icon :size="64"><ShoppingCartFull /></el-icon>
          <h2>欢迎回来</h2>
          <p>登录您的账户，开启购物之旅</p>
        </div>
      </div>

      <!-- 右侧表单 -->
      <div class="login-form-wrapper">
        <div class="form-header">
          <h2>登录</h2>
          <span class="form-switch">
            还没有账户？
            <el-link type="primary" @click="navigateTo('/register')">立即注册</el-link>
          </span>
        </div>

        <el-form
          ref="formRef"
          :model="loginForm"
          :rules="loginRules"
          size="large"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="phone">
            <el-input
              v-model="loginForm.phone"
              placeholder="请输入手机号"
              :prefix-icon="Phone"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="handleLogin"
              style="width: 100%"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Phone, Lock, ShoppingCartFull } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import type { FormInstance, FormRules } from 'element-plus'

definePageMeta({
  layout: false
})

useHead({
  title: '登录'
})

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()

const formRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive({
  phone: '',
  password: ''
})

const loginRules: FormRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
}

// 登录
const handleLogin = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await userStore.login(loginForm.phone, loginForm.password)
      ElMessage.success('登录成功')

      // 跳转到来源页或首页
      const redirect = route.query.redirect as string
      await router.push(redirect || '/')
    } catch (error: any) {
      ElMessage.error(error.message || '登录失败')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-container {
  display: flex;
  width: 900px;
  max-width: 100%;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

/* 左侧装饰 */
.login-decoration {
  flex: 1;
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  padding: 40px;

  .decoration-content {
    text-align: center;

    h2 {
      font-size: 32px;
      font-weight: 700;
      margin: 20px 0 12px;
    }

    p {
      font-size: 16px;
      opacity: 0.9;
    }
  }
}

/* 右侧表单 */
.login-form-wrapper {
  flex: 1;
  padding: 60px 50px;
}

.form-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 40px;

  h2 {
    font-size: 28px;
    font-weight: 600;
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }

  .login-decoration {
    padding: 30px;

    .decoration-content h2 {
      font-size: 24px;
    }
  }

  .login-form-wrapper {
    padding: 30px 20px;
  }
}
</style>
