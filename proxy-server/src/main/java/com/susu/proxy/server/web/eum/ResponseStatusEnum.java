package com.susu.proxy.server.web.eum;

import lombok.Getter;

/**
 * <p>Description: Controller 响应状态码</p>
 *
 * @author fxbsujay@gmail.com
 * @since 10:20 2023/08/01
 * @version 1.0 JDK1.8
 */
@Getter
public enum ResponseStatusEnum {

    SUCCESS_200(200, "操作成功"),

    /**
     *  系统异常
     */
    ERROR_404(404,"未找到对应资源"),
    ERROR_500(500,"操作失败");

    /**
     * code 编码
     */
    private final Integer code;

    /**
     * message 消息内容
     */
    private final String message;

    ResponseStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
