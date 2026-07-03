import { get, post } from '@/utils/request'
import type { MenuItem } from '@/types'

/** 获取所有菜单列表（用于构建树） */
export function getMenuListApi(): Promise<MenuItem[]> {
  return get<MenuItem[]>('/menu/all')
}

/** 获取角色已分配的菜单ID列表 */
export function getRoleMenusApi(roleId: number): Promise<number[]> {
  return get<number[]>(`/role/${roleId}/menus`)
}

/** 保存角色菜单分配 */
export function saveRoleMenusApi(roleId: number, menuIds: number[]): Promise<void> {
  return post<void>(`/role/${roleId}/menus`, menuIds)
}
