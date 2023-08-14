package com.susu.proxy.server.web.controller;

import com.susu.proxy.server.web.annotation.Autowired;
import com.susu.proxy.server.web.annotation.RequestMapping;
import com.susu.proxy.server.web.annotation.RequestParam;
import com.susu.proxy.server.web.annotation.RestController;
import com.susu.proxy.server.web.dto.ClientDTO;
import com.susu.proxy.server.web.entity.Result;
import com.susu.proxy.server.web.service.ClientService;
import java.util.List;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @RequestMapping("/list")
    public Result<List<ClientDTO>> selectList(@RequestParam("name") String name) {
        List<ClientDTO> clients = clientService.selectList(name);
        return Result.ok(clients);
    }
}
