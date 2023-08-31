import { Constant } from '@/model/BaseObject'

/**
 * 协议类型
 */
export const ProtocolConstant: Constant[] = [
    {
        value: 'TCP',
        label: 'TCP'
    },
    {
        value: 'HTTP',
        label: 'HTTP'
    }
]

/**
 * 客户端状态
 */
export const ClientStatusConstant: Constant[] = [
    {
        value: 2,
        label: '活跃'
    },
    {
        value: 3,
        label: '断线'
    }
]

/**
 * 代理状态
 */
export const ProxyStateConstant: Constant[] = [
    {
        value: 'READY',
        label: '准备就绪'
    },
    {
        value: 'RUNNING',
        label: '运行中'
    },
    {
        value: 'CLOSE',
        label: '关闭'
    }
]