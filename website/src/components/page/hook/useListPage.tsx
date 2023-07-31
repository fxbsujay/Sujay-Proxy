/**
 * <p>表格 hook</p>
 * @author fxbsujay@gmail.com
 * @version 10:24 2023/03/10
 */

import { message, TableColumnType } from 'ant-design-vue'
import { BaseModel, PageData, QueryParams } from '@/model/BaseObject'
import { reactive, onBeforeMount, Ref  } from 'vue'
import { Props } from 'ant-design-vue/lib/form/useForm'
import Copy from '@/utils/copy'
import { hasPermission } from '@/router/permission'
import { tc } from '@/i18n'
export type Key = string | number

export interface ListPageOptions<T = any> {
    queryParams: QueryParams
    columns: TableColumnType<T>[] | Ref<TableColumnType<T>[]>
    addOrUpdateRef?: Props | Ref<Props>
    api: (...arg: any) => Promise<PageData<T>>
    deleteApi?: (...arg: any) => Promise<any>
    infoApi?:  (...arg: any) => Promise<T>      // 打开新增修改模态框时执行，如果没有则传递当前行数据
    rowKey?: Key
    iconExpandColumnKey?: string                // 自定义展开行按钮的 Column Key
    isBeforeRefurbish?: boolean                 // 是否开启页面刷新
    isSelection?: boolean                       // 是否多选
    isExpandWrapper?: boolean                   // 是否需要展开更多筛选条件
    pagination?: boolean                        // 是否需要分页
    isSearchBut?: boolean                       // 搜索按钮
    isRefurbishBut?: boolean                    // 刷新按钮
    isClickRowExpand?: boolean                  // 是否需要点击行展开
    savePermissions?: boolean | string
    updatePermissions?: boolean | string
    deletePermissions?: boolean |string
}

export interface Table<T = any> {
    tableContext: TableContext<T>
    permissionState: PermissionState
    selectPageRequest: () => void
    tablePaginationChange: (page: number) => void,
    refurbishHandle: ()  => void
    methods: TableMethods
}

export interface TableContext<T = any> {
    dataSource: PageData<T>,
    isClickRowExpand: boolean
    isExpandWrapper: boolean
    expandWrapperState: boolean
    columns: TableColumnType<T>[] | Ref<TableColumnType<T>[]>
    rowKey: Key
    iconExpandColumnKey?: string
    isSelection: boolean
    paginationOptions: PaginationOptions
    selectedRowKeys: Key[]
    expandedRowKeys: Key[]
    loading: boolean
}

export interface PaginationOptions {
    show: boolean
    pageParams: QueryParams
    showTotal: (total: number) => string,
    showQuickJumper: boolean
    showSizeChanger: boolean
    pageSizeOptions: string[]
}

interface PermissionState {
    isDelete: boolean
    isSave: boolean
    isUpdate: boolean
    isSearchBut: boolean
    isRefurbishBut: boolean
}

interface TableMethods {
    isExpandedKey: (key: Key) => boolean                // 当前行是否是展开状态
    onExpandedClickHandle: (key: Key) => void           // 展开某一行，当前为展开状态则关闭当前行
    onExpandedWrapperHandle: (state: boolean) => void   // 展开更多筛选条件
    onSelectChange: (selectedRowKeys: Key[]) => void    // 多选方法
    onDeleteHandle: (id: Key) => void                   // 删除方法
    onAddOrUpdateHandle: (record?: BaseModel) => void    // 打开新增修改模态框
}

export const useListPage = (options: ListPageOptions): Table => {

    const registerTable = useTable(options)

    return {
        ...registerTable
    }
}

