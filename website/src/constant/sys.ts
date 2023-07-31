/**
 * <p>系统常量 </p>
 * @author fxbsujay@gmail.com
 * @version 15:59 2022/6/22
 */
export class SysConstant {

    /**
     * 路径前缀
     */
    public static baseApiPrefix: string = '/api'

    /**
     * 静态文件路径
     */
    public static baseStaticFileUrl: string = '/api/file'

    /**
     * 基础地址
     */
    public static baseUrl: string = window.location.protocol + "//" + window.location.host

    /**
     * 系统地址
     */
    public static baseApiUrl: string = SysConstant.baseUrl + SysConstant.baseApiPrefix

    /**
     * 系统名称
     */
    public static title: string = 'A'

    /**
     * 备案号
     */
    public static record: string = 'A'

}