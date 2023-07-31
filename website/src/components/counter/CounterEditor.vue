<template>
  <a-spin :spinning="show">
    <a-row :gutter="24" class="counter-editor">
      <a-col :span="18">
        <div class="counter-buts">
          <a-button class="save-but" type="primary">{{ $t('save')}}</a-button>
          <a-button class="clear-but" type="primary" @click="computeList.splice(0, computeList.length)">{{ $t('clear') }}</a-button>
        </div>
        <div class="counter-reveal">
          <div class="counter-reveal-left">
            <div class="counter-tags" v-if="!show">
              <ButtonTag
                  v-for="(item,index) in computeList"
                  :theme="item.type === '2' || item.type === '1' ? 'grey' : 'green'"
                  :value="item"
                  @clear="() => onClear(index)"
                  :label="getName(item)"
              />
            </div>
          </div>
          <Counter @click="onClick"/>
        </div>
      </a-col>
      <a-col :span="6">
        <a-input-search
            class="counter-buts"
            style="max-width: 270px;"
            :placeholder="$tc('pleaseEnter', { key: 'versionData.name' })"
            enter-button
            @search="onSearchTree"
        />
        <a-row class="counter-tree scrollbar-pro-y">
          <a-tree
              style="width: 100%"
              :tree-data="versionDataTreeList"
          >
            <template v-slot:title="nodeData">
               <div class="tree-node">
                 <div class="node-label">{{ nodeData.name }}</div>
                 <div v-if="nodeData.type" class="node-btn" @click="onClick(nodeData)">{{ $t('insert') }}</div>
               </div>
            </template>
          </a-tree>
        </a-row>
      </a-col>
    </a-row>
  </a-spin>
</template>

<script setup lang="ts">
/**
 * @description 活动数据计算器
 * @author fxbsujay@gmail.com
 * @version 09:42 2023/03/27
 */
import { VersionDataComputeModel, VersionDataModel } from '@/model/plan/VersionDataModel'
import ButtonTag from '@/components/tag/ButtonTag.vue'
import Counter from '@/components/counter/Counter.vue'
import { defineProps, ref, watch, defineEmits } from 'vue'
import GwpModel from '@/model/plan/GwpModel'
import { FacModel } from '@/model/plan/FacModel'
import { CarbonPriceModel } from '@/model/assets/CarbonPriceModel'
import { EmissionSourceTreeModel } from '@/model/plan/EmissionSourceModel'
import Copy from "@/utils/copy";
import { TreeNode } from '@/model/BaseObject'

interface TagItem {
  id?: number
  children?: TagItem[]
  name?: string
  type?: string // 0 活动数据 1 数字 2 符合 3 GWP 4 碳排放 6 碳资产价格
}

interface Props {
  versionDataId?: number
  versionSummaryId?: number
  isCarbonEmissions: boolean // 是否是碳排放数据
  computeList: VersionDataComputeModel[]
  facList: FacModel[]
  emissionSourceList: EmissionSourceTreeModel[]
  versionDataList: VersionDataModel[]
  gwpList: GwpModel[]
  carbonPriceList: CarbonPriceModel[]
}

const show = ref<boolean>(true)
const props = defineProps<Props>()
const emit = defineEmits(['change'])

const versionDataTreeList = ref<Array<TagItem>>([])
const originalDataTreeList = ref<Array<TagItem>>([])

