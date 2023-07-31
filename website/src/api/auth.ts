import { LoginModel } from '@/model/AuthModel'
import { ContentType, Method } from './http'
import https from './http/https'

/**
 * <p>授权API</p>
 * @author fxbsujay@gmail.com
 * @version 14:20 2023/03/08
 */

export const loginRequest = (userInfo: LoginModel) => {
    return https().request<string>('/auth/login', Method.POST, userInfo, ContentType.json)
}
