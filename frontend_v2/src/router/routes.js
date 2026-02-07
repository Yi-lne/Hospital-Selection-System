/**
 * 路由表配置
 */
export const routes = [
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layouts/DefaultLayout.vue'),
    redirect: '/home',
    children: [
      // 首页
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页' }
      },
      // 医院相关
      {
        path: '/hospital',
        name: 'HospitalList',
        component: () => import('@/views/hospital/HospitalList.vue'),
        meta: { title: '医院列表' }
      },
      {
        path: '/hospital/:id',
        name: 'HospitalDetail',
        component: () => import('@/views/hospital/HospitalDetail.vue'),
        meta: { title: '医院详情', requiresAuth: true }
      },
      // 医生详情（仅用于从医院详情页点击医生卡片跳转）
      {
        path: '/doctor/:id',
        name: 'DoctorDetail',
        component: () => import('@/views/doctor/DoctorDetail.vue'),
        meta: { title: '医生详情', requiresAuth: true }
      },
      // 搜索结果
      {
        path: '/search',
        name: 'SearchResult',
        component: () => import('@/views/search/SearchResult.vue'),
        meta: { title: '搜索结果' }
      },
      // 社区相关
      {
        path: '/community',
        name: 'Community',
        component: () => import('@/views/community/CommunityHome.vue'),
        meta: { title: '社区交流' }
      },
      {
        path: '/community/topics',
        name: 'TopicList',
        component: () => import('@/views/community/TopicList.vue'),
        meta: { title: '话题列表' }
      },
      {
        path: '/community/topic/:id',
        name: 'TopicDetail',
        component: () => import('@/views/community/TopicDetail.vue'),
        meta: { title: '话题详情' }
      },
      {
        path: '/community/publish',
        name: 'PublishTopic',
        component: () => import('@/views/community/PublishTopic.vue'),
        meta: { title: '发布话题', requiresAuth: true }
      },
      {
        path: '/community/boards',
        name: 'BoardList',
        component: () => import('@/views/community/BoardList.vue'),
        meta: { title: '板块分类' }
      },
      // 管理员-医院管理
      {
        path: '/admin/hospitals',
        name: 'AdminHospitalList',
        component: () => import('@/views/admin/HospitalList.vue'),
        meta: { title: '医院管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: '/admin/hospital/:hospitalId/departments',
        name: 'AdminDepartmentManage',
        component: () => import('@/views/admin/DepartmentManage.vue'),
        meta: { title: '科室管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: '/admin/hospital/:hospitalId/doctors',
        name: 'AdminDoctorManage',
        component: () => import('@/views/admin/DoctorManage.vue'),
        meta: { title: '医生管理', requiresAuth: true, requiresAdmin: true }
      },
      // 用户中心
      {
        path: '/user',
        name: 'UserCenter',
        redirect: '/user/center',
        meta: { requiresAuth: true }
      },
      {
        path: '/user/center',
        name: 'UserCenter',
        component: () => import('@/views/user/UserCenter.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      },
      {
        path: '/user/profile',
        name: 'UserProfile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人资料', requiresAuth: true }
      },
      {
        path: '/user/collection',
        name: 'MyCollection',
        component: () => import('@/views/user/MyCollection.vue'),
        meta: { title: '我的收藏', requiresAuth: true }
      },
      {
        path: '/user/topics',
        name: 'MyTopics',
        component: () => import('@/views/user/MyTopics.vue'),
        meta: { title: '我的话题', requiresAuth: true }
      },
      {
        path: '/user/medical-history',
        name: 'MedicalHistory',
        component: () => import('@/views/user/MedicalHistory.vue'),
        meta: { title: '病史管理', requiresAuth: true }
      },
      {
        path: '/user/medical-history/edit/:id?',
        name: 'MedicalHistoryEdit',
        component: () => import('@/views/user/MedicalHistoryEdit.vue'),
        meta: { title: '编辑病史', requiresAuth: true }
      },
      {
        path: '/user/query-history',
        name: 'QueryHistory',
        component: () => import('@/views/user/QueryHistory.vue'),
        meta: { title: '查询历史', requiresAuth: true }
      },
      {
        path: '/user/comments',
        name: 'MyComments',
        component: () => import('@/views/user/MyComments.vue'),
        meta: { title: '我的评论', requiresAuth: true }
      },
      {
        path: '/user/notifications',
        name: 'Notifications',
        component: () => import('@/views/user/Notifications.vue'),
        meta: { title: '互动通知', requiresAuth: true }
      },
      // 消息相关
      {
        path: '/message',
        name: 'Message',
        redirect: '/message/conversations',
        meta: { requiresAuth: true }
      },
      {
        path: '/message/conversations',
        name: 'ConversationList',
        component: () => import('@/views/message/ConversationList.vue'),
        meta: { title: '私信列表', requiresAuth: true }
      },
      {
        path: '/message/chat/:userId?',
        name: 'Chat',
        component: () => import('@/views/message/Chat.vue'),
        meta: { title: '聊天', requiresAuth: true }
      }
    ]
  },
  // 认证相关
  {
    path: '/auth',
    component: () => import('@/layouts/EmptyLayout.vue'),
    children: [
      {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/auth/Login.vue'),
        meta: { title: '登录' }
      },
      {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/auth/Register.vue'),
        meta: { title: '注册' }
      }
    ]
  },
  // 错误页面
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面不存在' }
  },
  {
    path: '/500',
    name: 'ServerError',
    component: () => import('@/views/error/500.vue'),
    meta: { title: '服务器错误' }
  },
  // 重定向
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]
