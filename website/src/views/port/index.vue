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
  <a-table bordered :data-source="dataSource" :columns="columns" :pagination="false">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'operation'">
        <a-popconfirm title="你确定要删除吗" @confirm="onDeleteHandle(record)">
          <a-button danger  type="link" size="large">
            <template #icon>
              <DeleteOutlined />
            </template>
          </a-button>
        </a-popconfirm>
      </template>
    </template>
  </a-table>
  <AddOrUpdate ref="addOrUpdateRef" />
</template>

<script setup lang="ts">
import { ref, onBeforeMount } from 'vue'
import { mappingListRequest, mappingDeleteRequest } from '@/api/proxy'
import { MappingModel } from '@/model/ProxyModel'
import { columns, Wrapper } from './data'
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import AddOrUpdate from './edit.vue'

const addOrUpdateRef = ref()
const wrapper = ref<Wrapper>({
  port: ''
})
const dataSource = ref<Array<MappingModel>>([])

const queryList = () => {
  mappingListRequest(wrapper.value).then( res => {
    dataSource.value = res
  })
}

onBeforeMount(() => queryList())

const onAddOrUpdateHandle = () => {
  addOrUpdateRef.value.init(queryList)
}

const onDeleteHandle = (record: MappingModel) => {
  mappingDeleteRequest(record.serverPort).then( () => {
    queryList()
  })
}
</script>

<style scoped lang="less">

</style>