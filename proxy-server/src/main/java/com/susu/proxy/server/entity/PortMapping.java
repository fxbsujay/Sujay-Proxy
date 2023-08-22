package com.susu.proxy.server.entity;

import com.susu.proxy.core.common.eum.ProtocolType;
import lombok.Data;

/**
 * 端口映射
 */
@Data
public class PortMapping {

    /**
     * 代理协议  http or tcp
     */
    private ProtocolType protocol;

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
    private String clientPort;
}
