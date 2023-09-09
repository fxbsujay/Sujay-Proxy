export interface ClientModel {
    name: string
    hostname: string
}

export interface MappingModel {
    protocol: string
    serverPort: number
    clientIp: string
    clientPort: number
    state: string
    binding?: boolean
}