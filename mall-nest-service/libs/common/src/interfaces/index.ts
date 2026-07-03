// 通用接口定义
export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
}

export interface PageParams {
  page: number;
  size: number;
  [key: string]: any;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

export interface UserInfo {
  id: number;
  username: string;
  nickname: string;
  avatar?: string;
  phone?: string;
  email?: string;
  roles: string[];
  permissions: string[];
}

export interface LoginResult {
  accessToken: string;
  tokenType: string;
  userId: number;
  username: string;
  nickname: string;
  avatar?: string;
  email?: string;
  phone?: string;
  roles: string[];
  permissions: string[];
}
