package com.susu.proxy.server.web.controller;

import com.susu.proxy.server.web.annotation.RequestBody;
import com.susu.proxy.server.web.annotation.RequestMapping;
import com.susu.proxy.server.web.annotation.RestController;
import com.susu.proxy.server.web.dto.LoginDTO;
import com.susu.proxy.server.web.entity.Result;

/**
 * <p>Description: 登录认证</p>
 *
 * @author sujay
 * @version 17:12 2023/08/02
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String ADMIN = "admin";

    @RequestMapping( value = "/login", method = "POST")
    public Result<String> login(@RequestBody LoginDTO dto) {
        if (ADMIN.equalsIgnoreCase(dto.getUsername()) && ADMIN.equalsIgnoreCase(dto.getPassword())) {
            return Result.ok("123456");
        }
        return Result.error("密码错误");
    }
}
