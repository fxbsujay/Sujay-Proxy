import router from './index'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

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