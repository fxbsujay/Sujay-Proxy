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

    TCP(0, "TCP"),
    HTTP(1, "HTTP");

    private int value;

    private String name;

    public static ProtocolType getEnum(int value) {
        for (ProtocolType protocolType : values()) {
            if (protocolType.getValue() == value) {
                return protocolType;
            }
        }
        return null;
    }
}
