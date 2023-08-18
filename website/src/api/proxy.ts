import { ClientModel, MappingModel } from '@/model/ProxyModel'
import { ContentType, Method, RequestParams } from './http'
import https from './http/https'

export const clientListRequest = (param: RequestParams) => {
    return https().request<Array<ClientModel>>('/client/list', Method.GET, param, ContentType.form)
}

export const mappingListRequest = (param: RequestParams) => {
    return https().request<Array<MappingModel>>('/port/list', Method.GET, param, ContentType.form)
}