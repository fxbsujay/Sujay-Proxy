package com.susu.proxy.server.dto;

import lombok.Data;

/**
 * 端口映射
 */
@Data
public class PortMappingDTO {

    /**
     * 代理协议  http or tcp
     */
    private String protocol;

    /**
     * 服务端端口
     */
    private Integer serverPort;

    /**
     * 代理的客户端IP
     */
    private String  clientIp;

    /**
     * 代理的客户端端口
     */
    private String clientPort;

}
