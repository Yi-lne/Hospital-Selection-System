<template>
  <div class="user-card" @click="handleClick">
    <el-avatar :size="avatarSize" :src="user.avatar || defaultAvatar">
      {{ user.nickname?.charAt(0) || '用' }}
    </el-avatar>
    <div class="user-info">
      <h4 class="username">{{ user.nickname || '用户' }}</h4>
      <p v-if="showPhone && user.phone" class="phone">{{ maskPhone(user.phone) }}</p>
      <p v-if="showBio && user.bio" class="bio">{{ user.bio }}</p>
      <div class="stats">
        <span v-if="showStats"><b>{{ user.topicCount || 0 }}</b> 话题</span>
        <span v-if="showStats"><b>{{ u ser.followersCount || 0 }}</b> 粉丝</span>
      </div>
    </div>
    <slot />
  </div>
</template>

<script setup>
const props = defineProps({
  user: {
    type: Object,
    required: true
  },
  avatarSize: {
    type: Number,
    default: 50
  },
  showPhone: {
    type: Boolean,
    default: false
  },
  showBio: {
    type: Boolean,
    default: false
  },
  showStats: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['click'])

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const maskPhone = (phone) => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

const handleClick = () => {
  emit('click', props.user)
}
</script>

<style scoped lang="scss">
.user-card {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    background: #f5f7fa;
  }

  .user-info {
    flex: 1;

    .username {
      margin: 0 0 4px;
      font-size: 15px;
      font-weight: 500;
      color: #303133;
    }

    .phone,
    .bio {
      margin: 0 0 4px;
      font-size: 13px;
      color: #909399;
    }

    .stats {
      display: flex;
      gap: 16px;
      font-size: 13px;
      color: #606266;

      b {
        color: #303133;
      }
    }
  }
}
</style>
