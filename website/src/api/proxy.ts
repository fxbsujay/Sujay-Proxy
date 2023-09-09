import { ClientModel, MappingModel } from '@/model/ProxyModel'
import { ContentType, Method, RequestParams } from './http'
import https from './http/https'

export const clientListRequest = (param: RequestParams) => {
    return https().request<Array<ClientModel>>('/client/list', Method.GET, param, ContentType.form)
}

export const mappingListRequest = (param: RequestParams) => {
    return https().request<Array<MappingModel>>('/port/list', Method.GET, param, ContentType.form)
}

export const mappingSaveRequest = (data: MappingModel) => {
    return https().request<boolean>('/port/save', Method.POST, data, ContentType.json)
}

export const mappingDeleteRequest = (port: number) => {
    return https().request<boolean>('/port/delete/' + port, Method.DELETE)
}

export const startProxyServerRequest = (param: RequestParams) => {
    return https().request<Array<ClientModel>>('/port/start', Method.GET, param, ContentType.form)
}
export const closeProxyServerRequest = (param: RequestParams) => {
    return https().request<Array<ClientModel>>('/port/close', Method.GET, param, ContentType.form)
}