<template>
  <div class="topic-detail-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">话题详情</span>
      </template>
    </el-page-header>

    <div v-loading="loading" class="content-wrapper">
      <template v-if="topicDetail">
        <!-- 主体内容 -->
        <el-card class="topic-card">
          <div class="topic-header">
            <div class="author-info">
              <el-avatar :size="48" :src="topicDetail.avatar || defaultAvatar" />
              <div>
                <h3>{{ topicDetail.nickname || '匿名用户' }}</h3>
                <span class="publish-time">{{ formatTime(topicDetail.createTime) }}</span>
              </div>
            </div>
            <div class="topic-actions">
              <el-button
                v-if="!isAuthor"
                :type="topicDetail.isCollected ? 'warning' : 'default'"
                :icon="topicDetail.isCollected ? StarFilled : Star"
                @click="toggleCollect"
              >
                {{ topicDetail.isCollected ? '已收藏' : '收藏' }}
              </el-button>
              <el-button
                :type="topicDetail.isLiked ? 'danger' : 'default'"
                :icon="Check"
                @click="toggleLike"
              >
                {{ topicDetail.isLiked ? '已点赞' : '点赞' }} ({{ topicDetail.likeCount }})
              </el-button>
              <el-button
                v-if="isAuthor"
                type="primary"
                :icon="Edit"
                @click="showEditDialog"
              >
                编辑
              </el-button>
              <el-button
                v-if="isAuthor"
                type="danger"
                :icon="Delete"
                @click="handleDelete"
              >
                删除
              </el-button>
              <el-button
                v-if="!isAuthor"
                type="info"
                @click="showReportDialog(1, topicDetail.id)"
              >
                举报
              </el-button>
            </div>
          </div>

          <h1 class="topic-title">{{ topicDetail.title }}</h1>

          <el-tag v-if="topicDetail.diseaseName" class="disease-tag" type="info">
            {{ topicDetail.diseaseName }}
          </el-tag>

          <div class="topic-content">
            {{ topicDetail.content }}
          </div>

          <div class="topic-stats">
            <span><el-icon><View /></el-icon> {{ topicDetail.viewCount }} 浏览</span>
            <span><el-icon><ChatDotRound /></el-icon> {{ topicDetail.commentCount }} 评论</span>
          </div>
        </el-card>

        <!-- 评论区 -->
        <el-card class="comment-card">
          <template #header>
            <div class="comment-header">
              <span>评论 ({{ commentList.length }})</span>
            </div>
          </template>

          <!-- 发表评论 -->
          <div v-if="userStore.isLogin" class="comment-input-area">
            <el-input
              v-model="commentContent"
              type="textarea"
              :rows="3"
              placeholder="发表你的评论..."
            />
            <el-button
              type="primary"
              :loading="submitting"
              @click="submitComment"
            >
              发表评论
            </el-button>
          </div>
          <div v-else class="login-tip">
            <el-link type="primary" @click="$router.push('/auth/login')">登录</el-link> 后发表评论
          </div>

          <!-- 评论列表 -->
          <div v-if="commentList.length > 0" class="comment-list">
            <div v-for="comment in commentList" :key="comment.id" class="comment-item">
              <!-- 评论头像和内容 -->
              <div class="comment-main">
                <el-avatar :size="40" :src="comment.avatar || defaultAvatar" />
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="author-name">{{ comment.nickname || '匿名用户' }}</span>
                    <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                  </div>
                  <p class="comment-text">{{ comment.content }}</p>
                  <div class="comment-actions">
                    <el-button
                      text
                      size="small"
                      @click="toggleLikeComment(comment)"
                    >
                      <el-icon><Check /></el-icon>
                      {{ comment.likeCount }}
                    </el-button>
                    <el-button
                      v-if="userStore.isLogin"
                      text
                      size="small"
                      @click="showReplyInput(comment)"
                    >
                      <el-icon><ChatDotRound /></el-icon>
                      回复
                    </el-button>
                    <el-button
                      v-if="userStore.isLogin && comment.userId !== userStore.userId"
                      text
                      size="small"
                      type="info"
                      @click="showReportDialog(2, comment.id)"
                    >
                      举报
                    </el-button>
                    <el-button
                      v-if="comment.userId === userStore.userId"
                      text
                      size="small"
                      type="danger"
                      @click="handleDeleteComment(comment.id)"
                    >
                      删除
                    </el-button>
                  </div>
                </div>
              </div>

              <!-- 回复输入框 -->
              <div v-if="replyingTo === comment.id" class="reply-input-area">
                <el-input
                  v-model="replyContent"
                  type="textarea"
                  :rows="2"
                  :placeholder="`回复 @${comment.nickname || '匿名用户'}...`"
                />
                <div class="reply-actions">
                  <el-button size="small" @click="cancelReply">取消</el-button>
                  <el-button
                    type="primary"
                    size="small"
                    :loading="submitting"
                    @click="submitReply(comment)"
                  >
                    发送
                  </el-button>
                </div>
              </div>

              <!-- 回复列表 -->
              <div v-if="comment.replies && comment.replies.length > 0" class="reply-list-wrapper">
                <div v-if="comment.replies.length >= 3" class="reply-toggle">
                  <el-button
                    text
                    size="small"
                    @click="toggleCommentExpanded(comment.id)"
                  >
                    {{ isCommentExpanded(comment.id) ? '收起回复' : `展开回复 (${comment.replies.length})` }}
                  </el-button>
                </div>
                <div v-show="comment.replies.length < 3 || isCommentExpanded(comment.id)" class="reply-list">
                  <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                    <el-avatar :size="32" :src="reply.avatar || defaultAvatar" />
                    <div class="reply-content">
                      <div class="reply-header">
                        <span class="author-name">{{ reply.nickname || '匿名用户' }}</span>
                        <span v-if="reply.replyToNickname" class="reply-to">
                          回复 @{{ reply.replyToNickname }}
                        </span>
                        <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
                      </div>
                      <p class="reply-text">{{ reply.content }}</p>
                      <div class="reply-actions">
                        <el-button
                          v-if="userStore.isLogin && reply.userId !== userStore.userId"
                          text
                          size="small"
                          type="info"
                          @click="showReportDialog(2, reply.id)"
                        >
                          举报
                        </el-button>
                        <el-button
                          v-if="reply.userId === userStore.userId"
                          text
                          size="small"
                          type="danger"
                          @click="handleDeleteComment(reply.id)"
                        >
                          删除
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <Empty v-else description="暂无评论，快来发表第一条评论吧！" />
        </el-card>
      </template>
    </div>

    <!-- 举报对话框 -->
    <el-dialog
      v-model="reportDialogVisible"
      title="举报"
      width="500px"
    >
      <el-form :model="reportForm" label-width="100px">
        <el-form-item label="举报对象">
          <span>{{ reportTargetTypeNames[reportForm.targetType] }}</span>
        </el-form-item>
        <el-form-item label="举报原因" required>
          <el-select v-model="reportForm.reasonType" placeholder="请选择举报原因">
            <el-option
              v-for="reason in reportReasonTypes"
              :key="reason"
              :label="reason"
              :value="reason"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="详细说明">
          <el-input
            v-model="reportForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入举报详细说明（选填，最多500字）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reportDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReport">提交举报</el-button>
      </template>
    </el-dialog>

    <!-- 编辑话题对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑话题"
      width="700px"
    >
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="板块类型" required>
          <el-select
            v-model="editForm.boardType"
            placeholder="请选择板块类型"
            style="width: 100%"
            @change="handleEditBoardTypeChange"
          >
            <el-option label="疾病板块" :value="1" />
            <el-option label="医院评价" :value="2" />
            <el-option label="就医经验" :value="3" />
            <el-option label="康复护理" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="一级板块" required>
          <el-select
            v-model="editForm.boardLevel1"
            placeholder="请选择一级板块"
            :disabled="!editForm.boardType"
            style="width: 100%"
          >
            <el-option
              v-for="board in level1Boards"
              :key="board"
              :label="board"
              :value="board"
            />
          </el-select>
        </el-form-item>

        <el-form-item
          v-if="editForm.boardType === 1"
          :label="editForm.boardType === 1 ? '疾病分类 *' : '疾病分类 '"
        >
          <el-cascader
            v-model="editForm.diseaseCode"
            :options="diseaseOptions"
            :props="{ expandTrigger: 'hover', value: 'code', label: 'name' }"
            :placeholder="editForm.boardType === 1 ? '请选择疾病分类' : '请选择疾病分类(可选)'"
            clearable
            @change="handleEditDiseaseChange"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="标题" required>
          <el-input
            v-model="editForm.title"
            placeholder="请输入话题标题"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="内容" required>
          <el-input
            v-model="editForm.content"
            type="textarea"
            :rows="8"
            placeholder="请输入话题内容"
            maxlength="5000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="saveEdit">保存修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Star, StarFilled, View, ChatDotRound, Delete, Check, Edit } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores'
