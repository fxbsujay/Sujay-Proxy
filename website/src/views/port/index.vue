<template>
  <div class="wrapper">
    <a-input-search
        v-model:value="wrapper.port"
        placeholder="代理端口"
        enter-button
        style="width: 300px"
        size="large"
        @search="queryList"
    />
  </div>
  <a-table  bordered :data-source="dataSource" :columns="columns" :pagination="false">
  </a-table>
</template>

<script setup lang="ts">
import { ref, onBeforeMount } from 'vue'
import { mappingListRequest } from '@/api/proxy'
import { ProxyModel } from '@/model/ProxyModel'
import { columns, Wrapper } from './data'

const wrapper = ref<Wrapper>({
  port: ''
})
const dataSource = ref<ProxyModel[]>([])

const queryList = () => {
  mappingListRequest(wrapper.value).then( res => {
    dataSource.value = res
    console.log(res)
  })
}
onBeforeMount(() => queryList())
</script>

<style scoped lang="less">

</style>