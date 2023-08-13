import router from './index'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import  { authStore } from  '@/store/auth'
import { getToken } from '@/utils/cookies'
/**
 * @description 路由守卫
 * @author fxbsujay@gmail.com
 * @version 0:28 2022/6/5
 */
const whiteList: Array<string> = ['/login']

/**
 * 加载条
 */
NProgress.configure({ showSpinner: false })

router.beforeEach( async (to, from, next) => {
    NProgress.start()

    const token = authStore().token || getToken()

    if (token && token.length > 0) {
        if (to.path === '/login') {
            next({path: '/'})
        } else {
            next()
        }
    } else {
        if (whiteList.indexOf(to.path) !== -1) {
            next()
        } else {
            next(`/login`)
        }
    }
    NProgress.done()
})

router.afterEach((to) => {
    NProgress.done()
})
