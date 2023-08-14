export interface ProxyModel {
    name: string
    hostname: string
}

export interface MappingModel {
    protocol: string
    serverPort: number
    clientIp: string
    clientPort: number
}