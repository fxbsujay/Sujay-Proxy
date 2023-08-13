import { ClientModel } from '@/model/ClientModel'
import { ContentType, Method, RequestParams } from './http'
import https from './http/https'

export const clientListRequest = (param: RequestParams) => {
    return https().request<Array<ClientModel>>('/client/list', Method.GET, param, ContentType.form)
}