import { getTopicDetail, getCommentList, addComment, replyComment, likeComment, unlikeComment, deleteComment, deleteTopic, updateTopic, likeTopic, unlikeTopic } from '@/api/community'
import { checkCollected, addCollection, cancelCollection } from '@/api/collection'
import { createReport } from '@/api/report'
import { formatRelativeTime } from '@/utils/date'
import { getDiseaseTree } from '@/api/disease'
import Empty from '@/components/common/Empty.vue'



const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const topicDetail = ref(null)
const commentList = ref([])
const commentContent = ref('')
const replyingTo = ref(null)
const replyContent = ref('')
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const expandedComments = ref(new Set())

// 举报相关状态
const reportDialogVisible = ref(false)
const reportForm = ref({
  targetType: null,
  targetId: null,
  reasonType: '',
  reason: ''
})
const reportReasonTypes = [
  '垃圾广告',
  '违法违规',
  '色情低俗',
  '诈骗钓鱼',
  '人身攻击',
  '抄袭他人',
  '其他'
]
const reportTargetTypeNames = {
  1: '话题',
  2: '评论'
}

// 编辑话题相关状态
const editDialogVisible = ref(false)
const editForm = ref({
  boardType: undefined,
  boardLevel1: '',
  diseaseCode: '',
  title: '',
  content: ''
})

