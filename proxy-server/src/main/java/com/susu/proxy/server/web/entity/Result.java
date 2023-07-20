package com.susu.proxy.server.web.entity;

import com.susu.proxy.server.web.eum.ResponseStatusEnum;
import lombok.Data;

/**
 * <p>Description: 结果返回类</p>
 * @author sujay
 * @version 17:36 2022/07/17
 */
@Data
public class Result<T> {

    /**
     * 编码
     **/
    private int code;

    /**
     * 消息内容
     **/
    private String msg;

    /**
     * 数据
     **/
    private T data;

    private Result() {
        this(ResponseStatusEnum.SUCCESS_200.getCode(), ResponseStatusEnum.SUCCESS_200.getMessage());
    }

    private Result(T data) {
        this(ResponseStatusEnum.SUCCESS_200.getCode(), ResponseStatusEnum.SUCCESS_200.getMessage(), data);
    }

    private Result(Integer code, String msg) {
        this(code, msg, null);
    }

    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Result<String> ok() {
        return new Result<>();
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(data);
    }

    public static <T> Result<T> error() {
        return new Result<>(ResponseStatusEnum.ERROR_500.getCode(), ResponseStatusEnum.ERROR_500.getMessage());
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(ResponseStatusEnum.ERROR_500.getCode(),msg);
    }

    public static <T> Result<T> error(Integer code,String msg) {
        return new Result<>(code,msg);
    }

    public static <T> Result<T> error(ResponseStatusEnum e) {
        return new Result<>(e.getCode(),e.getMessage());
    }

}
