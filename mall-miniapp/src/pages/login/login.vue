<!-- 登录页 -->
<template>
  <view class="page-login">
    <!-- 背景装饰 -->
    <view class="bg-circle circle-1"></view>
    <view class="bg-circle circle-2"></view>
    <view class="bg-circle circle-3"></view>

    <!-- 顶部 Logo 区域 -->
    <view class="login-header">
      <view class="logo-wrap">
        <text class="logo-icon">🛒</text>
      </view>
      <text class="app-name">臻品商城</text>
      <text class="app-slogan">发现好物，畅享生活</text>
    </view>

    <!-- 登录表单 -->
    <view class="login-form">
      <view class="form-title">欢迎回来</view>

      <view class="input-group">
        <view class="input-wrap" :class="{ focus: usernameFocus }">
          <text class="input-icon">👤</text>
          <input
            v-model="form.username"
            class="input-field"
            placeholder="请输入用户名"
            placeholder-class="placeholder"
            @focus="usernameFocus = true"
            @blur="usernameFocus = false"
          />
        </view>

        <view class="input-wrap" :class="{ focus: passwordFocus }">
          <text class="input-icon">🔒</text>
          <input
            v-model="form.password"
            class="input-field"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请输入密码"
            placeholder-class="placeholder"
            @focus="passwordFocus = true"
            @blur="passwordFocus = false"
          />
          <text class="toggle-pwd" @click="showPassword = !showPassword">
            {{ showPassword ? '🙈' : '👁️' }}
          </text>
        </view>
      </view>

      <view class="form-extra">
        <text class="forgot-pwd" @click="goForgotPwd">忘记密码？</text>
      </view>

      <button class="login-btn" :loading="loading" @click="handleLogin">
        <text class="btn-text">{{ loading ? '登录中...' : '登  录' }}</text>
      </button>

      <view class="divider-wrap">
        <view class="divider-line"></view>
        <text class="divider-text">其他方式</text>
        <view class="divider-line"></view>
      </view>

      <view class="other-login">
        <view class="other-btn wechat" @click="handleWechatLogin">
          <text class="other-icon">💬</text>
          <text class="other-label">微信登录</text>
        </view>
        <view class="other-btn phone" @click="goPhoneLogin">
          <text class="other-icon">📱</text>
          <text class="other-label">手机号登录</text>
        </view>
      </view>

      <view class="register-wrap">
        <text class="register-text">还没有账号？</text>
        <text class="register-link" @click="goRegister">立即注册</text>
      </view>
    </view>

    <!-- 底部协议 -->
    <view class="agreement">
      <text class="agreement-text">登录即代表同意</text>
      <text class="agreement-link">《用户协议》</text>
      <text class="agreement-text">和</text>
      <text class="agreement-link">《隐私政策》</text>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useUserStore } from '@/store/user'
import { showToast } from '@/utils'

const userStore = useUserStore()
const loading = ref(false)
const showPassword = ref(false)
const usernameFocus = ref(false)
const passwordFocus = ref(false)

const form = reactive({
  username: '',
  password: ''
})

