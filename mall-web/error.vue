<template>
  <div class="error-page">
    <div class="error-content">
      <h1 class="error-code">{{ error?.statusCode || '404' }}</h1>
      <p class="error-message">{{ error?.statusMessage || '页面不存在' }}</p>
      <el-button type="primary" size="large" @click="handleBack">
        返回首页
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  error: any
}>()

const handleBack = () => {
  clearError()
  navigateTo('/')
}
</script>

<style lang="scss" scoped>
.error-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.error-content {
  text-align: center;
  color: #fff;
  padding: 40px;

  .error-code {
    font-size: 120px;
    font-weight: 700;
    line-height: 1;
    margin-bottom: 16px;
    text-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  }

  .error-message {
    font-size: 20px;
    margin-bottom: 40px;
    opacity: 0.9;
  }
}
</style>
