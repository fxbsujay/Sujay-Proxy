<template>
  <div class="counter-instance">
      <ButtonTag
          v-for="item in symbols"
          :display-clear="true"
          :theme="item.type === '1' ?'white' : item.type === '2' ? 'grey' : 'transparent'"
          @click="onClick"
          :value="item"
          :label="item.value"
      />
  </div>
</template>

<script setup lang="ts">
import ButtonTag from '@/components/tag/ButtonTag.vue'

interface Symbol {
  value: string
  type: string  // 1 数组 2 符号 3 空白
}

/**
 * @description 计算器实例
 * @author fxbsujay@gmail.com
 * @version 13:10 2023/03/28
 */
import { ref, defineEmits } from 'vue'
const symbols = ref<Array<Symbol>>( [
  { type: '3', value: '' },
  { type: '3', value: '' },
  { type: '2', value: '(' },
  { type: '2', value: ')' },
  { type: '1', value: '7' },
  { type: '1', value: '8' },
  { type: '1', value: '9' },
  { type: '2', value: '/' },
  { type: '1', value: '4' },
  { type: '1', value: '5' },
  { type: '1', value: '6' },
  { type: '2', value: '×' },
  { type: '1', value: '1' },
  { type: '1', value: '2' },
  { type: '1', value: '3' },
  { type: '2', value: '-' },
  { type: '1', value: '0' },
  { type: '1', value: '.' },
  { type: '1', value: '000' },
  { type: '2', value: '+' }
])

const emit = defineEmits(['click'])

const onClick = (value: string) => {
  emit('click', value)
}

</script>

<style scoped lang="less">
.counter-instance {
  padding: 5px 0;
  float:right;
  margin: 0 5px;
  width: 13.5vw;
  height: 27.5vh;
  background: rgba(234, 236, 243, 1);
  border:  1px solid rgba(234, 236, 243, 1);
  border-radius: 1vw;
  display: flex;
  overflow: hidden;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  .butter-tag {
    margin: 0.2vw;
    box-shadow: 0 2px 5px 0 rgba(0, 0, 0, 0.1);
    &:hover {
      box-shadow: 0 0 20px 2px rgba(42, 130, 228, 0.3);
    }
  }
}
</style>