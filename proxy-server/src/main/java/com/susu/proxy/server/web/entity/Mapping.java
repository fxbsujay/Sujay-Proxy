package com.susu.proxy.server.web.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Description: Controller 接口映射</p>
 *
 * @author sujay
 * @since 10:20 2023/08/01
 * @version 1.0 JDK1.8
 */
@Data
public class Mapping {

    /**
     * 请求URL
     */
    private String url;

    /**
     * 请求方式
     */
    private String method;

    /**
     * 调用的方法
     */
    private Method invokeMethod;

    private List<ParamMetadata> parameterList = new LinkedList<>();

    public void addParameterList(Type type, String paramKey, Class<?> paramClassType) {
        parameterList.add(new ParamMetadata(type, paramKey, paramClassType));
    }

    @Data
    @AllArgsConstructor
    public static class ParamMetadata {
        Type type;
        String paramKey;
        Class<?> paramClassType;
    }

    public enum Type {
        /**
         * 路径参数
         */
        PATH_VARIABLE,
        /**
         * 实体类参数
         */
        REQUEST_BODY,
        /**
         *  其他已有类型参数
         */
        QUERY_ENTITY
    }
}
