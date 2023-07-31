
export interface UserModel {
    info: UserInfoModel,
    roles: string[]
}
/**
 * 用户对象
 * @author fxbsujay@gmail.com
 */
export interface UserInfoModel {
    id?: number
    name: string
    phone: string
    activation?: number
    admin: boolean
    password?: string
    approve: boolean
    code: string
    deptIds: []
    disable: boolean
    duty: string
    email: string
    enterpriseId?: number
    enterpriseLegal: boolean
    enterpriseLimit?: number
    enterpriseName: string
    face: string
    invalidTime: string
    legal?: number
    post: string
}

/**
 * 用户对象
 * @author fxbsujay@gmail.com
 */
export class CookieUserInfoModel {
    id: number = 0
    name: string = ''
    phone: string = ''
    activation: number = 0
    admin: boolean = false
    approve: boolean = false
    code: string = ''
    deptIds: [] = []
    disable: boolean =  true
    duty: string = ''
    email: string = ''
    enterpriseId: number = 0
    enterpriseLegal: boolean = false
    enterpriseLimit: number = 0
    enterpriseName: string = ''
    invalidTime: string = ''
    legal: number = 0
    post: string = ''
}