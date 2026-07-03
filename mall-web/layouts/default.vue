<template>
  <div class="mall-layout">
    <!-- 顶部导航栏 -->
    <header class="mall-header">
      <div class="header-container">
        <!-- Logo -->
        <div class="logo" @click="navigateTo('/')">
          <el-icon :size="28" color="#409eff"><ShoppingCartFull /></el-icon>
          <span class="logo-text">商城</span>
        </div>

        <!-- 搜索栏 -->
        <div class="search-bar">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索商品..."
            size="large"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="handleSearch" />
            </template>
          </el-input>
        </div>

        <!-- 右侧操作区 -->
        <div class="header-actions">
          <el-badge :value="cartCount" :hidden="cartCount === 0" class="cart-badge">
            <el-button text type="primary" size="large" @click="navigateTo('/cart')">
              <el-icon :size="22"><ShoppingCart /></el-icon>
              <span class="action-text">购物车</span>
            </el-button>
          </el-badge>

          <!-- 未登录 -->
          <template v-if="!isLoggedIn">
            <el-button type="primary" @click="navigateTo('/login')">登录</el-button>
            <el-button @click="navigateTo('/register')">注册</el-button>
          </template>

          <!-- 已登录 -->
          <template v-else>
            <el-dropdown @command="handleUserCommand">
              <el-button text>
                <el-icon :size="22"><User /></el-icon>
                <span class="action-text">{{ userInfo?.nickname || '用户' }}</span>
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="center">个人中心</el-dropdown-item>
                  <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </div>
      </div>
    </header>

    <!-- 分类导航 -->
    <nav class="category-nav">
      <div class="nav-container">
        <div
          v-for="cat in categories"
          :key="cat"
          class="nav-item"
          :class="{ active: currentCategory === cat }"
          @click="handleCategoryClick(cat)"
        >
          {{ cat }}
        </div>
      </div>
    </nav>

    <!-- 主要内容区 -->
    <main class="mall-main">
      <slot />
    </main>

    <!-- 底部 -->
    <footer class="mall-footer">
      <div class="footer-container">
        <div class="footer-links">
          <div class="footer-col">
            <h4>关于我们</h4>
            <a href="#">公司简介</a>
            <a href="#">联系方式</a>
            <a href="#">招聘信息</a>
          </div>
          <div class="footer-col">
            <h4>客户服务</h4>
            <a href="#">帮助中心</a>
            <a href="#">售后服务</a>
            <a href="#">投诉建议</a>
          </div>
          <div class="footer-col">
            <h4>支付方式</h4>
            <a href="#">微信支付</a>
            <a href="#">支付宝</a>
            <a href="#">银联支付</a>
          </div>
          <div class="footer-col">
            <h4>配送方式</h4>
            <a href="#">上门自提</a>
            <a href="#">快递配送</a>
            <a href="#">配送说明</a>
          </div>
        </div>
        <div class="footer-bottom">
          <p>© 2024 商城 版权所有 | 京ICP备XXXXXXXX号</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { Search, ShoppingCart, ShoppingCartFull, User, ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { useCartStore } from '@/store/cart'

const userStore = useUserStore()
const cartStore = useCartStore()
const route = useRoute()

const searchKeyword = ref('')
const currentCategory = ref('')

// 分类列表
const categories = ['首页', '电子产品', '服装服饰', '食品饮料', '家居生活', '运动户外']

// 计算属性
const isLoggedIn = computed(() => userStore.isLoggedIn)
const userInfo = computed(() => userStore.userInfo)
const cartCount = computed(() => cartStore.totalCount)

// 搜索
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    navigateTo(`/product?keyword=${encodeURIComponent(searchKeyword.value.trim())}`)
  }
}

// 分类点击
const handleCategoryClick = (cat: string) => {
  if (cat === '首页') {
    navigateTo('/')
  } else {
    navigateTo(`/product?category=${encodeURIComponent(cat)}`)
  }
}

// 用户下拉菜单
const handleUserCommand = (command: string) => {
  switch (command) {
    case 'center':
      navigateTo('/user/center')
      break
    case 'orders':
      navigateTo('/user/orders')
      break
    case 'logout':
      userStore.logout()
      navigateTo('/')
      break
  }
}

// 初始化
onMounted(async () => {
  await userStore.initUserInfo()
  await cartStore.initCart()
})
</script>

<style lang="scss" scoped>
.mall-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

/* 顶部导航 */
.mall-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 64px;
  display: flex;
  align-items: center;
  gap: 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex-shrink: 0;

  .logo-text {
    font-size: 22px;
    font-weight: 700;
    background: linear-gradient(135deg, #409eff, #67c23a);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }
}

.search-bar {
  flex: 1;
  max-width: 520px;

  :deep(.el-input-group__append) {
    background: #409eff;
    color: #fff;
    cursor: pointer;
    border-color: #409eff;

    .el-button {
      color: #fff;
    }
  }
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.cart-badge {
  :deep(.el-badge__content) {
    z-index: 1;
  }
}

.action-text {
  margin-left: 4px;
}

/* 分类导航 */
.category-nav {
  background: #fff;
  border-bottom: 2px solid #409eff;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  gap: 0;
}

.nav-item {
  padding: 12px 24px;
  cursor: pointer;
  font-size: 15px;
  color: #333;
  transition: all 0.3s;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;

  &:hover,
  &.active {
    color: #409eff;
    border-bottom-color: #409eff;
    background: rgba(64, 158, 255, 0.05);
  }
}

/* 主要内容区 */
.mall-main {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 20px auto;
  padding: 0 20px;
}

/* 底部 */
.mall-footer {
  background: #2c3e50;
  color: #ecf0f1;
  margin-top: auto;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px 20px;
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 40px;
  margin-bottom: 30px;
}

.footer-col {
  h4 {
    font-size: 16px;
    margin-bottom: 16px;
    color: #fff;
  }

  a {
    display: block;
    color: #bdc3c7;
    text-decoration: none;
    margin-bottom: 8px;
    font-size: 14px;
    transition: color 0.3s;

    &:hover {
      color: #409eff;
    }
  }
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #34495e;
  color: #95a5a6;
  font-size: 14px;
}

/* 响应式 */
@media (max-width: 768px) {
  .header-container {
    gap: 12px;
  }

  .action-text {
    display: none;
  }

  .nav-container {
    overflow-x: auto;
  }

  .footer-links {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }
}
</style>
