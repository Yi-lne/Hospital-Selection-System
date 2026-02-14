# TypeScript to JavaScript Conversion - Complete Guide

## Status: 6/26 Files Converted (23%)

### Completed Files:
1. ✅ App.vue
2. ✅ layouts/AppHeader.vue
3. ✅ layouts/AppFooter.vue
4. ✅ layouts/DefaultLayout.vue
5. ✅ layouts/EmptyLayout.vue
6. ✅ views/error/404.vue
7. ✅ views/error/500.vue
8. ✅ components/common/Empty.vue
9. ✅ components/common/Loading.vue

### Remaining Files (17):

To complete the conversion of the remaining 17 files, follow these steps for each file:

## Manual Conversion Steps for Each File:

### Step 1: Remove `lang="ts"` from script tag
```vue
<!-- BEFORE -->
<script setup lang="ts">

<!-- AFTER -->
<script setup>
```

### Step 2: Remove type imports
```javascript
// BEFORE
import type { FormInstance, FormRules } from 'element-plus'
import type { HospitalSimple } from '@/types/hospital'

// AFTER
// Remove these lines entirely
```

### Step 3: Convert defineProps
```javascript
// BEFORE
const props = defineProps<{
  hospital: HospitalSimple
  sortPriority?: string
}>()

// AFTER
const props = defineProps({
  hospital: Object,
  sortPriority: String
})
```

### Step 4: Convert defineEmits
```javascript
// BEFORE
const emit = defineEmits<{
  success: []
  'switch-to-register': []
}>()

// AFTER
const emit = defineEmits(['success', 'switch-to-register'])
```

### Step 5: Remove type annotations from refs
```javascript
// BEFORE
const loading = ref(false)
const hospital = ref<any>(null)
const conversations = ref<Conversation[]>([])
const formRef = ref<FormInstance>()

// AFTER
const loading = ref(false)
const hospital = ref(null)
const conversations = ref([])
const formRef = ref()
```

### Step 6: Remove type annotations from functions
```javascript
// BEFORE
const handleUserCommand = (command: string) => {
const formatTime = (time: string) => {
const getAvatar = (senderId: number) => {

// AFTER
const handleUserCommand = (command) => {
const formatTime = (time) => {
const getAvatar = (senderId) => {
```

### Step 7: Remove type annotations from reactive objects
```javascript
// BEFORE
const formData = reactive({
  name: '' as string | undefined
})

// AFTER
const formData = reactive({
  name: ''
})
```

## Quick Reference for File Locations:

All files are in: `frontend/src/`

### Components:
- components/user/UserAvatar.vue
- components/user/LoginForm.vue
- components/community/TopicCard.vue
- components/hospital/HospitalCard.vue

### Views:
- views/auth/Login.vue
- views/auth/Register.vue
- views/user/MyCollection.vue
- views/user/Profile.vue
- views/user/MedicalHistory.vue
- views/user/MyTopics.vue
- views/community/TopicDetail.vue
- views/community/PublishTopic.vue
- views/doctor/DoctorDetail.vue
- views/hospital/HospitalDetail.vue
- views/message/ConversationList.vue
- views/message/Chat.vue
- views/notification/NotificationList.vue

## Validation:

After conversion, verify:
1. No `lang="ts"` in any `<script>` tags
2. No `: Type` annotations in function parameters
3. No `<Type>` generics in ref/computed calls
4. No `type { }` imports
5. No `defineProps<Type>` syntax
6. No `defineEmits<Type>` syntax

## Testing:

After conversion, test the application:
```bash
cd frontend
npm install
npm run dev
```

Check for:
- No TypeScript compilation errors
- Components render correctly
- Props are passed correctly
- Events fire correctly
- No runtime errors in browser console
