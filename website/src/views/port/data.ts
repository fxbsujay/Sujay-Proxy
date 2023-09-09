import { MappingModel } from '@/model/ProxyModel'
import { ProxyStateConstant } from '@/constant'

export const columns = [
    {
        title: '服务端口',
        dataIndex: 'serverPort',
        align: 'center',
    },
    {
        title: '代理客户端',
        dataIndex: 'clientIp',
        align: 'center',
    },
    {
        title: '代理端口',
        dataIndex: 'clientPort',
        align: 'center'
    },
    {
        title: '启动状态',
        dataIndex: 'binding',
        align: 'center',
        customRender: ({ text }) => text ? "成功" : "失败"
    },
    {
        title: '访客通道',
        dataIndex: 'state',
        align: 'center',
        customRender: ({ text }) => ProxyStateConstant.find(item => item.value === text)?.label
    },
    {
        title: '协议',
        dataIndex: 'protocol',
        align: 'center'
    },
    {
        title: '操作',
        dataIndex: 'operation',
        align: 'center'
    }
]

export interface Wrapper {
    port: string
}

export const defaultForm: MappingModel = {
    clientIp: 'localhost',
    clientPort: 3306,
    protocol: 'HTTP',
    serverPort: 3306,
    state: 'READY'
}

export const ruleList = {
    clientIp: [{ required: true, trigger: 'change', message: '请输入代理客户端Ip' }],
    clientPort: [{ required: true, trigger: 'change', message: '请输入代理客户端端口' }],
    serverPort: [{ required: true, trigger: 'change', message: '请输入服务端端口' }],
    protocol: [{ required: true, trigger: 'change', message: '请输入协议' }]
}