<template>
  <div class="slider-captcha">
    <div class="captcha-header">
      <span class="captcha-title">安全验证</span>
      <el-button
        type="text"
        @click="refreshCaptcha"
        :loading="loading"
        class="refresh-btn"
      >
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </div>

    <div class="captcha-content">
      <!-- 背景图容器 -->
      <div class="background-container" ref="bgContainerRef">
        <img
          v-if="backgroundImage"
          :src="backgroundImage"
          alt="验证码背景"
          class="background-image"
        />
        <!-- 滑块拼图 -->
        <div
          v-if="sliderImage && isDragging"
          class="slider-piece"
          :style="{ left: currentX + 'px', top: sliderY + 'px' }"
        >
          <img :src="sliderImage" alt="滑块" />
        </div>
      </div>

      <!-- 滑块轨道 -->
      <div class="slider-track">
        <div class="slider-button" ref="sliderBtnRef">
          <div
            class="slider-handle"
            :class="{ 'is-active': isDragging, 'is-verified': isVerified }"
            ref="sliderHandleRef"
            @mousedown="handleMouseDown"
          >
            <el-icon v-if="!isVerified"><CaretRight /></el-icon>
            <el-icon v-else class="success-icon"><Select /></el-icon>
          </div>
        </div>
        <div class="slider-text" v-if="!isVerified">
          {{ sliderText }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Refresh, CaretRight, Select } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { captchaApi } from '@/api/user'

const emit = defineEmits(['captcha-verified', 'captcha-failed', 'before-verify', 'captcha-refreshed'])

const backgroundImage = ref('')
const sliderImage = ref('')
const captchaId = ref('')
const loading = ref(false)

const isDragging = ref(false)
const isVerified = ref(false)
const currentX = ref(0)
const sliderY = ref(60) // 滑块垂直位置（将从后端获取）
const sliderText = ref('请先填写上方信息')
const canDrag = ref(false) // 是否可以拖动，初始为false

const bgContainerRef = ref(null)
const sliderBtnRef = ref(null)
const sliderHandleRef = ref(null)

let startX = 0
let maxMove = 0

// 生成验证码
const generateCaptcha = async () => {
  try {
    loading.value = true
    const res = await captchaApi.generate()

    if (res.code === 200) {
      backgroundImage.value = res.data.backgroundImage
      sliderImage.value = res.data.sliderImage
      captchaId.value = res.data.captchaId
      sliderY.value = res.data.sliderY || 60
      // 重置状态，但不改变 canDrag，让父组件控制
      isDragging.value = false
      isVerified.value = false
      currentX.value = 0
    } else {
      ElMessage.error(res.msg || '验证码生成失败')
    }
  } catch (error) {
    console.error('生成验证码失败:', error)
    ElMessage.error('验证码生成失败，请稍后重试')
  } finally {
    loading.value = false
  }

  // 通知父组件验证码已刷新，让父组件重新检查表单状态
  emit('captcha-refreshed')
}

// 刷新验证码
const refreshCaptcha = () => {
  generateCaptcha()
}

// 重置状态（保留 canDrag 状态）
const resetState = () => {
  isDragging.value = false
  isVerified.value = false
  currentX.value = 0
  // 不修改 canDrag 和 sliderText，让父组件控制
}

// 鼠标按下事件
const handleMouseDown = (e) => {
  if (isVerified.value || loading.value) return

  // 检查是否允许拖动
  if (!canDrag.value) {
    return
  }

  // 触发验证前的检查事件，让父组件检查是否已填写完信息
  emit('before-verify')

  isDragging.value = true
  startX = e.clientX

  const trackWidth = sliderBtnRef.value?.offsetWidth || 0
  maxMove = trackWidth - 40 // 40是滑块按钮的宽度

  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
}

// 鼠标移动事件
const handleMouseMove = (e) => {
  if (!isDragging.value) return

  const moveX = e.clientX - startX

  // 限制移动范围
  if (moveX < 0) {
    currentX.value = 0
  } else if (moveX > maxMove) {
    currentX.value = maxMove
  } else {
    currentX.value = moveX
  }
}

