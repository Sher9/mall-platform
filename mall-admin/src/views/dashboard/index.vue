<template>
  <div class="page-container">
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card" shadow="never">
      <div class="welcome-content">
        <el-avatar :size="64" :src="userStore.avatar">
          <el-icon :size="32"><UserFilled /></el-icon>
        </el-avatar>
        <div class="welcome-info">
          <h2>{{ greeting }}，{{ userStore.nickname }}</h2>
          <p>欢迎使用商城管理后台，今天是 {{ today }}</p>
        </div>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="item in stats" :key="item.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: item.color }">
              <el-icon :size="28" color="#fff"><component :is="item.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ item.value }}</p>
              <p class="stat-title">{{ item.title }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <span>近7天订单趋势</span>
          </template>
          <div ref="chartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <router-link v-for="action in quickActions" :key="action.path" :to="action.path">
              <el-button :icon="action.icon" plain>{{ action.title }}</el-button>
            </router-link>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import * as echarts from 'echarts'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 17) return '下午好'
  if (hour < 19) return '傍晚好'
  return '晚上好'
})

const today = new Date().toLocaleDateString('zh-CN', {
  year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'
})

const stats = ref([
  { title: '商品总数', value: '128', icon: 'Goods', color: '#409eff' },
  { title: '今日订单', value: '36', icon: 'List', color: '#67c23a' },
  { title: '注册客户', value: '1,024', icon: 'User', color: '#e6a23c' },
  { title: '库存预警', value: '5', icon: 'Warning', color: '#f56c6c' }
])

const quickActions = [
  { title: '新增商品', path: '/product/list', icon: 'Goods' },
  { title: '查看订单', path: '/order/list', icon: 'List' },
  { title: '客户管理', path: '/customer/list', icon: 'User' },
  { title: '库存管理', path: '/stock/list', icon: 'Box' },
  { title: '系统用户', path: '/system/admin', icon: 'UserFilled' },
  { title: '角色管理', path: '/system/role', icon: 'Avatar' }
]

// ECharts 图表
const chartRef = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null

function initChart() {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  chartInstance.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      boundaryGap: false
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '订单数',
        type: 'line',
        smooth: true,
        data: [12, 18, 25, 30, 28, 35, 40],
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.01)' }
          ])
        },
        lineStyle: { color: '#409eff', width: 2 },
        itemStyle: { color: '#409eff' }
      }
    ]
  })
}

function handleResize() {
  chartInstance?.resize()
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<style lang="scss" scoped>
.welcome-card {
  margin-bottom: $spacing-lg;

  .welcome-content {
    display: flex;
    align-items: center;
    gap: $spacing-lg;

    .welcome-info {
      h2 { font-size: $font-size-xl; margin-bottom: $spacing-xs; }
      p { color: $text-secondary; font-size: $font-size-md; }
    }
  }
}

.stat-row {
  margin-bottom: $spacing-lg;
}

.stat-card {
  .stat-content {
    display: flex;
    align-items: center;
    gap: $spacing-lg;
  }

  .stat-icon {
    width: 56px;
    height: 56px;
    border-radius: $radius-md;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .stat-info {
    .stat-value {
      font-size: $font-size-xxl;
      font-weight: 700;
      color: $text-primary;
      line-height: 1.2;
    }
    .stat-title {
      font-size: $font-size-sm;
      color: $text-secondary;
      margin-top: $spacing-xs;
    }
  }
}

.chart-card {
  .chart-container {
    height: 320px;
  }
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-md;

  .el-button {
    width: calc(50% - #{$spacing-md} / 2);
  }
}
</style>