const generateData = () => {
  const { versionDataList, facList, emissionSourceList, gwpList, carbonPriceList } = props

  show.value = true
  versionDataTreeList.value = []
  const facItems: TagItem[] = []
  const emissionSourceItems: TagItem[] = []
  const gwpItems: TagItem[] = []
  const carbonPriceItems: TagItem[] = []
  const otherItems: TagItem[] = []

  gwpList.forEach( item => {
    gwpItems.push({
      id: item.id,
      name: `*GWP*：${item.name}，值为：${item.value}`,
      type: '3'
    })
  })

  carbonPriceList.forEach( item =>{
    carbonPriceItems.push({
      id: item.id,
      type: '6',
      name: `${item.exchangeDesc}${item.date}，价格：${item.price}`
    })
  })

  facList.forEach( item => {
    facItems.push({
      id: item.id,
      name: item.name
    })
  })

  emissionSourceList.forEach( item => {
    emissionSourceItems.push({
      id: item.id,
      name: item.name,
      children: Copy.DeepClone(item.children,[])
    })
  })

  for (let i = 0; i < versionDataList.length; i++) {

    const dataItem: TagItem = {
      type: '0',
      id:  versionDataList[i].id,
      name: versionDataList[i].name
    }

    if (versionDataList[i].facId! > 0 || versionDataList[i].fuelIds! > 0) {
      if (versionDataList[i].facId! > 0) {
        facItems.forEach( facItem => {
          if (versionDataList[i].facId === facItem.id) {
            if (facItem.children) {
              facItem.children.push(dataItem)
            } else {
              facItem.children = [dataItem]
            }
          }
        })
      }

      if (versionDataList[i].fuelIds! > 0) {
        emissionSourceItems.forEach( emissionSource => {
          if (emissionSource.children && emissionSource.children.length > 0 && emissionSource.id === versionDataList[i].fuelId) {
            emissionSource.children.forEach( subFuel => {
              if (versionDataList[i].fuelIds === subFuel.id) {
                if (subFuel.children) {
                  subFuel.children.push(dataItem)
                } else {
                  subFuel.children = [dataItem]
                }
              }
            })
          }
        })
      }
    } else {
      otherItems.push(dataItem)
    }
  }

  versionDataTreeList.value = [
    {
      id: -1,
      name: '排放设施',
      children: facItems,      // 0
    },
    {
      id: -2,
      name: '排放源',
      children: emissionSourceItems,  // 0
    },
    {
      id: -3,
      name: 'GWP',
      children: gwpItems,   // 3
    },
    {
      id: -4,
      name: '其他参数',
      children: otherItems,  // 0
    },
    {
      id: -5,
      name: '碳资产价格',
      children: carbonPriceItems, // 6
    }
  ]

  if (props.isCarbonEmissions) {
    const newFacItems: TagItem[] = []
    facList.forEach( item => {
      newFacItems.push({
        id: item.id,
        type: '4',
        name: `${item.name}碳排放量`    // 4
      })
    })
    versionDataTreeList.value.push({
      id: -6,
      name: '全部设施碳排放量',
      children: newFacItems
    })
  }
  originalDataTreeList.value = Copy.DeepClone(versionDataTreeList.value, [])
  show.value = false
}

watch(() => props.versionDataList,() => generateData())

const onClear = (index: number) => {
  props.computeList.splice(index, 1)
}

// 添加数组符号
const onClick = (value: { id?: number, value: string, type: string }) => {
  const lastItem = props.computeList[props.computeList.length - 1]
  if (!value.id) {
    if (value.type === '1' && lastItem && lastItem.type === '1') {
      if (lastItem.value === '0' && ( value.value === '0' || value.value === '000')) {
        return;
      }
      if (value.value === '.' && lastItem.value.indexOf('.') >= 0) {
        return;
      }
      lastItem.value = lastItem.value + value.value
    } else {
      if (value.type === '1' && lastItem && lastItem.type !== '2') {
        return;
      }
      if (value.value === '.' || value.value === '000') {
        return
      }

      const item: VersionDataComputeModel = {
        type: value.type,
        value: value.value,
        number: props.computeList.length
      }
      if (props.versionDataId) {
        item.versionDataId = props.versionDataId
      }
      if (props.versionSummaryId) {
        item.versionSummaryId = props.versionSummaryId
      }
      props.computeList.push(item)
    }
  } else {
    if (lastItem && lastItem.type !== '2') {
      return
    }
    const item: VersionDataComputeModel = {
      type: value.type,
      value: value.id.toString(),
      number: props.computeList.length
    }
    if (props.versionDataId) {
      item.versionDataId = props.versionDataId
    }
    if (props.versionSummaryId) {
      item.versionSummaryId = props.versionSummaryId
    }
    props.computeList.push(item)
  }
}


