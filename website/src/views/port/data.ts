export const columns = [
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
        title: '服务端口',
        dataIndex: 'serverPort',
        align: 'center',
    },
    {
        title: '协议',
        dataIndex: 'protocol',
        align: 'center'
    }
]

export interface Wrapper {
    port: string
}