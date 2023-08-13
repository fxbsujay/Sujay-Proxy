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