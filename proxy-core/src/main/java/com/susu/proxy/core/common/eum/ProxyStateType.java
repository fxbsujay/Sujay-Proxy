package com.susu.proxy.core.common.eum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProxyStateType {

    READY( "READY"),
    RUNNING("RUNNING"),
    CLOSE("CLOSE");

    private String name;

    public static ProxyStateType getEnum(String name) {
        for (ProxyStateType stateType : values()) {
            if (stateType.getName().equals(name)) {
                return stateType;
            }
        }
        return null;
    }
}
