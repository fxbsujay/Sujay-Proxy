package com.susu.proxy.server.web.controller;

import com.susu.proxy.server.web.annotation.*;
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
    public Result<List<MappingDTO>> selectList(@RequestParam("port") String port) {
        List<MappingDTO> clients = proxyService.selectList(port);
        return Result.ok(clients);
    }

    @RequestMapping(value="save", method = "POST")
    public Result<Boolean> save(@RequestBody MappingDTO mapping) {
        Boolean save = proxyService.save(mapping);
        return Result.ok(save);
    }
}