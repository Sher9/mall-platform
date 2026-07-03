import { get } from '@/utils/request'

/** 角色信息 */
export interface Role {
  id: number
  roleName: string
  roleCode: string
  description?: string
  status: 0 | 1
  createTime: string
}

/** 获取角色列表（全量） */
export function getRoleListApi(): Promise<Role[]> {
  return get<Role[]>('/role/all')
}

/** 获取角色分页列表 */
export function getRolePageApi(params: {
  pageNum: number
  pageSize: number
  roleName?: string
}) {
  return get<{ records: Role[]; total: number }>('/role', params)
}
