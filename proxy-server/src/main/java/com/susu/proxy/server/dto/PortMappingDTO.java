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
     * 代理的客户端 ip:port  127.0.0.1:3306
     */
    private String clientAddress;
}
