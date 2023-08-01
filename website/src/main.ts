import { createApp } from 'vue'
import App from  './App.vue'
import router from './router'
import '@/styles/index.less'
import '@/router/permission'
import 'virtual:svg-icons-register'
import Antd from 'ant-design-vue'

const app = createApp(App)
    .use(router)
    .use(Antd)


app.mount('#app')