async function handleLogin() {
  if (!form.username.trim()) return showToast('请输入用户名')
  if (!form.password.trim()) return showToast('请输入密码')

  loading.value = true
  try {
    await userStore.loginAction({ username: form.username, password: form.password })
    showToast('登录成功', 'success')
    setTimeout(() => uni.switchTab({ url: '/pages/index/index' }), 800)
  } catch (e) {
    showToast(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}

function handleWechatLogin() {
  showToast('微信登录开发中')
}

function goPhoneLogin() {
  showToast('手机号登录开发中')
}

function goForgotPwd() {
  showToast('忘记密码开发中')
}

function goRegister() {
  showToast('注册功能开发中')
}
</script>

<style lang="scss" scoped>
.page-login {
  min-height: 100vh;
  background: linear-gradient(160deg, #1989FA 0%, #4facfe 40%, #e8f4fd 100%);
  position: relative;
  overflow: hidden;
}

// 背景装饰圆
.bg-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
  background: #fff;
}
.circle-1 { width: 400rpx; height: 400rpx; top: -100rpx; right: -80rpx; }
.circle-2 { width: 300rpx; height: 300rpx; top: 200rpx; left: -120rpx; }
.circle-3 { width: 200rpx; height: 200rpx; bottom: 300rpx; right: -60rpx; opacity: 0.06; }

// 头部
.login-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 180rpx;
  position: relative;
  z-index: 1;
  .logo-wrap {
    width: 120rpx;
    height: 120rpx;
    background: rgba(255,255,255,0.25);
    border-radius: 30rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    backdrop-filter: blur(10px);
    .logo-icon { font-size: 60rpx; }
  }
  .app-name {
    margin-top: 24rpx;
    font-size: 40rpx;
    font-weight: 800;
    color: #fff;
    letter-spacing: 4rpx;
  }
  .app-slogan {
    margin-top: 10rpx;
    font-size: 24rpx;
    color: rgba(255,255,255,0.85);
    letter-spacing: 2rpx;
  }
}

// 表单卡片
.login-form {
  margin: 60rpx 40rpx 0;
  background: #fff;
  border-radius: 24rpx;
  padding: 48rpx 36rpx;
  box-shadow: 0 8rpx 40rpx rgba(0,0,0,0.12);
  position: relative;
  z-index: 1;
  .form-title {
    font-size: 36rpx;
    font-weight: 700;
    color: #323233;
    margin-bottom: 36rpx;
  }
}

// 输入框
.input-group {
  .input-wrap {
    display: flex;
    align-items: center;
    background: #f7f8fa;
    border-radius: 14rpx;
    padding: 24rpx 20rpx;
    margin-bottom: 20rpx;
    border: 2rpx solid transparent;
    transition: all 0.3s;
    &.focus {
      border-color: #1989FA;
      background: #ecf5ff;
    }
    .input-icon { font-size: 32rpx; margin-right: 16rpx; flex-shrink: 0; }
    .input-field {
      flex: 1;
      font-size: 28rpx;
      color: #323233;
    }
    .placeholder { color: #c8c9cc; }
    .toggle-pwd {
      font-size: 28rpx;
      padding: 8rpx;
      flex-shrink: 0;
    }
  }
}

// 忘记密码
.form-extra {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 28rpx;
  .forgot-pwd {
    font-size: 24rpx;
    color: #1989FA;
  }
}

// 登录按钮
.login-btn {
  width: 100% !important;
  height: 88rpx;
  background: linear-gradient(135deg, #1989FA, #4facfe) !important;
  border-radius: 14rpx !important;
  border: none !important;
  display: flex;
  align-items: center;
  justify-content: center;
  .btn-text {
    font-size: 32rpx;
    font-weight: 600;
    color: #fff;
    letter-spacing: 8rpx;
  }
}

// 分割线
.divider-wrap {
  display: flex;
  align-items: center;
  margin: 36rpx 0;
  .divider-line { flex: 1; height: 1rpx; background: #ebedf0; }
  .divider-text {
    margin: 0 16rpx;
    font-size: 22rpx;
    color: #c8c9cc;
  }
}

// 其他登录方式
.other-login {
  display: flex;
  justify-content: center;
  gap: 40rpx;
  .other-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    .other-icon { font-size: 44rpx; margin-bottom: 8rpx; }
    .other-label { font-size: 22rpx; color: #646566; }
  }
}

// 注册
.register-wrap {
  margin-top: 36rpx;
  text-align: center;
  .register-text { font-size: 24rpx; color: #969799; }
  .register-link {
    font-size: 24rpx;
    color: #1989FA;
    font-weight: 600;
    margin-left: 4rpx;
  }
}

// 底部协议
.agreement {
  position: absolute;
  bottom: 60rpx;
  left: 0;
  right: 0;
  text-align: center;
  .agreement-text { font-size: 20rpx; color: rgba(255,255,255,0.7); }
  .agreement-link { font-size: 20rpx; color: #fff; font-weight: 600; }
}
</style>
