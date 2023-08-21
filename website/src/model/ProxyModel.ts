export interface ClientModel {
    name: string
    hostname: string
}

export interface MappingModel {
    id: string
    protocol: string
    serverPort: number
    clientIp: string
    clientPort: number
}