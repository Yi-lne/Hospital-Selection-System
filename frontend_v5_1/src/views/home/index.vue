<template>
  <div class="home-page">
    <!-- 轮播图 -->
    <el-carousel height="400px" class="hero-carousel">
      <el-carousel-item v-for="item in banners" :key="item.id">
        <div class="carousel-item" :style="{ backgroundImage: `url(${item.image})` }">
          <div class="carousel-content">
            <h1>{{ item.title }}</h1>
            <p>{{ item.description }}</p>
            <el-button type="primary" size="large" @click="item.action">立即体验</el-button>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 快捷入口 -->
    <div class="quick-access">
      <el-card
        v-for="item in quickAccessItems"
        :key="item.id"
        class="access-card"
        shadow="hover"
        @click="item.action"
      >
        <el-icon :size="48" :color="item.color">
          <component :is="item.icon" />
        </el-icon>
        <h3>{{ item.title }}</h3>
        <p>{{ item.description }}</p>
      </el-card>
    </div>

    <!-- 推荐医院 -->
    <div class="section">
      <div class="section-header">
        <h2>推荐医院</h2>
        <el-link type="primary" @click="$router.push('/hospital')">查看更多 →</el-link>
      </div>
      <el-row :gutter="20">
        <el-col v-for="hospital in recommendedHospitals" :key="hospital.id" :xs="24" :sm="12" :md="8">
          <HospitalCard :hospital="hospital" />
        </el-col>
      </el-row>
    </div>

    <!-- 社区热帖 -->
    <div class="section">
      <div class="section-header">
        <h2>社区热帖</h2>
        <el-link type="primary" @click="$router.push('/community')">查看更多 →</el-link>
      </div>
      <el-card v-for="topic in hotTopics" :key="topic.id" class="topic-item" shadow="hover" @click="goToTopic(topic.id)">
        <div class="topic-header">
          <h3>{{ topic.title }}</h3>
          <el-tag size="small" type="danger">热</el-tag>
        </div>
        <p>{{ (topic.content || '').slice(0, 100) }}...</p>
        <div class="topic-meta">
          <span>{{ topic.authorName || '匿名' }}</span>
          <span>{{ topic.viewCount || 0 }} 浏览</span>
          <span>{{ topic.commentCount || 0 }} 评论</span>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import { OfficeBuilding, User, ChatDotRound, Star, Edit } from '@element-plus/icons-vue'
import HospitalCard from '@/components/hospital/HospitalCard.vue'
import { getHospitalList } from '@/api/hospital'
import { getTopicList } from '@/api/community'

const router = useRouter()

const banners = ref([
  {
    id: 1,
    title: '智能医院选择系统',
    description: '为您的健康保驾护航',
    image: 'https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=1600',
    action: () => router.push('/hospital')
  },
  {
    id: 2,
    title: '病友交流社区',
    description: '分享经验，互相支持',
    image: 'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=1600',
    action: () => router.push('/community')
  }
])

const quickAccessItems = ref([
  {
    id: 1,
    title: '找医院',
    description: '搜索全国医院',
    icon: markRaw(OfficeBuilding),
    color: '#409eff',
    action: () => router.push('/hospital')
  },
  {
    id: 2,
    title: '社区交流',
    description: '病友经验分享',
    icon: markRaw(ChatDotRound),
    color: '#e6a23c',
    action: () => router.push('/community')
  },
  {
    id: 3,
    title: '我的收藏',
    description: '收藏的内容',
    icon: markRaw(Star),
    color: '#f56c6c',
    action: () => router.push('/user/collection')
  },
  {
    id: 4,
    title: '发布话题',
    description: '分享就医经验',
    icon: markRaw(Edit),
    color: '#67c23a',
    action: () => router.push('/community/publish')
  }
])

const recommendedHospitals = ref([])
const hotTopics = ref([])

// 跳转到话题详情
const goToTopic = (topicId) => {
  router.push(`/community/topic/${topicId}`)
}