// 鼠标松开事件
const handleMouseUp = async () => {
  if (!isDragging.value) return

  document.removeEventListener('mousemove', handleMouseMove)
  document.removeEventListener('mouseup', handleMouseUp)

  isDragging.value = false

  // 验证滑块位置
  await verifyCaptcha()
}

// 验证验证码
const verifyCaptcha = async () => {
  try {
    const res = await captchaApi.verify({
      captchaId: captchaId.value,
      moveX: Math.round(currentX.value)
    })

    if (res.code === 200 && res.data) {
      isVerified.value = true
      sliderText.value = '验证成功'
      ElMessage.success('验证成功')

      // 发送验证成功事件
      emit('captcha-verified', {
        captchaId: captchaId.value,
        moveX: Math.round(currentX.value)
      })
    } else {
      sliderText.value = '验证失败，请重试'
      ElMessage.error('验证失败，请重试')

      // 发送验证失败事件，通知父组件清空表单
      emit('captcha-failed')

      // 1秒后刷新验证码
      setTimeout(() => {
        refreshCaptcha()
      }, 1000)
    }
  } catch (error) {
    console.error('验证验证码失败:', error)
    sliderText.value = '验证失败，请重试'
    ElMessage.error('验证失败，请重试')

    // 发送验证失败事件，通知父组件清空表单
    emit('captcha-failed')

    setTimeout(() => {
      refreshCaptcha()
    }, 1000)
  }
}

// 暴露方法供父组件调用
defineExpose({
  refreshCaptcha,
  getCaptchaData: () => ({
    captchaId: captchaId.value,
    moveX: Math.round(currentX.value)
  }),
  setCanDrag: (value) => {
    canDrag.value = value
    // 根据是否可拖动更新提示文本
    if (value) {
      sliderText.value = '向右滑动填充拼图'
    } else {
      sliderText.value = '请先填写上方信息'
    }
  }
})

onMounted(() => {
  generateCaptcha()
})
</script>

<style scoped lang="scss">
.slider-captcha {
  width: 100%;
  margin: 20px 0;
}

.captcha-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.captcha-title {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.refresh-btn {
  color: #409eff;
  cursor: pointer;
  font-size: 14px;

  &:hover {
    color: #66b1ff;
  }

  .el-icon {
    margin-right: 4px;
  }
}

.captcha-content {
  width: 100%;
}

.background-container {
  position: relative;
  width: 350px;
  height: 200px;
  margin: 0 auto;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
  background: #f5f7fa;
}

.background-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.slider-piece {
  position: absolute;
  width: 60px;
  height: 60px;

  img {
    width: 100%;
    height: 100%;
    display: block;
  }
}

.slider-track {
  position: relative;
  width: 350px;
  height: 40px;
  margin: 15px auto 0;
  background: #f7f9fa;
  border: 1px solid #e4e7ed;
  border-radius: 20px;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.1);
}

.slider-button {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
}

.slider-handle {
  position: absolute;
  left: 0;
  top: 0;
  width: 40px;
  height: 40px;
  background: linear-gradient(180deg, #ffffff 0%, #f0f0f0 100%);
  border: 1px solid #dcdfe6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
  transition: all 0.2s;
  transform: translateX(0);

  &.is-active {
    background: linear-gradient(180deg, #409eff 0%, #66b1ff 100%);
    border-color: #409eff;
    color: #fff;
  }

  &.is-verified {
    background: #67c23a;
    border-color: #67c23a;
    color: #fff;

    .success-icon {
      font-size: 20px;
    }
  }

  &.is-disabled {
    background: #f5f7fa;
    border-color: #dcdfe6;
    color: #c0c4cc;
    cursor: not-allowed;
    opacity: 0.6;
  }

  .el-icon {
    font-size: 18px;
  }
}

.slider-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 14px;
  color: #909399;
  pointer-events: none;
  user-select: none;
}
</style>
