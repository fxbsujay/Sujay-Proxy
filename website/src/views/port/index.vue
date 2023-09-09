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
        <a-popconfirm title="你确定要删除代理吗" @confirm="onDeleteHandle(record)">
          <a-button danger  type="link" size="middle">
            删除
          </a-button>
        </a-popconfirm>
        <a-popconfirm title="你确定要重启吗" @confirm="onStartOrClose(record, true)">
          <a-button type="link" size="middle">
            重启
          </a-button>
        </a-popconfirm>
        <a-popconfirm title="你确定要关闭吗" @confirm="onStartOrClose(record, false)">
          <a-button type="link" size="middle">
            关闭
          </a-button>
        </a-popconfirm>
      </template>
    </template>
  </a-table>
  <AddOrUpdate ref="addOrUpdateRef" />
</template>

<script setup lang="ts">
import { ref, onBeforeMount } from 'vue'
import {
  mappingListRequest,
  mappingDeleteRequest,
  closeProxyServerRequest,
  startProxyServerRequest
} from '@/api/proxy'
import { notification } from 'ant-design-vue'
import { MappingModel } from '@/model/ProxyModel'
import { columns, Wrapper } from './data'
import { PlusOutlined, DeleteOutlined, CloseSquareOutlined } from '@ant-design/icons-vue'
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

    notification.success({
      message: '删除代理成功',
      description: `localhost:${record.serverPort}`
    })
  })
}

const onStartOrClose = (record: MappingModel, isStart: boolean) => {
  if (isStart) {
    startProxyServerRequest({ port: record.serverPort }).then( () => {
      queryList()
      notification.success({
        message: '代理服务已重启',
        description: '重启为异步任务，请稍后刷新列表查看运行状态'
      })
    })
  } else {
    closeProxyServerRequest({ port: record.serverPort }).then( () => {
      queryList()
      notification.success({
        message: '代理服义器已关闭',
      })
    })
  }
}
</script>

<style scoped lang="less">

</style>