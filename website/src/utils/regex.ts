/**
 * <p>常用正则表达式封装</p>
 * @author fxbsujay@gmail.com
 * @version 16:45 2023/03/08
 */
import { Rule } from 'ant-design-vue/es/form'
import { tc }   from '@/i18n'

export class RegExpConstant {
    /**
     * 校验码
     */
    public static captcha = '^[0-9a-zA-Z]{5,5}$'
    /**
     * 手机验证码
     */
    public static code = '^[0-9a-zA-Z]{6,6}$'
    /**
     * 邮箱
     */
    public static email = '\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}'
    /**
     * 用户名
     */
    public static username = '^[0-9a-zA-Z]{4,20}$'
    /**
     * 密码
     */
    public static password = '^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{4,20}$'
    /**
     * 手机号
     */
    public static phone = '^1[3|4|5|7|8][0-9]{9}$'
    /**
     * 身份证
     */
    public static idCard = '\\d{17}[\\d|x]|\\d{15}'
}

/**
 * 验证方法
 *
 * @returns {boolean}
 * @param value    内容
 * @param reg      表达式
 */
export const checkRegExp = (value: string, reg: string) => {
    if (value) {
        let regExp = new RegExp(reg);
        return regExp.test(value);
    } else {
        return false
    }
}

/**
 * 表达验证
 */
export const validateUsername = (rule: Rule, value: string) => {
    if (rule.required && !checkRegExp(value,RegExpConstant.username)) {
        return Promise.reject(tc('pleaseEnter', { key: 'account' }))
    } else {
        return Promise.resolve();
    }
}

export const validatePassword = (rule: Rule, value: string) => {
    if (rule.required && !checkRegExp(value,RegExpConstant.password)) {
        return Promise.reject(tc('pleaseEnter', { key: 'password' }))
    } else {
        return Promise.resolve();
    }
}

export const validatePhone = (rule: Rule, value: string) => {
    if (rule.required && !checkRegExp(value,RegExpConstant.phone)) {
        return Promise.reject(tc('pleaseEnter', { key: 'phoneNumber' }))
    } else {
        return Promise.resolve();
    }
}

export const validateEmail = (rule: Rule, value: string) => {
    if (rule.required && rule.required && !checkRegExp(value,RegExpConstant.email)) {
        return Promise.reject(tc('pleaseEnter', { key: 'email' }))
    } else {
        return Promise.resolve();
    }
}

export const validateCaptcha = (rule: Rule, value: string) => {
    if (rule.required && rule.required && !checkRegExp(value,RegExpConstant.captcha)) {
        return Promise.reject(tc('pleaseEnter', { key: 'checkCode' }))
    } else {
        return Promise.resolve();
    }
}
export const validateCode = (rule: any, value: string) => {
    if (rule.required && rule.required && !checkRegExp(value,RegExpConstant.code)) {
        return Promise.reject(tc('pleaseEnter', { key: 'verificationCode' }))
    } else {
        return Promise.resolve();
    }
}