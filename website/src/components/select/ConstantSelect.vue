<template>
  <a-select
      :allow-clear="allowClear"
      :disabled="disabled"
      :placeholder="placeholder"
      v-model:value="modelValue"
      @change="props.change"
  >
    <a-select-option v-for="item in constant" :key="item['value']" :value="item['value']">
      {{ $t(item['label']) }}
    </a-select-option>
  </a-select>
</template>
<script lang="ts">
export default {
  name: 'ConstantSelect'
}
</script>
<script setup lang="ts">
import { Select as ASelect } from 'ant-design-vue'
import { defineProps, defineEmits, computed } from 'vue'
import { Constant } from '@/constant'

interface Props {
  value: any
  allowClear?: boolean
  placeholder?: string
  constant: Constant[],
  disabled?: boolean
  change?: (value: any) => void
}

const props = defineProps<Props>()
const emits = defineEmits(['update:value'])

const modelValue = computed({
  get() {
    return props.value
  },
  set(v: any) {
    emits('update:value', v)
  }
})
</script>

<style scoped>

</style>