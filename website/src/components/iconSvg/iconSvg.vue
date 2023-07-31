<template>
  <svg :class="svgClass" aria-hidden="true" :style="{ width: svgSize ? svgSize + 'px' : '26px',height: svgSize ? svgSize  + 'px' : '26px', color: svgColor ? svgColor : 'rgba(47, 84, 235, 1)'  }">
    <use class="svg-use" :href="symbolId" />
  </svg>
</template>

<script setup lang="ts">
import { computed, toRef } from 'vue'

interface Props {
  name: string,
  color?: string,
  size?: number | string,
  className?: string
}

const props = defineProps<Props>()

const symbolId = computed(() => `#${props.name}`)
const svgSize = toRef(props, 'size')
const svgColor = toRef(props, 'color')
const svgClass = computed(() => {
  if (props.className) {
    return `svg-icon ${props.className}`
  }
  return 'svg-icon'
})


</script>
<style scoped>
.svg-icon {
  outline: none !important;
  fill: currentColor;
  vertical-align: -0.15em;
  overflow: hidden;
}

</style>