onMounted(async () => {
  try {
    // 获取推荐医院 - 获取更多数据以便筛选和随机
    const hospitalRes = await getHospitalList({ page: 1, pageSize: 100 })
    const allHospitals = hospitalRes.data.list || []

    console.log('总医院数:', allHospitals.length)

    // 筛选三甲医院（grade3A）
    const grade3AHospitals = allHospitals.filter((hospital) =>
      hospital.hospitalLevel === 'grade3A' || hospital.level === 'grade3A'
    )

    console.log('三甲医院数:', grade3AHospitals.length)

    // 如果三甲医院不足6个，使用所有医院
    let filteredHospitals = grade3AHospitals
    if (filteredHospitals.length < 6) {
      filteredHospitals = allHospitals
      console.log('三甲医院不足，使用所有医院')
    }

    // 映射字段名并随机打乱
    recommendedHospitals.value = filteredHospitals
      .map((item) => ({
        id: item.id,
        name: item.hospitalName || item.name,
        level: item.hospitalLevel || item.level,
        province: item.provinceName || item.province,
        city: item.cityName || item.city,
        address: item.address,
        avatar: null  // 不设置avatar，让组件使用默认头像
      }))
      .sort(() => Math.random() - 0.5) // 随机打乱
      .slice(0, 6) // 取前6个

    console.log('最终展示的医院数:', recommendedHospitals.value.length)

    // 获取热帖 - 获取更多数据，筛选并随机选择
    const topicRes = await getTopicList(1, 100)
    const allTopics = topicRes.data.list || []

    console.log('总话题数:', allTopics.length)

    // 第一轮筛选：评论数 >= 50 或 浏览量 >= 3000
    let hotTopicsFiltered = allTopics.filter((topic) =>
      (topic.commentCount >= 50 || topic.viewCount >= 3000) &&
      topic.status === 1
    )

    console.log('第一轮筛选后:', hotTopicsFiltered.length)

    // 如果满足条件的话题不足5个，降低筛选条件
    if (hotTopicsFiltered.length < 5) {
      hotTopicsFiltered = allTopics.filter((topic) =>
        ((topic.commentCount >= 10 || topic.viewCount >= 500) &&
        topic.status === 1)
      )
      console.log('第二轮筛选后:', hotTopicsFiltered.length)
    }

    // 如果还是不足5个，再降低条件
    if (hotTopicsFiltered.length < 5) {
      hotTopicsFiltered = allTopics.filter((topic) =>
        (topic.status === 1)
      )
      console.log('第三轮筛选后（仅状态）:', hotTopicsFiltered.length)
    }

    // 如果还是没有话题，使用所有话题
    if (hotTopicsFiltered.length === 0) {
      hotTopicsFiltered = allTopics
      console.log('使用所有话题:', hotTopicsFiltered.length)
    }

    // 随机打乱数组
    const shuffled = hotTopicsFiltered.sort(() => Math.random() - 0.5)

    // 取前5个作为热帖
    hotTopics.value = shuffled.slice(0, 5)

    console.log('最终展示的热帖数:', hotTopics.value.length)
  } catch (error) {
    console.error('加载数据失败:', error)
  }
})
</script>

<style scoped lang="scss">
.home-page {
  padding-bottom: 40px;
}

.hero-carousel {
  margin-bottom: 40px;

  .carousel-item {
    height: 100%;
    background-size: cover;
    background-position: center;
    position: relative;

    &::before {
      content: '';
      position: absolute;
      inset: 0;
      background: rgba(0, 0, 0, 0.4);
    }

    .carousel-content {
      position: relative;
      z-index: 1;
      height: 100%;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      color: #fff;
      text-align: center;
      padding: 0 20px;

      h1 {
        font-size: 48px;
        margin-bottom: 16px;
      }

      p {
        font-size: 20px;
        margin-bottom: 32px;
        opacity: 0.9;
      }
    }
  }
}

.quick-access {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 40px;

  .access-card {
    text-align: center;
    padding: 30px;
    cursor: pointer;
    transition: transform 0.3s;

    &:hover {
      transform: translateY(-8px);
    }

    h3 {
      margin: 16px 0 8px;
      font-size: 20px;
      color: #303133;
    }

    p {
      margin: 0;
      color: #909399;
      font-size: 14px;
    }
  }
}

.section {
  margin-bottom: 40px;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h2 {
      margin: 0;
      font-size: 24px;
      color: #303133;
    }
  }

  .topic-item {
    margin-bottom: 16px;
    cursor: pointer;

    .topic-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;

      h3 {
        margin: 0;
        font-size: 16px;
        color: #303133;
      }
    }

    p {
      margin: 0 0 12px;
      color: #606266;
      font-size: 14px;
    }

    .topic-meta {
      display: flex;
      gap: 20px;
      font-size: 13px;
      color: #909399;
    }
  }
}
</style>
