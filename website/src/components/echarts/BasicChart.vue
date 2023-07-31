<template>
  <a-spin :style="{ background: spinBackground ? spinBackground : ' #fff', maxHeight: 'none' }" :spinning="loading">
    <div :class="className" :style="{ height: height, width: width }" class="chart" :id="id" ></div>
  </a-spin>
</template>

<script setup lang="ts">
import { onMounted, ref, watch, defineExpose } from 'vue'
import { ECharts, EChartsOption, init } from 'echarts'
import 'echarts/theme/macarons'
import 'echarts-gl'
import { generateID } from '@/utils'
import walden from './walden'

interface Props {
  spinBackground?: string
  loading?: boolean
  height: string,
  theme?: boolean,
  width: string,
  className?: string
  option: EChartsOption
}

const id = ref(generateID())
const props = defineProps<Props>()

const { option } = props

let chart: ECharts

const initChart = (option: EChartsOption) => {
  if (!option) {
    return
  }

  chart.setOption(option, true)
}

onMounted(() => {
  chart = init(document.getElementById(id.value) as HTMLElement, props.theme ? walden : 'macarons')
  initChart(option)
  window.onresize = function () {
    chart.resize()
  }
})

// 当页面有多个组件时要放在父组件中去调用
const resize = () => {
  chart.resize()
}

watch(props, () => {
  initChart(props.option)
})

const download = () => {
  const img = new Image()
  img.src = chart.getDataURL({ type: 'png', pixelRatio: 1, backgroundColor: '#fff' })

  img.onload = function () {
    const canvas = document.createElement("canvas");
    canvas.width = img.width
    canvas.height = img.height
    const ctx = canvas.getContext(`2d`)
    ctx?.drawImage(img, 0, 0);
    const dataURL = canvas.toDataURL("image/png");
    const a = document.createElement("a");
    const event = new MouseEvent("click");
    a.download = "chart.png"
    a.href = dataURL
    a.dispatchEvent(event)
    a.remove();
  }
}

defineExpose({
  download,
  resize
})
</script>

<style scoped lang="less">
.chart {
  width: 100%;
  height: 400px;
}
/deep/ .ant-spin-nested-loading {
  height: 100%;
  .ant-spin-container {
    height: 100%;
  }
}
</style>
