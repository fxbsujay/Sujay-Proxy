<template>
  <a-modal
      class="basic-form"
      v-model:open="open"
      :maskClosable="false"
      :title="title"
      :width="500"
      :centered="true"
      :confirm-loading="loading"
      @cancel="onCloseHandle"
      @ok="onAddOrUpdateSubmitHandle"
  >
    <a-form v-bind="formItemLayout">
      <a-form-item label="服务端端口" v-bind="validateInfos.serverPort">
        <a-input :disabled="loading" v-model:value="formState.serverPort" />
      </a-form-item>
      <a-form-item label="代理客户端IP" v-bind="validateInfos.clientIp">
        <a-input :disabled="loading" v-model:value="formState.clientIp" />
      </a-form-item>
      <a-form-item label="代理客户端端口" v-bind="validateInfos.clientPort">
        <a-input :disabled="loading" v-model:value="formState.clientPort" />
      </a-form-item>
      <a-form-item label="协议" v-bind="validateInfos.protocol">
        <a-select
            :disabled="loading"
            v-model:value="formState.protocol"
            :options="ProtocolConstant"
        />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue'
import { mappingSaveRequest, mappingUpdateRequest } from '@/api/proxy'
import { MappingModel } from '@/model/ProxyModel'
import { Form, notification } from 'ant-design-vue'
import { ProtocolConstant } from '@/constant'
import { defaultForm, ruleList } from './data'
import Copy from '@/utils/copy'

const useForm = Form.useForm
const formItemLayout = {
  labelCol: { span: 7 },
  wrapperCol: { span: 24 }
}

const open = ref<boolean>(false)
const loading = ref<boolean>(false)
const title = ref<string>('新建')
const formState = ref<MappingModel>(defaultForm)

const { resetFields, validate, validateInfos, clearValidate } = useForm(formState,ruleList)

let callbackFunc = reactive<any>(null)

const onAddOrUpdateSubmitHandle = () => {
  validate().then(() => {
    loading.value = true
    if (formState.value.id) {
      mappingUpdateRequest(formState.value).then(() => {
        successHandle()
      }).catch(() => loading.value = false)
    } else {
      mappingSaveRequest(formState.value).then(() => {
        successHandle()
      }).catch(() => loading.value = false)
    }

  })
}

const init = (callback: () => void, data: MappingModel) => {
  callbackFunc = callback

  if (data && data.id) {
    Copy.SimpleClone(data, formState)
    title.value = '编辑'
  } else {
    Copy.DeepClone(defaultForm, formState)
    resetFields()
    title.value = '新建'
  }

  open.value = true
  nextTick(() => clearValidate())
}

const onCloseHandle = () => {
  loading.value = false
  open.value = false
  clearValidate()
}

function successHandle () {
  onCloseHandle()

  notification.success({
    message: '端口映射成功',
    description: `serverIp:${formState.value.serverPort} -> ${formState.value.clientIp}:${formState.value.clientPort}`
  })
  if (callbackFunc) {
    callbackFunc()
  }
}
defineExpose({ init })
</script>

<style scoped>

</style>