const getName = (compute: VersionDataComputeModel) => {
  let name: string = '---'
  if (compute.type === '0') {
    props.versionDataList.forEach( item => {
      if (item.id === parseInt(compute.value)) {
        name = item.name!
        return
      }
    })
  } else if (compute.type === '3') {
    props.gwpList.forEach( item => {
      if (item.id === parseInt(compute.value)) {
        name = `*GWP*：${item.name}，值为：${item.value}`
        return
      }
    })
  } else if (compute.type === '6') {
    props.carbonPriceList.forEach( item =>{
      if (item.id === parseInt(compute.value)) {
        name = `${item.exchangeDesc}${item.date}，价格：${item.price}`
        return
      }
    })
  } else if (compute.type === '4') {
    props.facList.forEach( item => {
      if (item.id === parseInt(compute.value)) {
        name = `${item.name}碳排放量`
        return
      }
    })
  } else {
    name = compute.value
  }

  return name
}

const onSearchTree = (searchValue: string) => {
  if (!searchValue || searchValue === '') {
    versionDataTreeList.value = Copy.DeepClone(originalDataTreeList.value,[])
   return
  }

  const list: TreeNode[] = []
  filterTree(searchValue,Copy.DeepClone(originalDataTreeList.value,[]),list)
  versionDataTreeList.value = list
}


const filterTree = (name: string, list: TagItem[] = [], data: TagItem[]) => {
  if (!list.length) return []

  for (let item of list) {
    const children: TagItem[] = []
    if (item.children && item.children.length > 0) {
      filterTree(name,item.children, children)
    }

    item.children = children.length <= 0 ? undefined : children

    if (item.name?.indexOf(name) !== -1 || children.length > 0) {
      data.push(item)
    }
  }
}

</script>
<style scoped lang="less">
/deep/ .ant-spin-nested-loading {
  height: 500px;
}
/deep/ .ant-spin-container {
  min-height: 400px;
}
.counter-editor {
  height: 100%;

  .counter-buts {
    padding: 10px 0 ;
    .clear-but {
      margin-left: 15px;
      border-color: rgba(215, 168, 41, 1);
      background: rgba(215, 168, 41, 1);
    }
  }

  .counter-reveal {
    margin-top: 4px;
    border-radius: 2px;
    border: 1px solid rgba(234, 236, 243, 1);
    height: calc(100% - 60px);
    width: 100%;
    word-wrap: break-word;
    padding: 15px;
    display: -webkit-flex;
    display: flex;

    .counter-reveal-left {
      -webkit-flex: 1;
      flex: 1;
    }

    .counter-tags {
      display: flex;
      overflow-x: hidden;
      flex-wrap: wrap;
    }
  }
  .counter-tree {
    margin-top: 4px;
    border-radius: 2px;
    border: 1px solid rgba(234, 236, 243, 1);
    height: 372px;
    width: 100%;
    word-wrap: break-word;
    padding: 10px;
  }
}
/deep/ .ant-tree {
  width: 100%;
}
/deep/.ant-tree .ant-tree-treenode {
  width: 100%;
}
/deep/.ant-tree .ant-tree-treenode-motion {
  width: 100%;
}

/deep/.ant-tree .ant-tree-node-content-wrapper {
  width: 100%;
}
.tree-node {
  width: 100%;
  display: -webkit-flex;
  display: flex;
  .node-label {
    -webkit-flex: 1;
    flex: 1;
    font-size: 14px;
  }
  .node-btn {
    text-align: center;
    width: 50px;
    color: rgba(47, 84, 235, 1);
    font-weight: bold;
    font-size: 14px;
  }
}

</style>