// 不再需要ID映射，因为数据库board_level1字段是VARCHAR类型，直接存储中文名称

// 板块类型对应的一级板块
const level1Boards = computed(() => {
  if (!editForm.value.boardType) return []
  switch (editForm.value.boardType) {
    case 1: // 疾病板块
      return ['心血管', '内分泌', '肿瘤', '儿科', '妇产科', '骨科', '神经科']
    case 2: // 医院评价
      return ['医院推荐', '医生推荐', '就医体验', '科室评价']
    case 3: // 就医经验
      return ['就诊经历', '用药经验', '检查经验', '手术经验']
    case 4: // 康复护理
      return ['康复训练', '护理知识', '心理健康', '营养饮食']
    default:
      return []
  }
})

// 疾病分类选项
const diseaseOptions = ref([])

// 收藏类型常量（1=医院，2=医生，3=话题）
const COLLECTION_TYPE = {
  HOSPITAL: 1,
  DOCTOR: 2,
  TOPIC: 3
}

// 判断是否是话题作者
const isAuthor = computed(() => {
  if (!userStore.isLogin || !topicDetail.value || !topicDetail.value.userId) {
    return false
  }
  return topicDetail.value.userId === userStore.userId
})

const formatTime = (time) => {
  return formatRelativeTime(time)
}

