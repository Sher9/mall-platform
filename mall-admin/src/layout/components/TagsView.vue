<template>
  <div class="tags-view">
    <el-scrollbar>
      <div class="tags-scroll">
        <div
          v-for="tag in visitedTags"
          :key="tag.path"
          class="tag-item"
          :class="{ active: isActive(tag) }"
          @click="router.push(tag.path)"
        >
          <span>{{ tag.title }}</span>
          <el-icon v-if="!tag.affix" class="tag-close" @click.stop="closeTag(tag)">
            <Close />
          </el-icon>
        </div>
      </div>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter, type RouteLocationNormalized } from 'vue-router'

interface TagItem {
  path: string
  title: string
  affix?: boolean
}

const route = useRoute()
const router = useRouter()
const visitedTags = ref<TagItem[]>([])

function addTag(to: RouteLocationNormalized) {
  if (to.meta?.hidden || to.path === '/login') return
  const exist = visitedTags.value.find((t) => t.path === to.path)
  if (!exist) {
    visitedTags.value.push({
      path: to.path,
      title: (to.meta.title as string) || '未命名',
      affix: to.meta.affix as boolean
    })
  }
}

function isActive(tag: TagItem) {
  return tag.path === route.path
}

function closeTag(tag: TagItem) {
  const index = visitedTags.value.findIndex((t) => t.path === tag.path)
  if (index === -1) return
  visitedTags.value.splice(index, 1)
  if (isActive(tag)) {
    const next = visitedTags.value[index] || visitedTags.value[index - 1]
    router.push(next ? next.path : '/')
  }
}

watch(() => route.path, () => addTag(route), { immediate: true })
</script>

<style lang="scss" scoped>
.tags-view {
  height: $tags-height;
  background: $bg-white;
  border-bottom: 1px solid #d8dce5;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.06);
}

.tags-scroll {
  display: flex;
  align-items: center;
  height: $tags-height;
  padding: 0 $spacing-sm;
  gap: $spacing-xs;
  white-space: nowrap;
}

.tag-item {
  display: inline-flex;
  align-items: center;
  gap: $spacing-xs;
  height: 26px;
  padding: 0 $spacing-sm;
  font-size: $font-size-sm;
  color: $text-regular;
  border: 1px solid #d8dce5;
  border-radius: $radius-sm;
  cursor: pointer;
  transition: all $transition-fast;

  &:hover {
    color: $primary-color;
  }

  &.active {
    background: $primary-color;
    color: #fff;
    border-color: $primary-color;
  }

  .tag-close {
    font-size: 12px;
    border-radius: 50%;
    transition: background $transition-fast;

    &:hover {
      background: rgba(255, 255, 255, 0.3);
    }
  }
}
</style>
