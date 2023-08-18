import { ClientStatusConstant } from '@/constant'

export const columns = [
    {
        title: '名称',
        dataIndex: 'name',
        align: 'center',
    },
    {
        title: '地址',
        dataIndex: 'hostname',
        align: 'center'
    },
    {
        title: '状态',
        dataIndex: 'status',
        align: 'center',
        customRender: ({ text }) => ClientStatusConstant.find(item => item.value === text).label
    },
    {
        title: '活跃时间',
        dataIndex: 'latestHeartbeatTime',
        align: 'center'
    }
]

export interface Wrapper {
    name: string
}