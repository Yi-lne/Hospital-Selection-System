<template>
  <el-avatar
    :size="size"
    :src="avatarUrl || defaultAvatar"
    :shape="shape"
    @click="handleClick"
  >
    {{ fallbackText }}
  </el-avatar>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(
  defineProps<{
    avatar?: string
    username?: string
    size?: number | 'large' | 'default' | 'small'
    shape?: 'circle' | 'square'
  }>(),
  {
    size: 'default',
    shape: 'circle'
  }
)

const emit = defineEmits<{
  click: []
}>()

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const avatarUrl = computed(() => props.avatar)

const fallbackText = computed(() => {
  return props.username ? props.username.charAt(0).toUpperCase() : 'U'
})

const handleClick = () => {
  emit('click')
}
</script>
