package com.susu.proxy.server.web.dto;

import lombok.Data;

@Data
public class MappingDTO {

    /**
     * 代理协议  http or tcp
     */
    private String protocol;

    /**
     * 服务端端口
     */
    private Integer serverPort;

    /**
     * 代理客户端ip
     */
    private String clientIp;

    /**
     * 代理客户端端口
     */
    private Integer clientPort;
}
