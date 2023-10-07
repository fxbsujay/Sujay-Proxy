<template>
  <a-layout class="layout">
    <a-layout-header class="s-layout-header">
      <div class="logo">
        PROXY
      </div>
      <a-menu
          v-model:selectedKeys="selectedKeyList"
          theme="light"
          mode="horizontal"
          :style="{ lineHeight: '64px' }"
      >
        <LayoutMenu :routes="routes" />
      </a-menu>
      <div class="logout" @click="logoutSubmit">
        LOGOUT
      </div>
    </a-layout-header>
    <a-layout-content class="s-layout-content">
      <router-view/>
    </a-layout-content>
    <a-layout-footer style="text-align: center">
      Simple-Proxy ©2023 Created by <a href="http://xuebin.xyz">Fan XueBin</a>
    </a-layout-footer>
  </a-layout>
</template>
<script setup lang="ts">

import { useRoute} from 'vue-router'
import { ref, watch } from 'vue'
import { commonRoutes } from '@/router'
import { RouteModel } from '@/model/AuthModel'
import LayoutMenu from '@/layout/LayoutMenu.vue'
import { authStore } from '@/store/auth'

const currentRoute = useRoute()
const selectedKeyList = ref<string[]>([])
const routes = ref<Array<RouteModel>>(generateLayoutMenu(commonRoutes.children!))

const logoutSubmit = () => {
  authStore().logout()
}
watch(currentRoute, () => {
  selectedKeyList.value = [currentRoute.name as string]
},{ deep: true, immediate: true})


function generateLayoutMenu (routes: RouteModel[]): RouteModel[] {
  const res: RouteModel[] = []
  for (let route of routes) {
    const r = { ...route }
    if (typeof r.hidden === 'boolean' && r.hidden) {
      continue
    }
    if (r.children) {
      r.children = generateLayoutMenu(r.children)
    }
    res.push(r)
  }
  return res
}

</script>
