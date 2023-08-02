<template>
  <div v-for="route in routes">
    <a-sub-menu v-if="route.children && route.children.length > 1" :title="route.meta.title" :key="route.name">
      <layout-menu :routes="route.children"/>
    </a-sub-menu>
    <a-menu-item v-else-if="route.children && route.children.length === 1" :key="route.children[0].name">
      <router-link class="nav-link" :to="route.path">
        {{ route.children[0].meta.title }}
      </router-link>
    </a-menu-item>
    <a-menu-item v-else :key="route.name">
      <router-link class="nav-link"  :to="route.path">{{ route.meta ? route.meta.title : route.name }}</router-link>
    </a-menu-item>
  </div>
</template>
<script setup lang="ts">

/**
 * @description 菜单组件
 * @author fxbsujay@gmail.com
 * @version 17:50 2023/2/27
 */
import { toRef } from 'vue'
import { RouteModel } from '@/model/AuthModel'
interface Props {
  routes: RouteModel[]
}

const props = defineProps<Props>()
const routes = toRef(props, 'routes')
</script>

<style scoped>

</style>