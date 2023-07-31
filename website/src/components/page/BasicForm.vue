<template>
  <a-modal
      :maskClosable="false"
      :centered="centered"
      class="basic-form"
      :width="width"
      v-model:visible="visible"
      :title="$t(title)"
      :footer="formContext.footer"
      @cancel="formContext.onCloseHandle ? formContext.onCloseHandle() : visible = false"
  >
    <template #footer>
      <a-button v-if="formContext.onAddOrUpdateSubmitHandle" key="submit" type="primary" :loading="loading" @click="formContext.onAddOrUpdateSubmitHandle()" >{{ $t('ok')}}</a-button>
      <a-button v-if="formContext.onCloseHandle" key="back" @click="formContext.onCloseHandle()">{{ $t('cancel')}}</a-button>
    </template>
    <slot/>
  </a-modal>

</template>

<script setup lang="ts">
/**
 * <p>基础表单</p>
 * @author fxbsujay@gmail.com
 * @version 10:24 2023/03/10
 */
import { FormContext } from '@/components/page/hook/useForm'
import { defineProps, toRefs } from 'vue'

interface Props {
  formContext: FormContext
}

const props = defineProps<Props>()
const { formContext } = props
const { width, title, visible, loading, centered } = toRefs(formContext)


</script>

<style scoped>

</style>