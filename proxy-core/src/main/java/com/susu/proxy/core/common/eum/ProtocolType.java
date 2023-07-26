package com.susu.proxy.core.common.eum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 代理协议类型
 *
 * @author sujay
 * @since 13:08 2022/04/25
 * @version 1.0 JDK1.8 <br/>
 */
@Getter
@AllArgsConstructor
public enum ProtocolType {

    TCP( "TCP"),
    HTTP("HTTP");

    private String name;

    public static ProtocolType getEnum(String name) {
        for (ProtocolType protocolType : values()) {
            if (protocolType.getName().equals(name)) {
                return protocolType;
            }
        }
        return null;
    }
}
