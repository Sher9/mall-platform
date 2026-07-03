<template>
  <div class="sidebar-container" :class="{ 'is-collapsed': collapsed }">
    <!-- Logo -->
    <div class="sidebar-logo">
      <el-icon :size="24" color="#fff"><Shop /></el-icon>
      <span v-show="!collapsed" class="logo-title">商城管理后台</span>
    </div>

    <!-- 菜单 -->
    <el-scrollbar class="sidebar-scroll">
      <el-menu
        :default-active="activeMenu"
        :collapse="collapsed"
        :collapse-transition="false"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        router
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <!-- 单级菜单 -->
          <el-menu-item
            v-if="route.children?.length === 1 && !route.children[0].children"
            :index="resolvePath(route.path, route.children[0].path)"
          >
            <el-icon><component :is="route.children[0].meta?.icon || route.meta?.icon" /></el-icon>
            <template #title>{{ route.children[0].meta?.title }}</template>
          </el-menu-item>

          <!-- 多级菜单 -->
          <el-sub-menu v-else :index="route.path">
            <template #title>
              <el-icon><component :is="route.meta?.icon" /></el-icon>
              <span>{{ route.meta?.title }}</span>
            </template>
            <el-menu-item
              v-for="child in route.children"
              :key="child.path"
              :index="resolvePath(route.path, child.path)"
            >
              <el-icon><component :is="child.meta?.icon" /></el-icon>
              <template #title>{{ child.meta?.title }}</template>
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { constantRoutes } from '@/router'

defineProps<{ collapsed: boolean }>()

const route = useRoute()

const activeMenu = computed(() => route.path)

const menuRoutes = computed(() =>
  constantRoutes.filter(
    (r) => !r.meta?.hidden && r.path !== '/login'
  )
)

function resolvePath(base: string, child: string): string {
  if (child.startsWith('/')) return child
  return `${base === '/' ? '' : base}/${child}`
}
</script>

<style lang="scss" scoped>
.sidebar-container {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  width: $sidebar-width;
  background: $bg-sidebar;
  transition: width $transition-normal;
  z-index: 1001;
  display: flex;
  flex-direction: column;

  &.is-collapsed {
    width: $sidebar-collapsed-width;

    .sidebar-logo {
      .logo-title {
        display: none;
      }
    }
  }
}

.sidebar-logo {
  height: $navbar-height;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-sm;
  background: #2b3a4d;

  .logo-title {
    color: #fff;
    font-size: $font-size-lg;
    font-weight: 600;
    white-space: nowrap;
  }
}

.sidebar-scroll {
  flex: 1;

  :deep(.el-menu) {
    border-right: none;
  }
}
</style>
