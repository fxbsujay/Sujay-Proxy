package com.susu.proxy.server.web.controller;

import com.susu.proxy.server.web.annotation.Autowired;
import com.susu.proxy.server.web.annotation.RequestBody;
import com.susu.proxy.server.web.annotation.RequestMapping;
import com.susu.proxy.server.web.annotation.RestController;
import com.susu.proxy.server.web.dto.LoginDTO;
import com.susu.proxy.server.web.entity.Result;
import com.susu.proxy.server.web.service.AuthService;
import com.susu.proxy.server.web.servlet.JwtUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 登录认证</p>
 *
 * @author sujay
 * @version 17:12 2023/08/02
 */
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    @RequestMapping( value = "/login", method = "POST")
    public Result<String> login(@RequestBody LoginDTO dto) {
        if (authService.verify(dto.getUsername(), dto.getPassword())) {
            return Result.ok(JwtUtils.encryption(dto.getUsername()));
        }

        return Result.error("账户密码错误");
    }
}
