<template>
  <a-upload
      v-model:file-list="fileList"
      name="file"
      :data="data"
      :action="SysConstant.baseApiPrefix + src"
      :showUploadList="false"
      :before-upload="beforeUpload"
      @change="handleChange"
  >
    <a-button :loading="status" class="butter-item" type="primary">
      <template #icon>
            <span class="anticon">
              <IconSvg color="rgba(255, 255, 255, 1)" size="16" name="import" />
            </span>
      </template>
      {{ $t('import') }}
    </a-button>
  </a-upload>
</template>

<script setup lang="ts">
import { ref, defineProps } from 'vue'
import type { UploadChangeParam, UploadFile } from 'ant-design-vue';
import { SysConstant } from '@/constant/sys'
import { message } from 'ant-design-vue'
import { t } from '@/i18n'

interface Props {
  src: string
  data?: object
  beforeUpload?: (file: UploadFile) => boolean
  uploadSuccessHandle?: (file: UploadFile) => void
}

const props = defineProps<Props>()
const fileList = ref([])
const status = ref<boolean>(false)

const handleChange = ({ file }: UploadChangeParam) => {
  if (file.status === 'done') {
    status.value = false
    message.success(t('component.submittedSuccessfully'))
    if (props.uploadSuccessHandle) {
      props.uploadSuccessHandle(file)
    }
  } else if (file.status === 'uploading'){
    status.value = true
  }
}

</script>

<style scoped>

</style>