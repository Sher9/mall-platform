<template>
  <div>
    <!-- 全局布局 -->
    <NuxtLoadingIndicator color="#409eff" />
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
    <ClientOnly>
      <el-backtop :right="40" :bottom="40" />
    </ClientOnly>
  </div>
</template>

<script setup lang="ts">
// 全局中间件
definePageMeta({
  middleware: []
})

// 设置全局 SEO
useHead({
  titleTemplate: (title) => {
    return title ? `${title} - 商城` : '商城 - 在线购物平台'
  }
})
</script>

<style>
/* 全局样式已在 assets/styles/main.scss 中定义 */
</style>
