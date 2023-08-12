import {defineStore, StoreDefinition, _GettersTree} from 'pinia'
import { loginRequest } from '@/api/auth'
import { LoginModel } from '@/model/AuthModel'
import { setToken, removeToken } from '@/utils/cookies'
import router from '@/router'

interface AuthInfo {
    token: string
}

export const authStore: StoreDefinition<string> = defineStore('auth', {
    state: () => ({
        token: '',
    }),
    actions: {
        async login(user: LoginModel) {
            await loginRequest(user).then(token => {
                this.token = token
                setToken(token)
                router.replace('/')
            }).catch(() => Promise.reject())
        },
        logout() {
            this.token = ''
            removeToken()
            router.replace('login')
        }
    }
})