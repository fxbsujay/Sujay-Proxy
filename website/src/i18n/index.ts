import { createI18n } from 'vue-i18n'
import { getLang } from '@/utils/cookies'
import cn from './cn'
import en from './en'
import zhCN from 'ant-design-vue/lib/locale-provider/zh_CN'
import enUS from 'ant-design-vue/lib/locale-provider/en_US'

export const localeType = {
    ZN: {
        antLocale: zhCN,
        value: 'zh_CN',
        label: '简体中文'
    },
    EN: {
        antLocale: enUS,
        value: 'en_US',
        label: 'English'
    },
}

export const messages = {
    [localeType.EN.value]: {
        ...en
    },
    [localeType.ZN.value]: {
        ...cn
    }
}

const getCurrentLanguage = (): string => {
    const UALang = navigator.language
    return getLang() ? getLang() as string : UALang.indexOf('zh') !== -1 ? localeType.ZN.value : localeType.EN.value
}

// 文档 https://vue-i18n.intlify.dev/
const i18n = createI18n({
    locale: getCurrentLanguage(),
    fallbackLocale: localeType.ZN.value,
    messages
})

export const t = i18n.global.t
export const tc = i18n.global.tc
export default i18n