<template>
  <div class="app-wrapper">
    <!-- 侧边栏 -->
    <Sidebar :collapsed="isCollapsed" />

    <!-- 主内容区 -->
    <div class="main-container" :class="{ 'is-collapsed': isCollapsed }">
      <!-- 顶部导航 -->
      <Navbar :collapsed="isCollapsed" @toggle="toggleSidebar" />

      <!-- 标签页 -->
      <TagsView />

      <!-- 页面内容 -->
      <AppMain />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import Sidebar from './components/Sidebar.vue'
import Navbar from './components/Navbar.vue'
import TagsView from './components/TagsView.vue'
import AppMain from './components/AppMain.vue'

const isCollapsed = ref(false)

function toggleSidebar() {
  isCollapsed.value = !isCollapsed.value
}
</script>

<style lang="scss" scoped>
.app-wrapper {
  display: flex;
  width: 100%;
  height: 100%;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 100%;
  margin-left: $sidebar-width;
  transition: margin-left $transition-normal;
  background: $bg-page;

  &.is-collapsed {
    margin-left: $sidebar-collapsed-width;
  }
}
</style>
