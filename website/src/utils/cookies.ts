/**
 * <p>Cookie</p>
 * @author fxbsujay@gmail.com
 * @version 21:19 2022/6/4
 */
import Cookies from 'js-cookie'
import Keys from '../constant/key'

export const getToken = (): string =>  {
    return Cookies.get(Keys.tokenKey) as string
}

export const setToken = (token: string) => {
    Cookies.set(Keys.tokenKey, token)
}

export const removeToken = () => {
    Cookies.remove(Keys.tokenKey)
}