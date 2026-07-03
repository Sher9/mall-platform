// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  // 应用配置
  app: {
    head: {
      title: '商城 - 在线购物平台',
      meta: [
        { charset: 'utf-8' },
        { name: 'viewport', content: 'width=device-width, initial-scale=1' },
        { name: 'description', content: '基于 Nuxt3 + Element Plus 的在线商城' }
      ],
      link: [
        { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }
      ]
    },
    baseURL: '/'
  },

  // 运行时配置（环境变量）
  runtimeConfig: {
    public: {
      apiBase: process.env.API_BASE || 'http://localhost:8000'
    }
  },

  // 自动导入
  imports: {
    dirs: ['composables/**', 'utils/**']
  },

  // 组件自动导入
  components: [
    {
      path: '~/components',
      extensions: ['vue'],
      global: true
    }
  ],

  // 插件
  plugins: [
    '~/plugins/element-plus.client'
  ],

  // 模块
  modules: [
    '@element-plus/nuxt',
    '@pinia/nuxt',
    '@nuxtjs/tailwindcss'
  ],

  // Element Plus 配置
  elementPlus: {
    importStyle: 'scss',
    themes: ['dark'],
    defaultLocale: 'zh-cn'
  },

  // CSS
  css: [
    '~/assets/styles/main.scss',
    '~/assets/styles/vant.scss',
    'element-plus/dist/index.css'
  ],

  // 构建配置
  build: {
    transpile: ['element-plus']
  },

  // 开发服务器代理（解决跨域问题）
  vite: {
    server: {
      port: 3001,
      host: '0.0.0.0',
      proxy: {
        '/api': {
          target: 'http://localhost:8000',
          changeOrigin: true
        }
      }
    }
  },

  // 源图
  sourcemap: false,

  // 兼容性
  compatibilityDate: '2024-01-01'
})
