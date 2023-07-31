<template>
  <div @click="e => selectClickHandel(e)">
    <a-select
        :disabled="disabled"
        :placeholder="placeholder"
        v-model:value="formState[modelKey]"
        :open="open"
        @blur="blurEvent"
        option-label-prop="label"
    >
      <a-select-option v-for="item in options" :label="item[labelKey]" :key="item[valueKey]" :value="item[valueKey]">
        <div class="select-option-item">
          <div class="item-label" @click="open = false">
            {{ item[labelKey] }}
          </div>
          <div v-if="!isDisableDeletionItem(item[valueKey])" class="item-delete">
            <IconSvg color="#ff7875" name="delete" @click.stop="deleteItemSubmitHandle(item[valueKey])" />
          </div>
        </div>
      </a-select-option>

      <template #dropdownRender="{ menuNode: menu }" >
        <v-nodes :vnodes="menu" />
        <a-divider style="margin: 4px 0" />
        <div
            style="padding: 4px 8px; cursor: pointer; color: rgba(0, 0, 0, 0.85)"
        >
          <div v-if="!status" @mousedown="e => e.preventDefault()"  @click="status = true">
            <plus-outlined />
            {{ $t('add') }}
          </div>
          <div  v-else>
            <a-row :gutter="24">
              <a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="10" :xxl="12">
                <a-input @blur="blurEvent" id="inputSearch"  v-model:value="saveName" />
              </a-col>
              <a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="14" :xxl="12">
                <div class="save-buts">
                  <a-button :loading="loading" size="small" @mousedown="e => e.preventDefault()" @click="saveItemSubmitHandle" type="primary">{{ $t('ok') }}</a-button>
                  <a-button size="small" @mousedown="e => e.preventDefault()" @click="cancelSaveItemHandle"  style="margin-left: 10px">{{ $t('cancel') }} </a-button>
                </div>
              </a-col>
            </a-row>
          </div>
        </div>
      </template>
    </a-select>
    <div v-show="!open" class="select_overlap" @mouseup="openSelect"></div>
  </div>

</template>

<script lang="ts">
/**
 * 类型选择框
 */
import { ref, defineComponent } from 'vue'
import SimpleEditSelectSetting, {disableDeletionKeys} from '@/components/select/selectInterface'
import { PlusOutlined } from '@ant-design/icons-vue'
import Copy from '@/utils/copy'
import { message } from 'ant-design-vue'
import IconSvg from '@/components/iconSvg/iconSvg.vue'

export default defineComponent({
  name: 'SimpleEditSelect',
  components: {
    PlusOutlined,
    IconSvg,
    VNodes: (_, { attrs }) => {
      return attrs.vnodes
    }
  },
  props: ['setting', 'formState', 'modelKey', 'placeholder', 'disabled'],
  setup(props) {

    const setting: SimpleEditSelectSetting = props.setting
    const { options, originalModel, disableDeletionKeys, saveApi, deleteApi, refreshHandle } = setting
    const valueKey = setting.valueKey ? setting.valueKey : 'id'
    const labelKey = setting.labelKey ? setting.labelKey : 'name'

    const status = ref<boolean>(false)
    const open = ref<boolean>(false)
    const loading = ref<boolean>(false)
    const disable = ref<boolean>(false)
    const saveName = ref<string>('')

    const saveItemSubmitHandle = () => {
      if (!saveApi) {
        return
      }
      loading.value = true
      const data = Copy.JsonClone(originalModel)
      data[labelKey] = saveName.value

      saveApi(data).then( () => {
        status.value = false
        loading.value = false
        message.success('添加成功')
        if (refreshHandle) {
          refreshHandle()
        }
      }).catch(() => {
        loading.value = false
        status.value = false
      })
    }

    const deleteItemSubmitHandle = (key: number) => {
      if (!deleteApi || disable.value) {
        return
      }
      disable.value = true
      deleteApi([key]).then( () => {
        status.value = false
        disable.value = false
        message.success('删除成功')
        if (refreshHandle) {
          refreshHandle()
        }
      }).catch(() => {
        disable.value = false
        status.value = false
      })

    }

    const cancelSaveItemHandle = () => {
      saveName.value = ''
      status.value = false
    }

    const openSelect = () => {
      open.value = !open.value
    }

    const selectClickHandel = (e) => {
      e.preventDefault()
      open.value = !open.value
      status.value = false
    }

    const blurEvent = () => {
      if (document.activeElement?.id !== 'inputSearch') {
        open.value = false
      }
    }

    const isDisableDeletionItem = (key: string) => {
      if (!disableDeletionKeys) {
        return false
      }

     return disableDeletionKeys.includes(key)

    }

    const { formState, modelKey, placeholder } = props

    return {
      formState,
      modelKey,
      placeholder,
      saveName,
      blurEvent,
      openSelect,
      open,
      selectClickHandel,
      cancelSaveItemHandle,
      deleteItemSubmitHandle,
      saveItemSubmitHandle,
      isDisableDeletionItem,
      status,
      loading,
      options,
      valueKey,
      labelKey
    }
  }
})

</script>

<style lang="less" scoped>
.select-option-item {
  height: 100%;
  align-items: center;
  .item-label {
    height: 100%;
    display: flex;
    align-items: center;
    float: left;
  }

  .item-delete {
    float: right;
  }
}
.save-buts {
  display: flex;
  height: 100%;
  align-items: center;
}
</style>