const useTable = (options: ListPageOptions) => {
    const originalParams = Copy.DeepClone(options.queryParams,{})
    const tableContext = reactive<TableContext>({
        rowKey: typeof options.rowKey === 'string' ? options.rowKey : 'id',
        dataSource: new PageData(),
        columns: options.columns,
        isSelection: typeof options.isSelection === 'boolean' ? options.isSelection : true,
        isClickRowExpand: typeof options.isClickRowExpand === 'boolean' ? options.isClickRowExpand : false,
        isExpandWrapper: typeof options.isExpandWrapper === 'boolean' ? options.isExpandWrapper : false,
        expandWrapperState: false,
        selectedRowKeys: [],
        iconExpandColumnKey: options.iconExpandColumnKey,
        expandedRowKeys: [],
        loading: false,
        paginationOptions: {
            pageParams: options.queryParams,
            show: typeof options.pagination === 'boolean' ? options.pagination : true,
            showTotal: (total: number) => tc('component.showTotalText', { count: total }),
            showSizeChanger: true,
            pageSizeOptions: ['10', '20', '50', '100'],
            showQuickJumper: true
        }
    })

    const permissionState = reactive<PermissionState>({
        isRefurbishBut: typeof options.isRefurbishBut === 'boolean' ? options.isRefurbishBut : true,
        isSearchBut: typeof options.isSearchBut === 'boolean' ? options.isSearchBut : true,
        isDelete: typeof options.deletePermissions === 'boolean' ? options.deletePermissions : options.deletePermissions === undefined ? true : hasPermission(options.deletePermissions),
        isSave: typeof options.savePermissions === 'boolean' ? options.savePermissions : options.savePermissions === undefined ? true : hasPermission(options.savePermissions),
        isUpdate: typeof options.updatePermissions === 'boolean' ? options.updatePermissions : options.updatePermissions === undefined ? true : hasPermission(options.updatePermissions)
    })

    const rinseTree = (tree: any, level: number = 0) => {
        if (tree.length <= 0) {
            return
        }

        level++

        for (let i = 0; i < tree.length; i++) {
            tree[i].index = i + 1
            tree[i].level = level
            if (tree[i].children && tree[i].children.length > 0) {
                rinseTree(tree[i].children, level   )
            } else {
                delete tree[i]['children']
            }

        }

    }

    /**
     * 分页请求
     */
    const selectPageRequest = () => {
        const { api, queryParams } = options
        tableContext.loading = true
        api(queryParams).then( res => {
            tableContext.loading = false
            if (res.list && res.list.length > 0) {
                rinseTree(res.list)
            }
            tableContext.dataSource = res
        }).catch(() => tableContext.loading = false )

    }

    /**
     * 分页器
     * @param page
     */
    const tablePaginationChange = (page: number) => {
        options.queryParams.page = page
        selectPageRequest()
    }

    /**
     * 刷新
     */
    const refurbishHandle = () => {
        options.queryParams = Copy.DeepClone(originalParams,options.queryParams)
        selectPageRequest()
    }

    if (typeof options.isBeforeRefurbish === 'boolean' ? options.isBeforeRefurbish : true) {
        onBeforeMount(refurbishHandle)
    }

    const methods = registerHandles(options, tableContext, selectPageRequest)

    return {
        tableContext,
        permissionState,
        selectPageRequest,
        refurbishHandle,
        tablePaginationChange,
        methods
    }
}


const registerHandles = (options: ListPageOptions, tableContext: TableContext, selectPageRequest: () => void): TableMethods => {

    /**
     * 多选
     * @param selectedRowKeys
     */
    const onSelectChange = (selectedRowKeys: Key[]) => {
        tableContext.selectedRowKeys = selectedRowKeys
    }

    /**
     * 删除方法
     */
    const onDeleteHandle = (id: Key) => {

        const ids = id ? [id] : tableContext.selectedRowKeys
        if (ids.length < 1) {
            message.warning('请选择要删除的一项')
            return
        }

        const { deleteApi } = options

        if (deleteApi) {
            deleteApi(ids).then( () => {
                message.success('删除成功')
                selectPageRequest()
            })
        }
    }


    /**
     * 新增修改 不配置 infoApi 则传递当前行对象
     */
    const onAddOrUpdateHandle = (record?: BaseModel) => {
        const { addOrUpdateRef } = options

        if (!addOrUpdateRef) {
            return
        }
        if (record && record.id) {
            const { infoApi } = options
            if (infoApi) {
                infoApi(record.id).then( res => {
                    addOrUpdateRef.value.init(selectPageRequest,res)
                })
            } else {
                addOrUpdateRef.value.init(selectPageRequest,record)
            }
        } else {
            addOrUpdateRef.value.init(selectPageRequest)
        }
    }

    const onExpandedClickHandle = (key: Key) => {

        if (!tableContext.isClickRowExpand && !tableContext.iconExpandColumnKey) {
            return
        }

        const index = tableContext.expandedRowKeys.indexOf(key)
        if (index < 0) {
            tableContext.expandedRowKeys.push(key)
        } else {
            tableContext.expandedRowKeys.splice(index,1)
        }
    }

    const isExpandedKey = (key: Key): boolean => {
        return tableContext.expandedRowKeys.indexOf(key) < 0
    }

    /**
     * 更多筛选条件的展开和关闭
     */
    const onExpandedWrapperHandle = (state: boolean) => {
        tableContext.expandWrapperState = state
    }
    return {
        isExpandedKey,
        onExpandedClickHandle,
        onExpandedWrapperHandle,
        onSelectChange,
        onDeleteHandle,
        onAddOrUpdateHandle
    }
}