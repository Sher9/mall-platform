<!-- uni-ui 演示页面 -->
<template>
  <view class="uni-ui-demo">
    <uni-nav-bar title="uni-ui 演示" />
    
    <!-- 按钮 -->
    <uni-section title="按钮" type="line" padding>
      <view class="button-group">
        <button type="primary" size="mini">主要按钮</button>
        <button type="warn" size="mini">警告按钮</button>
        <button type="default" size="mini">默认按钮</button>
      </view>
    </uni-section>
    
    <!-- 卡片 -->
    <uni-section title="卡片" type="line" padding>
      <uni-card title="卡片标题" sub-title="副标题" extra="额外信息">
        <text>这是一个uni-ui卡片组件</text>
      </uni-card>
    </uni-section>
    
    <!-- 列表 -->
    <uni-section title="列表" type="line" padding>
      <uni-list>
        <uni-list-item title="列表项1" note="描述信息" show-arrow />
        <uni-list-item title="列表项2" note="描述信息" show-arrow />
        <uni-list-item title="列表项3" note="描述信息" show-arrow />
      </uni-list>
    </uni-section>
    
    <!-- 徽标 -->
    <uni-section title="徽标" type="line" padding>
      <view class="badge-demo">
        <uni-badge text="1" type="error" />
        <uni-badge text="新" type="success" style="margin-left: 20px;" />
        <uni-badge text="99+" type="warning" style="margin-left: 20px;" />
      </view>
    </uni-section>
    
    <!-- 评分 -->
    <uni-section title="评分" type="line" padding>
      <uni-rate :value="rateValue" @change="onRateChange" />
      <text style="margin-left: 10px;">评分: {{ rateValue }}星</text>
    </uni-section>
    
    <!-- 数字输入框 -->
    <uni-section title="数字输入" type="line" padding>
      <uni-number-box :value="numberValue" @change="onNumberChange" />
    </uni-section>
    
    <!-- 操作按钮 -->
    <view class="actions">
      <button type="primary" @click="handleToast">显示Toast</button>
      <button type="default" @click="handlePopup" style="margin-top: 10px;">显示弹窗</button>
    </view>
    
    <!-- 弹窗 -->
    <uni-popup ref="popupRef" type="center">
      <view class="popup-content">
        <text>这是一个uni-popup弹窗</text>
        <button @click="closePopup" style="margin-top: 20px;">关闭</button>
      </view>
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'

// 评分值
const rateValue = ref(3)

// 数字值
const numberValue = ref(1)

// 弹窗引用
const popupRef = ref()

// 评分变化
const onRateChange = (value: number) => {
  rateValue.value = value
}

// 数字变化
const onNumberChange = (value: number) => {
  numberValue.value = value
}

// 显示Toast
const handleToast = () => {
  uni.showToast({
    title: '这是一个Toast提示',
    icon: 'none'
  })
}

// 显示弹窗
const handlePopup = () => {
  popupRef.value?.open()
}

// 关闭弹窗
const closePopup = () => {
  popupRef.value?.close()
}
</script>

<style scoped>
.uni-ui-demo {
  padding-bottom: 20px;
}

.button-group {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.badge-demo {
  display: flex;
  align-items: center;
}

.popup-content {
  background: white;
  padding: 30px;
  border-radius: 8px;
  text-align: center;
}

.actions {
  padding: 16px;
}
</style>
