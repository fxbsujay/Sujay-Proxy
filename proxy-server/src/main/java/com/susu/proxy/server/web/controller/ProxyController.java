package com.susu.proxy.server.web.controller;

import com.susu.proxy.server.web.annotation.Autowired;
import com.susu.proxy.server.web.annotation.RequestBody;
import com.susu.proxy.server.web.annotation.RequestMapping;
import com.susu.proxy.server.web.annotation.RestController;
import com.susu.proxy.server.web.dto.MappingDTO;
import com.susu.proxy.server.web.entity.Result;
import com.susu.proxy.server.web.service.ProxyService;
import java.util.List;

@RestController
@RequestMapping("/api/port")
public class ProxyController {

    @Autowired
    private ProxyService proxyService;

    @RequestMapping("/list")
    public Result<List<MappingDTO>> selectList() {
        List<MappingDTO> clients = proxyService.selectList();
        return Result.ok(clients);
    }

    @RequestMapping(value="save", method = "POST")
    public Result<Boolean> save(@RequestBody MappingDTO mapping) {
        return Result.ok(true);
    }
}
