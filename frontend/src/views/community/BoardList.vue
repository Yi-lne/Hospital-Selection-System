<template>
  <div class="board-list-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">板块分类</span>
      </template>
    </el-page-header>

    <el-card v-loading="loading" class="board-card">
      <!-- 一级板块 -->
      <div class="level1-section">
        <h2>疾病板块</h2>
        <el-row :gutter="20">
          <el-col v-for="board in level1Boards" :key="board.id" :xs="24" :sm="12" :md="8">
            <div class="board-item level1" @click="selectBoard(board)">
              <div class="board-icon">
                <el-icon :size="32" :color="getBoardColor(board.diseaseName)">
                  <component :is="getBoardIcon(board.diseaseName)" />
                </el-icon>
              </div>
              <div class="board-info">
                <h3>{{ board.diseaseName }}</h3>
                <p>{{ board.children?.length || 0 }} 个二级板块</p>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 特殊板块 -->
      <div class="special-section">
        <h2>特色板块</h2>
        <el-row :gutter="20">
          <el-col
            v-for="special in specialBoards"
            :key="special.id"
            :xs="24"
            :sm="12"
            :md="8"
          >
            <div class="board-item special" @click="goToSpecialBoard(special)">
              <div class="board-icon">
                <el-icon :size="32" :color="special.color">
                  <component :is="special.icon" />
                </el-icon>
              </div>
              <div class="board-info">
                <h3>{{ special.name }}</h3>
                <p>{{ special.description }}</p>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- 二级板块对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="selectedBoard?.diseaseName"
      width="600px"
    >
      <div v-if="selectedBoard?.children" class="level2-list">
        <div
          v-for="child in selectedBoard.children"
          :key="child.id"
          class="level2-item"
          @click="goToBoard(child)"
        >
          <el-icon><Right /></el-icon>
          <span>{{ child.diseaseName }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Right, TrendCharts, Star, ChatDotRound, Management,
  FirstAidKit, Stethoscope, Monitor, Pills, Operation
} from '@element-plus/icons-vue'
import { getDiseaseTree } from '@/api/disease'

const router = useRouter()

const loading = ref(false)
const diseaseTree = ref([])
const dialogVisible = ref(false)
const selectedBoard = ref(null)

// 特色板块
const specialBoards = ref([
  {
    id: 'hospital-review',
    name: '医院评价区',
    description: '分享就医体验',
    icon: Star,
    color: '#f56c6c',
    boardType: 2
  },
  {
    id: 'experience',
    name: '就医经验区',
    description: '交流治疗心得',
    icon: ChatDotRound,
    color: '#e6a23c',
    boardType: 3
  },
  {
    id: 'rehabilitation',
    name: '康复护理区',
    description: '康复经验分享',
    icon: TrendCharts,
    color: '#67c23a',
    boardType: 4
  }
])

// 一级板块（疾病分类）
const level1Boards = ref([])

// 获取板块图标
const getBoardIcon = (name) => {
  const icons = {
    '心血管疾病': FirstAidKit,
    '内分泌代谢': Pills,
    '肿瘤科': TrendCharts,
    '儿科': Stethoscope,
    '妇产科': Monitor,
    '骨科': Operation,
    '神经科': Management,
    '呼吸科': FirstAidKit,
    '消化科': Pills
  }
  return icons[name] || ChatDotRound
}

// 获取板块颜色
const getBoardColor = (name) => {
  const colors = {
    '心血管疾病': '#f56c6c',
    '内分泌代谢': '#67c23a',
    '肿瘤科': '#e6a23c',
    '儿科': '#409eff',
    '妇产科': '#f56c6c',
    '骨科': '#909399',
    '神经科': '#606266',
    '呼吸科': '#67c23a',
    '消化科': '#e6a23c'
  }
  return colors[name] || '#409eff'
}

// 加载疾病分类树
const loadDiseaseTree = async () => {
  try {
    loading.value = true
    const res = await getDiseaseTree()
    diseaseTree.value = res.data || []
    level1Boards.value = diseaseTree.value.filter(item => item.parentId === 0)
  } catch (error) {
    console.error('Failed to load disease tree:', error)
    ElMessage.error('加载板块失败')
  } finally {
    loading.value = false
  }
}

// 选择一级板块
const selectBoard = (board) => {
  if (board.children && board.children.length > 0) {
    selectedBoard.value = board
    dialogVisible.value = true
  } else {
    goToBoard(board)
  }
}

// 跳转到板块话题列表
const goToBoard = (board) => {
  dialogVisible.value = false
  router.push({
    path: '/community',
    query: {
      boardLevel2: board.diseaseName
    }
  })
}

// 跳转到特色板块
const goToSpecialBoard = (special) => {
  router.push({
    path: '/community',
    query: {
      boardType: special.boardType
    }
  })
}

onMounted(() => {
  loadDiseaseTree()
})
</script>

<style scoped lang="scss">
.board-list-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .board-card {
    margin-top: 20px;
  }

  h2 {
    margin: 0 0 20px;
    font-size: 18px;
    font-weight: 600;
    color: #303133;
  }

  .level1-section,
  .special-section {
    margin-bottom: 40px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .board-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    background: #fff;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    margin-bottom: 16px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      border-color: #409eff;
      box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
      transform: translateY(-2px);
    }

    &.level1 {
      border-left: 4px solid #409eff;
    }

    &.special {
      border-left: 4px solid #67c23a;
    }

    .board-icon {
      flex-shrink: 0;
    }

    .board-info {
      flex: 1;

      h3 {
        margin: 0 0 8px;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }

      p {
        margin: 0;
        font-size: 13px;
        color: #909399;
      }
    }
  }

  .level2-list {
    display: flex;
    flex-direction: column;
    gap: 8px;

    .level2-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px 16px;
      background: #f5f7fa;
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background: #e6f7ff;
        color: #409eff;
      }
    }
  }
}
</style>
