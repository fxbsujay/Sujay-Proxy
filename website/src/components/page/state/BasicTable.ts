import { PageData, QueryParams } from '@/model/BaseObject'
import { TableColumnType } from 'ant-design-vue'

export interface BasicTableContext {
    loading: boolean
    pageParams: QueryParams
    pageData: PageData<any>,
    columns: TableColumnType<any>[],
    queryListChange: (page: number) => void
}
