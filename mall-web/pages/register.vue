<template>
  <div class="register-page">
    <div class="register-container">
      <!-- 左侧装饰 -->
      <div class="register-decoration">
        <div class="decoration-content">
          <el-icon :size="64"><User /></el-icon>
          <h2>加入我们</h2>
          <p>创建您的账户，享受会员专属优惠</p>
        </div>
      </div>

      <!-- 右侧表单 -->
      <div class="register-form-wrapper">
        <div class="form-header">
          <h2>注册</h2>
          <span class="form-switch">
            已有账户？
            <el-link type="primary" @click="navigateTo('/login')">立即登录</el-link>
          </span>
        </div>

        <el-form
          ref="formRef"
          :model="registerForm"
          :rules="registerRules"
          size="large"
          @keyup.enter="handleRegister"
        >
          <el-form-item prop="phone">
            <el-input
              v-model="registerForm.phone"
              placeholder="请输入手机号"
              :prefix-icon="Phone"
            />
          </el-form-item>

          <el-form-item prop="nickname">
            <el-input
              v-model="registerForm.nickname"
              placeholder="请输入昵称"
              :prefix-icon="Edit"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请确认密码"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="handleRegister"
              style="width: 100%"
            >
              注册
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Phone, Lock, Edit, User } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import type { FormInstance, FormRules } from 'element-plus'

definePageMeta({
  layout: false
})

useHead({
  title: '注册'
})

const userStore = useUserStore()
const router = useRouter()

const formRef = ref<FormInstance>()
const loading = ref(false)

const registerForm = reactive({
  phone: '',
  nickname: '',
  password: '',
  confirmPassword: ''
})

// 自定义验证：确认密码
const validateConfirmPassword = (rule: any, value: string, callback: Function) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules: FormRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 注册
const handleRegister = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await userStore.register(
        registerForm.phone,
        registerForm.password,
        registerForm.nickname
      )
      ElMessage.success('注册成功，请登录')
      await router.push('/login')
    } catch (error: any) {
      ElMessage.error(error.message || '注册失败')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style lang="scss" scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  padding: 20px;
}

.register-container {
  display: flex;
  width: 900px;
  max-width: 100%;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

/* 左侧装饰 */
.register-decoration {
  flex: 1;
  background: linear-gradient(135deg, #67c23a 0%, #409eff 100%);
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
.register-form-wrapper {
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
  .register-container {
    flex-direction: column;
  }

  .register-decoration {
    padding: 30px;

    .decoration-content h2 {
      font-size: 24px;
    }
  }

  .register-form-wrapper {
    padding: 30px 20px;
  }
}
</style>
