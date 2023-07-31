<template>
  <a-table
      style="margin-top: 10px"
      :pagination="false"
      size="small"
      :loading="tableContext.loading"
      v-model:dataSource="tableContext.pageData.list"
      :columns="tableContext.columns"
  >
    <template #headerCell="{ title, column }">
      {{ $t(title) }}
    </template>
  </a-table>
  <a-pagination
      style="margin-top: 20px"
      v-model:page-size="tableContext.pageParams.limit"
      v-model:current="tableContext.pageParams.page"
      :show-total="(total) => $tc('component.showTotalText', { count: total })"
      :page-size-options="['10', '20', '50', '100']"
      :show-quick-jumper="true"
      :show-size-changer="true"
      :total="tableContext.pageData.total"
      @change="tableContext.queryListChange"
  />
</template>

<script setup lang="ts">
/**
 * <p>基础表表格</p>
 * @author fxbsujay@gmail.com
 * @version 10:24 2023/03/10
 */
import { defineProps } from 'vue'
import { BasicTableContext } from '@/components/page/state/BasicTable'

export interface Props {
  tableContext: BasicTableContext
}

const props = defineProps<Props>()

</script>