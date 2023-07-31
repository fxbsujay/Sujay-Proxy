<template xmlns="http://www.w3.org/1999/html">
  <div class="basic-table ">
    <a-row>
      <a-card class="butter-card">
          <slot name="wrapper" />
          <a-button v-if="permissionState.isSearchBut" class="butter-item" type="primary" shape="circle" @click="selectPageRequest">
            <template #icon><SearchOutlined /></template>
          </a-button>
          <a-button v-if="permissionState.isRefurbishBut" class="butter-divide freshen-button" shape="circle" @click="refurbishHandle">
            <template #icon><RedoOutlined :rotate="270"/></template>
          </a-button>
          <a-button id="add-btn" v-if="permissionState.isSave" class="butter-item" type="primary" ghost @click="onAddOrUpdateHandle()">
            <template #icon>
            <span class="anticon">
              <IconSvg color="rgba(47, 84, 235, 1)" size="16" name="plus" />
            </span>
            </template>
            {{ $t('add') }}
          </a-button>
          <a-popconfirm
              :title="$t('component.areYouSureYouWantToDeleteIt')"
              @confirm="onDeleteHandle()"
              v-if="permissionState.isDelete && tableContext.isSelection"
          >
            <a-button class="butter-item" type="primary" danger>
              <template #icon>
            <span class="anticon">
              <IconSvg color="rgba(255, 255, 255, 1)" size="16" name="delete" />
            </span>
              </template>
              {{ $t('delete') }}
            </a-button>
          </a-popconfirm>
          <slot name="buttons" />
          <a-button v-if="tableContext.isExpandWrapper" class="expand-wrapper-bnt" type="link" @click="onExpandedWrapperHandle(!tableContext.expandWrapperState)">
            {{ tableContext.expandWrapperState ? $t('collapse') : $t('expand') }}
            <DownOutlined v-if="!tableContext.expandWrapperState" />
            <UpOutlined v-else />
          </a-button>
        <a-row v-if="tableContext.isExpandWrapper && tableContext.expandWrapperState" class="expand-wrapper">
          <slot name="expandWrapper" />
        </a-row>
      </a-card>
    </a-row>
    <div style="display: flex">
      <slot name="tableLeftTree"/>
      <a-card class="content-card">
        <slot name="table">
          <a-table
              :expandRowByClick="tableContext.isClickRowExpand"
              :expandIconAsCell="false"
              :expandIconColumnIndex="-1"
              :indent-size="0"
              :expandedRowKeys="tableContext.expandedRowKeys"
              :onExpand="(bool, row) => { onExpandedClickHandle(row[tableContext.rowKey])}"
              bordered
              size="small"
              class="ant-pro-table ant-table-striped"
              :pagination="false"
              :loading="tableContext.loading"
              :row-class-name="(_record, index) => (index % 2 === 1 ? 'table-striped' : null)"
              :dataSource="tableContext.dataSource.list"
              :row-key="tableContext.rowKey"
              :row-selection="tableContext.isSelection ? { selectedRowKeys: tableContext.selectedRowKeys, onChange: onSelectChange } : null"
              :columns="tableContext.columns"
              @change="tablePaginationChange"
          >
            <template v-slot:[item]="scope" v-for="item in slots">
              <slot :name="item" :scope="scope" v-bind="scope || {}"></slot>
            </template>
            <template #headerCell="{ title, column }">
              {{ $t(title) }}
            </template>

            <template #bodyCell="{ column, record }">
              <slot :name="column.key" v-bind="record" />
              <template v-if="tableContext.iconExpandColumnKey && column.key === tableContext.iconExpandColumnKey">
                <span @click.stop>
                <div style="display: flex;justify-content: left;" >
                  <span :style="{ marginLeft:  (10 * record['level'] ) + 'px'}"></span>
                  <div style="min-width: 50px">
                    <IconSvg @click="onExpandedClickHandle(record[tableContext.rowKey])" v-if="(slots.includes('expandedRowRender') || record['children']) && isExpandedKey(record[tableContext.rowKey])" size="20" style="transform: rotate(-90deg);" name="expanded" />
                    <IconSvg @click="onExpandedClickHandle(record[tableContext.rowKey])" v-else-if="slots.includes('expandedRowRender') || record['children']" size="20" name="expanded" />
                  </div>
                  <div>
                    {{ record[column.dataIndex] }}
                  </div>
                </div>
                </span>
              </template>

              <template v-else-if="column.dataIndex === 'operation'">
                <span @click.stop>
                <slot name="beforeOperation" v-bind="{ column, record } || {}"></slot>
                <a-button
                    v-if="permissionState.isUpdate"
                    type="link"
                    @click="onAddOrUpdateHandle(record)"
                >
                {{ $t('edit') }}
                </a-button>
                <a-popconfirm
                    v-if="permissionState.isDelete"
                    :title="$t('component.areYouSureYouWantToDeleteIt')"
                    @confirm="onDeleteHandle(record[tableContext.rowKey])"
                >
                  <a-button type="link">{{ $t('delete') }}</a-button>
                </a-popconfirm>
                <slot name="afterOperation" v-bind="{ column, record } || {}"></slot>
              </span>
              </template>
            </template>
          </a-table>
          <a-pagination
              v-if="tableContext.paginationOptions.show"
              style="margin-top: 20px"
              v-model:page-size="tableContext.paginationOptions.pageParams.limit"
              v-model:current="tableContext.paginationOptions.pageParams.page"
              :show-total="tableContext.paginationOptions.showTotal"
              :page-size-options="tableContext.paginationOptions.pageSizeOptions"
              :show-quick-jumper="tableContext.paginationOptions.showQuickJumper"
              :show-size-changer="tableContext.paginationOptions.showSizeChanger"
              :total="tableContext.dataSource.total"
              @change="tablePaginationChange"
          />
        </slot>
      </a-card>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * <p>基础表表格</p>
 *
 * @author fxbsujay@gmail.com
 * @version 10:24 2023/03/10
 *
 * Explain: Turning off multiple selections also turns off the delete button in the top bar
 */
import { defineProps, useSlots } from 'vue'
import {
  SearchOutlined,
  RedoOutlined,
  DownOutlined,
  UpOutlined
} from '@ant-design/icons-vue'
import IconSvg from '@/components/iconSvg/iconSvg.vue'
import { Table } from '@/components/page/hook/useListPage'

interface Props {
  registerTable: Table
}

const slots = Object.keys(useSlots())

const props = defineProps<Props>()
const {
  registerTable: {
    tableContext,
    permissionState,
    tablePaginationChange,
    refurbishHandle,
    selectPageRequest,
    methods: {
      onExpandedWrapperHandle,
      isExpandedKey,
      onExpandedClickHandle,
      onSelectChange,
      onDeleteHandle,
      onAddOrUpdateHandle
    }
  }
} = props

</script>