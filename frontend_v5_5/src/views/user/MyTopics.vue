<template>
  <div class="my-topics-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">我的话题</span>
      </template>
    </el-page-header>

    <el-card v-loading="loading" class="list-card">
      <template v-if="topicList.length > 0">
        <TopicCard v-for="topic in topicList" :key="topic.id" :topic="topic" />
      </template>
      <Empty v-else description="暂无话题，快去发布第一条吧！" />

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadTopics"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyTopics } from '@/api/community'
import TopicCard from '@/components/community/TopicCard.vue'
import Empty from '@/components/common/Empty.vue'


const loading = ref(false)
const topicList = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)

// 加载话题列表
const loadTopics = async () => {
  try {
    loading.value = true
    const res = await getMyTopics(page.value, pageSize.value)
    topicList.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('加载话题失败:', error)
  } finally {
    loading.value = false
  }
}

// 分页大小改变
const handleSizeChange = (size) => {
  page.value = 1
  pageSize.value = size
  loadTopics()
}

onMounted(() => {
  loadTopics()
})
</script>

<style scoped lang="scss">
.my-topics-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .list-card {
    margin-top: 20px;
    min-height: 400px;
  }

  .pagination-wrapper {
    margin-top: 24px;
    display: flex;
    justify-content: center;
  }
}
</style>
