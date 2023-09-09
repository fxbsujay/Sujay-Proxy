package com.susu.proxy.server.web.entity;

import lombok.Data;

@Data
public class UserInfo {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    public UserInfo(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
