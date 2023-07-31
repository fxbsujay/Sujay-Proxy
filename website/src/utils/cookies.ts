/**
 * <p>Cookie</p>
 * @author fxbsujay@gmail.com
 * @version 21:19 2022/6/4
 */
import Cookies from 'js-cookie'
import Keys from '../constant/key'
import { CookieUserInfoModel, UserInfoModel } from '@/model/UserModel'
import Copy from '@/utils/copy'

export const getUser = (): UserInfoModel | void =>  {
    const user = Cookies.get(Keys.userKey) as string
    if (user) return JSON.parse(user) as UserInfoModel
    return
}

export const setUser = (user: UserInfoModel ) => {
    Cookies.set(Keys.userKey, JSON.stringify(Copy.UnCoverSimpleClone(user,new CookieUserInfoModel)))
}

export const removeUser = () => {
    Cookies.remove(Keys.userKey)
}

export const getLang = (): string | void =>  {
    return Cookies.get(Keys.lang)
}

export const setLang = (lang: string) => {
    Cookies.set(Keys.lang, lang)
}