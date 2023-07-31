<template>
  <div class="page-container">
    <a-row>
      <a-card class="butter-card">
        <slot name="header">
          <a-select
              style="width: 150px;"
              v-model:value="wrapper.type"
              class="query-item"
          >
            <a-select-option v-for="item in dateOptions" :key="item['value']" :value="item['value']">
              {{ $t(item.label) }}
            </a-select-option>
          </a-select>
          <a-range-picker
              :style="{ width: dateOptions.find( item => item.value === wrapper.type).showTime ? '400px' : '250px' }"
              separator="~"
              :allowClear="false"
              v-model:value="time"
              :picker="dateOptions.find( item => item.value === wrapper.type).picker"
              :showTime="dateOptions.find( item => item.value === wrapper.type).showTime"
              class="query-item"
              valueFormat="YYYY-MM-DD HH:mm:ss"
              @change="timeChangeHandle"
          />
          <a-select
              v-if="!disableVersionSelect"
              allowClear
              class="query-item"
              :placeholder="$tc('pleaseChoose', { key: 'version.version' })"
              v-model:value="wrapper.versionV2Id"
              :options="versionList"
              :fieldNames="{ label: 'name', value: 'id' }"
          />
          <a-select
              v-if="!disableEnterpriseSelect && multipleEnterprise"
              mode="multiple"
              class="query-item"
              :placeholder="$tc('pleaseChoose', { key: 'enterprise.enterprise' })"
              v-model:value="wrapper.enterpriseIds"
              :options="enterpriseList"
              :fieldNames="{ label: 'name', value: 'id' }"
          />
          <a-select
              v-else-if="!disableEnterpriseSelect"
              class="query-item"
              :placeholder="$tc('pleaseChoose', { key: 'enterprise.enterprise' })"
              v-model:value="wrapper.enterpriseId"
              :options="enterpriseList"
              :fieldNames="{ label: 'name', value: 'id' }"
          />
          <slot name="buttons" v-bind="{ wrapper }"/>
          <a-button
              class="butter-divide freshen-button"
              shape="circle"
              @click="searchHandle({ ...wrapper, enterpriseId:  multipleEnterprise ? wrapper.enterpriseIds?.toString() : wrapper.enterpriseId })"
          >
            <template #icon><RedoOutlined :rotate="270"/></template>
          </a-button>
        </slot>
      </a-card>
    </a-row>
    <a-row style="height: 100%;">
      <div class="content-card">
        <slot />
      </div>
    </a-row>
  </div>
</template>

<script setup lang="ts">

import {
  RedoOutlined
} from '@ant-design/icons-vue'
import { reactive, ref, defineExpose, onBeforeMount, } from 'vue'
import { VersionModel } from '@/model/plan/VersionModel'
import { EnterpriseModel } from '@/model/sys/EnterpriseModel'
import { enterpriseListRequest } from '@/api/modules/sys/enterprise'
import { versionListRequest } from '@/api/modules/plan/version'
import { getNowString, getYearStartTimeString } from '@/utils/date'
import store from '@/store'
import { t } from '@/i18n'

export interface Wrapper {
  type: string
  versionV2Id?: number
  versionId?: number
  enterpriseId?: number
  enterpriseIds?: number[]
  startTime: string
  endTime: string
}

interface Props {
  disableVersionSelect?: boolean
  disableEnterpriseSelect?: boolean
  multipleEnterprise?: boolean
  searchHandle?: (wrapper: Wrapper) => void
}

const props = defineProps<Props>()

const dateOptions =  [
  { value: 'hour', label: 'dateType.hour', picker: '', showTime: true },
  { value: 'day', label: 'dateType.day', picker: '', showTime: false },
  { value: 'month', label: 'dateType.month', picker: 'month', showTime: false },
  { value: 'quarter', label: 'dateType.quarter', picker: 'quarter', showTime: false },
  { value: 'year', label: 'dateType.year', picker: 'year', showTime: false }
]

const time = ref<[string,string]>([getYearStartTimeString(), getNowString()])
const versionList = ref<Array<VersionModel>>([])
const enterpriseList = ref<Array<EnterpriseModel>>([])
const wrapper = reactive<Wrapper>({
  enterpriseId: undefined,
  type: 'month',
  startTime: time.value[0],
  versionId: 1,
  versionV2Id: undefined,
  endTime: time.value[1]
})

versionListRequest({}).then( res => {
  versionList.value = res
})

enterpriseListRequest({}).then( res => {
  enterpriseList.value = res
  if (!props.disableEnterpriseSelect && !props.multipleEnterprise) {
    wrapper.enterpriseId = store.getters.user.enterpriseId
  } else if (!props.disableEnterpriseSelect) {
    wrapper.enterpriseIds = [store.getters.user.enterpriseId]
  }
})

const timeChangeHandle = (time: [string,string]) => {
  wrapper.startTime = time ? time[0] : getYearStartTimeString()
  wrapper.endTime = time ? time[1] : getNowString()
}

onBeforeMount(() => {
 if (props.searchHandle) {
   props.searchHandle(props.disableEnterpriseSelect ? wrapper : { ...wrapper, enterpriseId: store.getters.user.enterpriseId })
 }
})

defineExpose({ wrapper })
</script>

<style  lang="less">

.page-container {
  min-width: 950px;
  .butter-card {
    width: 100%;
    .ant-card-body {
      padding: 11px 20px 0 20px;
    }
    .query-item {
      width: 220px;
      margin-right: 20px;
      margin-bottom: 11px;
    }
    .butter-item {
      margin-right: 20px;
      margin-bottom: 10px;
    }
    .freshen-button {
      color: #fff;
      background: #13ce66;
    }
  }
  .content-card {
    flex-grow: 1;
    height: 100%;
    margin-top: 8px;
  }
}
</style>