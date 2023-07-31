<template>
  <a-button :disabled="!show" @click="onClick">{{ show ? $t(label) : $t('component.tryAgainSeconds', { count }) }}</a-button>
</template>

<script lang="ts">
import { defineComponent, reactive, ref, toRefs } from 'vue'
import {checkRegExp, RegExpConstant} from "@/utils/regex";
import {message} from "ant-design-vue";

export default defineComponent({
  props: ['label', 'count'],
  setup(props, context) {
    const label = ref(props.label)
    const count = ref(props.count)
    let timer =  ref()
    const data = reactive({
      show: true,
      timeCount: count.value
    })
    const onClick = () => {
      context.emit("onClick")
    }
    const onStart = () => {
      if (!timer.value) {
        data.show = false;
        timer.value = setInterval(() => {
          if (count.value > 0 && count.value <= data.timeCount) {
            count.value--;
          } else {
            count.value = data.timeCount
            data.show = true
            clearInterval(timer.value)
            timer.value = null
          }
        }, 1000);
      }
    }
    return {
      onClick,
      onStart,
      label,
      count,
      ...toRefs(data)
    }
  }
})
</script>

<style scoped>

</style>