import { createRouter, createWebHistory, RouteRecordRaw, RouterOptions } from 'vue-router'
import BasicLayout from '@/layout/BasicLayout.vue'
import { RouteModel } from '@/model/AuthModel'

/**
 * <p>路由</p>
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
        component: BasicLayout,
        children: [
            {
                path: '/login',
                component: () => import('@/views/auth/login.vue'),
                name: 'login',
            }
        ]
    }

]


/**
 * 需要放到菜单选项中的不需要判断权限的路由
 */
export const commonRoutes: Array<RouteModel> = []

const routes: Array<RouteRecordRaw> = [
    ...constantRoutes.concat(commonRoutes) as RouteRecordRaw[],
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