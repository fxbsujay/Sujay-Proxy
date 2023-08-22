package com.susu.proxy.server.web.servlet;

import com.susu.proxy.server.web.eum.ResponseStatusEnum;
import java.io.Serializable;

/**
 * <p>Description: Api 请求处理异常 </p>
 *
 * @author fxbsujay@gmail.com
 * @since 12:56 2023/08/22
 * @version 1.0 JDK1.8
 */
public class SysException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1;

    private final int code;

    private final String msg;

    public SysException(String msg) {
        this(ResponseStatusEnum.ERROR_500.getCode(), msg);
    }

    public SysException() {
        this(ResponseStatusEnum.ERROR_500);
    }

    public SysException(ResponseStatusEnum status) {
        this(status.getCode(), status.getMessage());
    }

    public SysException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String getMessage() {
        return getMsg();
    }

    @Override
    public String toString() {
        return "SysException{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
