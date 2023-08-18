package com.susu.proxy.server.web.service;

import com.susu.proxy.core.common.utils.DateUtils;
import com.susu.proxy.core.common.utils.StringUtils;
import com.susu.proxy.server.client.ClientInfo;
import com.susu.proxy.server.client.MasterClientManager;
import com.susu.proxy.server.web.dto.ClientDTO;
import com.susu.proxy.server.web.servlet.InstantiationComponent;
import java.util.ArrayList;
import java.util.List;

public class ClientService implements InstantiationComponent {

    private final MasterClientManager clientManager;

    public ClientService(MasterClientManager clientManager) {
        this.clientManager = clientManager;
        instantiationComponent();
    }

    public List<ClientDTO> selectList(String name) {
        List<ClientInfo> clientList = clientManager.getClientList();
        List<ClientDTO> result = new ArrayList<>();

        for (ClientInfo clientInfo : clientList) {

            if (clientInfo.getStatus() == ClientInfo.STATUS_INIT || (StringUtils.isNotBlank(name) && !clientInfo.getName().equals(name))) {
                continue;
            }

            ClientDTO dto = new ClientDTO();
            dto.setHostname(clientInfo.getHostname());
            dto.setName(clientInfo.getName());
            dto.setStatus(clientInfo.getStatus());
            dto.setLatestHeartbeatTime(DateUtils.getTime(clientInfo.getLatestHeartbeatTime(), DateUtils.PATTERN_WHOLE));
            result.add(dto);
        }

        return result;
    }
}
