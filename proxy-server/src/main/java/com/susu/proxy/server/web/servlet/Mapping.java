package com.susu.proxy.server.web.servlet;

import lombok.Data;

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
}
