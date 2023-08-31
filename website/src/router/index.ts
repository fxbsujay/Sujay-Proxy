import { createRouter, createWebHistory, RouteRecordRaw, RouterOptions } from 'vue-router'
import Layout from '@/layout/index.vue'
import { RouteModel } from '@/model/AuthModel'

/**
 * <p>路由</p>
 *
 * @author fxbsujay@gmail.com
 * @version 13:24 2022/6/3
 */

/**
 * 不需要放到菜单选项中的路由
 */
export const constantRoutes: Array<RouteModel> = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/auth/login.vue')
    }
]

/**
 * 需要放到菜单选项中的不需要判断权限的路由
 */
export const commonRoutes: RouteModel = {
    path: '/',
    name: 'Home',
    redirect: 'client',
    component: Layout,
    children: [
        {
            path: '/client',
            component: () => import('@/views/client/index.vue'),
            name: 'Client',
            meta: {
                title: '客户端'
            }
        },
        {
            path: '/port',
            component: () => import('@/views/port/index.vue'),
            name: 'Port',
            meta: {
                title: '端口池'
            }
        }
    ]
}

const routes: Array<RouteRecordRaw> = [
    commonRoutes as RouteRecordRaw,
    ...constantRoutes as RouteRecordRaw[],
]

const router = createRouter(<RouterOptions> {
    history: createWebHistory(),
    routes
})

export default router

const filterAsyncRoutes = (routes: RouteModel[]): RouteModel[]  => {
    const res: RouteModel[] = []
    routes.forEach(route => {
        const r = { ...route }
        if (r.children) {
            r.children = filterAsyncRoutes(r.children)
        }
        res.push(r)
    })
    return res
}