/**
 * @author fxbsujay@gmail.com
 */
export interface BaseModel {
  id?: number,
  index?: number,
  createdAt?: string,
  updatedAt?: string
}

/**
 * 响应体对象
 * @author fxbsujay@gmail.com
 */
export interface RootObject<T>{
  code: number
  msg: string
  data: T
}

/**
 * 系统常量
 * @author fxbsujay@gmail.com
 */
export interface Constant {
  key: string
  value: string
  desc: string
}

/**
 * 系统常量
 * @author fxbsujay@gmail.com
 */
export interface ConstantTree extends TreeNode<ConstantTree> {
  key: string,
  desc: string
}


/**
 * 响应体对象
 * @author fxbsujay@gmail.com
 */
export class QueryParams {
  page: number = 1
  limit: number = 10
}

/**
 * 响应体对象
 * @author fxbsujay@gmail.com
 */
export class PageData<T>{
  total?: number = 0
  list?: Array<T> = []
}

export interface TreeNode<T = any> {
  id: number
  name: string
  index?: number
  value?: any
  selectable?: boolean
  disabled?: boolean
  defineId?: number
  level?: number
  children?: T[]
}

export interface SimpleTreeNode extends TreeNode<SimpleTreeNode> {
  label: string
}

/**
 * 图表数据
 */
export interface EchartsRecord {
  name: string
  source?: string
  target?: string
  value?: number
  unit?: string
  data: number[]
  data2?: number[]
  xdata?: string[]
}

/**
 * 站点设置
 */
export interface SiteSetting {
  componentSize: string, // small middle large
  websiteConfigure: WebsiteConfigure
}


export interface WebsiteConfigure {
  id: number
  headTitle: string
  footTitle: string
  icon: string
  logo: string
  leftLogo: string
  rightLogo: string
}