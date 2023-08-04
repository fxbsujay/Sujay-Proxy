import { RouteComponent } from 'vue-router'

/**
 * 登录对象
 * @author fxbsujay@gmail.com
 */
export interface LoginModel {
    // 用户名
    username: string
    // 密码
    password: string
}

/**
 * 菜单对象
 * @author fxbsujay@gmail.com
 */
export interface RouteModel {
    path: string
    redirect?: string
    component?: string | RouteComponent
    name?: string
    meta?: Meta
    order?: number
    hidden?: boolean                // 是否在菜单选项中显示
    children?: RouteModel[]
}

export interface Meta {
    title: string                   // 标题名称
    i18n?: boolean                  // 是否需要国际化
    icon?: string                   // 图标
    roles?: string[]                // 权限数组
}
