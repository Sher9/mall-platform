import { get, post, put, del } from '@/utils/request'
import type { SystemUser, PageResult } from '@/types'

/** 系统用户列表分页参数 */
interface AdminUserQuery {
  page: number
  size: number
  keyword?: string
  status?: number
}

/** 系统用户列表 */
export function getAdminUserListApi(params: AdminUserQuery): Promise<PageResult<SystemUser>> {
  return get<PageResult<SystemUser>>('/user', {
    pageNum: params.page,
    pageSize: params.size,
    keyword: params.keyword,
    status: params.status
  })
}

/** 新增系统用户 */
export function addAdminUserApi(data: Partial<SystemUser> & { password: string }): Promise<SystemUser> {
  return post<SystemUser>('/user', data)
}

/** 更新系统用户 */
export function updateAdminUserApi(data: Partial<SystemUser>): Promise<SystemUser> {
  return put<SystemUser>('/user', data)
}

/** 删除系统用户 */
export function deleteAdminUserApi(id: number): Promise<void> {
  return del<void>(`/user/${id}`)
}

/** 更新系统用户状态 */
export function updateAdminUserStatusApi(id: number, status: 0 | 1): Promise<SystemUser> {
  return put<SystemUser>('/user/status', { id, status })
}

/** 重置密码 */
export function resetAdminUserPasswordApi(id: number, password: string): Promise<void> {
  return put<void>('/user/reset-password', { id, password })
}

/** 分配角色 */
export function assignAdminUserRoleApi(userId: number, roleIds: number[]): Promise<void> {
  return put<void>('/user/roles', { userId, roleIds })
}