// 加载话题详情
const loadTopicDetail = async () => {
  try {
    loading.value = true
    const id = Number(route.params.id)
    const res = await getTopicDetail(id)
    topicDetail.value = res.data

    // 加载评论
    const commentRes = await getCommentList(id)
    commentList.value = commentRes.data?.list || []

    // 检查收藏状态（仅登录用户）
    if (userStore.isLogin && !isAuthor.value) {
      try {
        const collectRes = await checkCollected(COLLECTION_TYPE.TOPIC, id)
        topicDetail.value.isCollected = collectRes.data || false
      } catch (error) {
        console.error('检查收藏状态失败:', error)
        topicDetail.value.isCollected = false
      }
    }
  } catch (error) {
    console.error('加载话题详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 切换收藏状态
const toggleCollect = async () => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    router.push('/auth/login')
    return
  }

  if (isAuthor.value) {
    ElMessage.warning('不能收藏自己的话题')
    return
  }

  if (!topicDetail.value) return

  try {
    if (topicDetail.value.isCollected) {
      await cancelCollection({
        targetType: COLLECTION_TYPE.TOPIC,
        targetId: topicDetail.value.id
      })
      ElMessage.success('取消收藏成功')
      topicDetail.value.isCollected = false
    } else {
      await addCollection({
        targetType: COLLECTION_TYPE.TOPIC,
        targetId: topicDetail.value.id
      })
      ElMessage.success('收藏成功')
      topicDetail.value.isCollected = true
    }
  } catch (error) {
    console.error('切换收藏状态失败:', error)
    // 显示错误信息
    const errorMsg = error.response?.data?.message || error.message || '操作失败'
    ElMessage.error(errorMsg)
  }
}

// 点赞话题
const handleLike = async () => {
  if (!topicDetail.value) return

  try {
    if (topicDetail.value.isLiked) {
      await unlikeTopic(topicDetail.value.id)
      topicDetail.value.likeCount--
    } else {
      await likeTopic(topicDetail.value.id)
      topicDetail.value.likeCount++
    }
    topicDetail.value.isLiked = !topicDetail.value.isLiked
  } catch (error) {
    console.error('切换点赞失败:', error)
  }
}

// 点赞评论
const toggleLikeComment = async (comment) => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    return
  }

  try {
    if (comment.isLiked) {
      await unlikeComment(comment.id)
      comment.likeCount--
    } else {
      await likeComment(comment.id)
      comment.likeCount++
    }
    comment.isLiked = !comment.isLiked
  } catch (error) {
    console.error('切换评论点赞失败:', error)
  }
}

const toggleLike = () => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    return
  }
  handleLike()
}

