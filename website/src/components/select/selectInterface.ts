import { Ref } from 'vue'

export type KeyType = string | number
/**
 * <p>各种附带选项新增删除的选择下拉框</p>
 * @author fxbsujay@gmail.com
 * @version 12:00 202/03/16
 */
export default interface SimpleEditSelectSetting<T = any> {
    valueKey?: KeyType
    labelKey?: KeyType                 // 新增时需要设置的字段名
    options: T | Ref<T>
    disableDeletionKeys?: KeyType[]              // 禁止删除的选项
    originalModel: T                    // 原始初始化对象 用于添加
    saveApi?: (data: T) => Promise<any>
    deleteApi?: (ids: number[]) => Promise<any>
    refreshHandle?: () => void                // 刷新方法
}

/***
 * 企业文件
 */
export const disableDeletionKeys: number[] = [-2,-1,1,2,3,4,5,6,12]