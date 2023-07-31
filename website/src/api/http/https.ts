/**
 * <p>请求客户端配置</p>
 * @author fxbsujay@gmail.com
 * @version 13:24 2022/6/3
 */
import HttpClient, { ContentType, HttpClientConfig } from './index'
import { SysConstant } from '@/constant/sys'
const https = () => {
  const config: HttpClientConfig = {
    headers: {
      'content-type': ContentType.json
    },
    baseURL: SysConstant.baseApiPrefix,
    clickInterval: 1000,
    timeout: 100000,
    timeoutErrorMessage: '请求超时'
  }
  return new HttpClient(config)
}

export default https


