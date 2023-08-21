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
    <a-button style="float: right;margin-right: 10px" type="primary" size="large" @click="onAddOrUpdateHandle()">
      <template #icon>
        <PlusOutlined style="font-size: 20px" />
      </template>
    </a-button>
  </div>
  <a-table  bordered :data-source="dataSource" :columns="columns" :pagination="false">
  </a-table>
  <AddOrUpdate ref="addOrUpdateRef" />
</template>

<script setup lang="ts">
import { ref, onBeforeMount } from 'vue'
import { mappingListRequest } from '@/api/proxy'
import { MappingModel } from '@/model/ProxyModel'
import { columns, Wrapper } from './data'
import { PlusOutlined } from '@ant-design/icons-vue'
import AddOrUpdate from './edit.vue'

const addOrUpdateRef = ref()
const wrapper = ref<Wrapper>({
  port: ''
})
const dataSource = ref<MappingModel[]>([])

const queryList = () => {
  mappingListRequest(wrapper.value).then( res => {
    dataSource.value = res
    console.log(res)
  })
}

onBeforeMount(() => queryList())

const onAddOrUpdateHandle = (record?: MappingModel) => {
  if (record && record.serverPort) {
    addOrUpdateRef.value.init(queryList, record)
  } else {
    addOrUpdateRef.value.init(queryList)
  }

}
</script>

<style scoped lang="less">

</style>