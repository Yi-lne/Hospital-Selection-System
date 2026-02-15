/**
 * 用户模块 API 测试
 */

import { describe, it, expect, vi, beforeEach } from 'vitest'

vi.mock('@/utils/request', () => {
  const mockPost = vi.fn()
  const mockGet = vi.fn()
  const mockPut = vi.fn()

  return {
    default: {
      post: mockPost,
      get: mockGet,
      put: mockPut,
    },
  }
})

import Request from '@/utils/request'

describe('User API Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('应该成功登录', async () => {
    const mockResponse = {
      code: 200,
      message: 'success',
      data: {
        token: 'mock-jwt-token',
        userInfo: { id: 1, phone: '13800000000', nickname: '测试用户' },
      },
    }

    vi.mocked(Request.post).mockResolvedValue(mockResponse)

    const userApi = await import('@/api/user')
    const result = await userApi.login({
      phone: '13800000000',
      password: 'admin123',
    })

    expect(result).toBeDefined()
    expect(result.code).toBe(200)
    expect(result.data.token).toBeDefined()
  })

  it('应该成功获取用户信息', async () => {
    const mockResponse = {
      code: 200,
      message: 'success',
      data: { id: 1, phone: '13800000000', nickname: '测试用户' },
    }

    vi.mocked(Request.get).mockResolvedValue(mockResponse)

    const userApi = await import('@/api/user')
    const result = await userApi.getUserInfo()

    expect(result).toBeDefined()
    expect(result.code).toBe(200)
  })
})
