package com.susu.proxy.server.web.dto;

import lombok.Data;

@Data
public class ClientDTO {

    /**
     * 客户端Name
     */
    private String name;

    /**
     * 节点地址
     */
    private String hostname;

    /**
     * 客户端状态
     */
    private int status;

    /**
     * 最近一次心跳时间
     */
    private String latestHeartbeatTime;
}