// 删除话题
const handleDelete = async () => {
  try {
    await ElMessageBox.confirm(
      '确定删除这条话题吗？删除后无法恢复。',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteTopic(topicDetail.value.id)
    ElMessage.success('删除成功')
    router.back()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除话题失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 删除评论
const handleDeleteComment = async (commentId) => {
  try {
    await ElMessageBox.confirm(
      '确定删除这条评论吗？删除后无法恢复。',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteComment(commentId)
    ElMessage.success('删除成功')

    // 重新加载评论
    const commentRes = await getCommentList(topicDetail.value.id)
    commentList.value = commentRes.data?.list || []

    // 更新话题评论数
    if (topicDetail.value) {
      topicDetail.value.commentCount--
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 显示回复输入框
const showReplyInput = (comment) => {
  replyingTo.value = comment.id
  replyContent.value = ''
}

// 取消回复
const cancelReply = () => {
  replyingTo.value = null
  replyContent.value = ''
}

// 显示编辑对话框
const showEditDialog = async () => {
  if (!topicDetail.value) return

  // 加载疾病分类数据
  if (topicDetail.value.boardType === 1) {
    await loadDiseasesForEdit()
  }

  editForm.value = {
    boardType: topicDetail.value.boardType,
    boardLevel1: topicDetail.value.boardLevel1 || '',
    diseaseCode: topicDetail.value.diseaseCode || '',
    title: topicDetail.value.title,
    content: topicDetail.value.content
  }
  editDialogVisible.value = true
}

// 加载疾病分类（用于编辑）
const loadDiseasesForEdit = async () => {
  try {
    const { getDiseaseTree } = await import('@/api/disease')
    const res = await getDiseaseTree()
    diseaseOptions.value = transformDiseaseData(res.data)
  } catch (error) {
    console.error('加载疾病列表失败:', error)
  }
}

// 转换疾病数据
const transformDiseaseData = (diseases = []) => {
  return diseases.map(disease => ({
    id: disease.id,
    name: disease.diseaseName || disease.name,
    code: disease.diseaseCode,
    parentId: disease.parentId,
    level: disease.level,
    children: disease.children ? transformDiseaseData(disease.children) : undefined
  }))
}

// 板块类型改变
const handleEditBoardTypeChange = () => {
  // 清空下级选项
  editForm.value.boardLevel1 = ''
  editForm.value.diseaseCode = ''
}

// 疾病选择改变
const handleEditDiseaseChange = (value) => {
  const newValue = value?.length > 0 ? value[value.length - 1] : ''
  editForm.value.diseaseCode = newValue
}

// 保存编辑
const saveEdit = async () => {
  if (!editForm.value.boardType) {
    ElMessage.warning('请选择板块类型')
    return
  }
  if (!editForm.value.title.trim()) {
    ElMessage.warning('请输入话题标题')
    return
  }
  if (!editForm.value.content.trim()) {
    ElMessage.warning('请输入话题内容')
    return
  }

  try {
    submitting.value = true

    const updateData = {
      boardType: editForm.value.boardType,
      title: editForm.value.title,
      content: editForm.value.content,
      boardLevel1: editForm.value.boardLevel1 || '',
      diseaseCode: editForm.value.diseaseCode || ''
    }

    await updateTopic(topicDetail.value.id, updateData)
    ElMessage.success('修改成功')
    editDialogVisible.value = false

    // 重新加载话题详情
    const res = await getTopicDetail(topicDetail.value.id)
    topicDetail.value = res.data
  } catch (error) {
    console.error('修改话题失败:', error)
    ElMessage.error('修改失败')
  } finally {
    submitting.value = false
  }
}

// 提交回复
const submitReply = async (parentComment) => {
  if (!topicDetail.value || replyingTo.value === null) return

  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }

  try {
    submitting.value = true
    await replyComment({
      topicId: topicDetail.value.id,
      parentId: replyingTo.value,
      content: replyContent.value
    })

    ElMessage.success('回复成功')
    cancelReply()

    // 重新加载评论
    const commentRes = await getCommentList(topicDetail.value.id)
    commentList.value = commentRes.data?.list || []

    // 更新话题评论数
    if (topicDetail.value) {
      topicDetail.value.commentCount++
    }
  } catch (error) {
    console.error('提交回复失败:', error)
  } finally {
    submitting.value = false
  }
}

// 提交评论
const submitComment = async () => {
  if (!topicDetail.value) return

  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  try {
    submitting.value = true
    await addComment({
      topicId: topicDetail.value.id,
      content: commentContent.value
    })

    ElMessage.success('评论发表成功')
    commentContent.value = ''

    // 重新加载评论
    const commentRes = await getCommentList(topicDetail.value.id)
    commentList.value = commentRes.data?.list || []

    // 更新话题评论数
    if (topicDetail.value) {
      topicDetail.value.commentCount++
    }
  } catch (error) {
    console.error('提交评论失败:', error)
  } finally {
    submitting.value = false
  }
}

// 切换评论展开/收起状态
const toggleCommentExpanded = (commentId) => {
  if (expandedComments.value.has(commentId)) {
    expandedComments.value.delete(commentId)
  } else {
    expandedComments.value.add(commentId)
  }
}

// 检查评论是否展开
const isCommentExpanded = (commentId) => {
  return expandedComments.value.has(commentId)
}

// 显示举报对话框
const showReportDialog = (targetType, targetId) => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    router.push('/auth/login')
    return
  }
  reportForm.value = {
    targetType,
    targetId,
    reasonType: '',
    reason: ''
  }
  reportDialogVisible.value = true
}

// 提交举报
const submitReport = async () => {
  if (!reportForm.value.reasonType) {
    ElMessage.warning('请选择举报原因')
    return
  }

  try {
    await createReport(reportForm.value)
    ElMessage.success('举报成功，我们会尽快处理')
    reportDialogVisible.value = false
  } catch (error) {
    console.error('提交举报失败:', error)
  }
}

onMounted(() => {
  loadTopicDetail()
})
</script>

<style scoped lang="scss">
.topic-detail-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .content-wrapper {
    margin-top: 20px;
  }

  .topic-card {
    margin-bottom: 20px;

    .topic-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 20px;

      .author-info {
        display: flex;
        gap: 12px;

        h3 {
          margin: 0 0 4px;
          font-size: 16px;
          color: #303133;
        }

        .publish-time {
          font-size: 13px;
          color: #909399;
        }
      }
    }

    .topic-title {
      margin: 0 0 16px;
      font-size: 24px;
      color: #303133;
    }

    .disease-tag {
      margin-bottom: 16px;
    }

    .topic-content {
      margin-bottom: 20px;
      font-size: 15px;
      line-height: 1.8;
      color: #606266;
      white-space: pre-wrap;
    }

    .topic-stats {
      display: flex;
      gap: 24px;
      padding-top: 16px;
      border-top: 1px solid #f0f0f0;
      color: #909399;

      span {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }

  .comment-card {
    .comment-input-area {
      display: flex;
      flex-direction: column;
      gap: 12px;
      margin-bottom: 24px;

      .el-button {
        align-self: flex-end;
      }
    }

    .login-tip {
      padding: 20px;
      text-align: center;
      background: #f5f7fa;
      border-radius: 4px;
      margin-bottom: 24px;
    }

    .comment-list {
      .comment-item {
        padding: 12px;
        margin-bottom: 12px;
        background-color: #f9f9f9;
        border-radius: 4px;

        &:last-child {
          margin-bottom: 0;
        }

        .comment-main {
          display: flex;
          gap: 12px;
          margin-bottom: 12px;

          .comment-content {
            flex: 1;

            .comment-header {
              display: flex;
              justify-content: space-between;
              align-items: center;
              margin-bottom: 8px;

              .author-name {
                font-size: 14px;
                font-weight: 500;
                color: #303133;
              }

              .comment-time {
                font-size: 12px;
                color: #909399;
              }
            }

            .comment-text {
              margin: 0 0 8px;
              font-size: 14px;
              line-height: 1.6;
              color: #606266;
            }

            .comment-actions {
              display: flex;
              gap: 16px;
            }
          }
        }

        .reply-input-area {
          display: flex;
          flex-direction: column;
          gap: 8px;
          padding: 12px;
          margin-bottom: 12px;
          background-color: #f5f5f5;
          border-radius: 4px;

          .reply-actions {
            display: flex;
            justify-content: flex-end;
            gap: 8px;
          }
        }

        .reply-list-wrapper {
          padding-left: 52px;

          .reply-toggle {
            margin-bottom: 8px;
          }

          .reply-list {
            display: flex;
            flex-direction: column;
            gap: 8px;

            .reply-item {
              display: flex;
              gap: 8px;
              padding: 8px;
              background-color: #f5f5f5;
              border-radius: 4px;

              .reply-content {
                flex: 1;

                .reply-header {
                  display: flex;
                  align-items: center;
                  gap: 8px;
                  margin-bottom: 4px;

                  .author-name {
                    font-size: 13px;
                    font-weight: 500;
                    color: #303133;
                  }

                  .reply-to {
                    font-size: 12px;
                    color: #409eff;
                  }

                  .reply-time {
                    font-size: 12px;
                    color: #909399;
                  }
                }

                .reply-text {
                  margin: 0;
                  font-size: 13px;
                  line-height: 1.6;
                  color: #606266;
                }

                .reply-actions {
                  margin-top: 8px;
                }
              }
            }
          }
        }
      }
    }
  }
}
</style>