<template>
  <div class="area-selector">
    <el-cascader
      v-model="selectedArea"
      :options="areaOptions"
      :props="cascaderProps"
      :placeholder="placeholder"
      :clearable="clearable"
      :disabled="disabled"
      @change="handleChange"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAreaTree } from '@/api/area'

const props = defineProps({
  modelValue: {
    type: [Array, String],
    default: () => []
  },
  placeholder: {
    type: String,
    default: '请选择地区'
  },
  clearable: {
    type: Boolean,
    default: true
  },
  disabled: {
    type: Boolean,
    default: false
  },
  level: {
    type: Number,
    default: 3 // 1=省, 2=市, 3=区
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

const areaOptions = ref([])
const selectedArea = ref(props.modelValue)

const cascaderProps = {
  label: 'name',
  value: 'code',
  children: 'children',
  checkStrictly: true,
  emitPath: true
}

// 加载地区数据
const loadAreaData = async () => {
  try {
    const res = await getAreaTree()
    let data = res.data || []

    // 根据级别过滤数据
    if (props.level < 3) {
      data = filterByLevel(data, props.level)
    }

    areaOptions.value = data
  } catch (error) {
    console.error('加载地区数据失败:', error)
  }
}

// 根据级别过滤数据
const filterByLevel = (data, level) => {
  return data.map(item => {
    if (level === 1) {
      return { ...item, children: undefined }
    } else if (level === 2 && item.children) {
      return {
        ...item,
        children: item.children.map(child => ({
          ...child,
          children: undefined
        }))
      }
    }
    return item
  })
}

// 处理选择变化
const handleChange = (value) => {
  emit('update:modelValue', value)
  emit('change', value)
}

onMounted(() => {
  loadAreaData()
})
</script>

<style scoped lang="scss">
.area-selector {
  .el-cascader {
    width: 100%;
  }
}
</style>
