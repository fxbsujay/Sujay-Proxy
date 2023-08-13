<template>
  <div class="wrapper">
    <a-input-search
        v-model:value="wrapper.name"
        placeholder="客户端名称"
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
import { clientListRequest } from '@/api/client'
import { ClientModel } from '@/model/ClientModel'
import { columns, Wrapper } from './data'

const wrapper = ref<Wrapper>({
  name: ''
})
const dataSource = ref<ClientModel[]>([])

const queryList = () => {
  clientListRequest(wrapper.value).then( res => {
    dataSource.value = res
    console.log(res)
  })
}
onBeforeMount(() => queryList())
</script>

<style scoped lang="less">

</style>