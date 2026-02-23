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

<script setup>
import { computed } from 'vue'

const props = defineProps({
  avatar: String,
  username: String,
  size: {
    type: [Number, String],
    default: 'default'
  },
  shape: {
    type: String,
    default: 'circle'
  }
})

const emit = defineEmits(['click'])

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const avatarUrl = computed(() => props.avatar)

const fallbackText = computed(() => {
  return props.username ? props.username.charAt(0).toUpperCase() : 'U'
})

const handleClick = () => {
  emit('click')
}
</script>
