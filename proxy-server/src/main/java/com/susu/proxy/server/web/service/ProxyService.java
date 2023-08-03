package com.susu.proxy.server.web.service;

import com.susu.proxy.server.entity.PortMapping;
import com.susu.proxy.server.proxy.PortInstantiationStrategy;
import com.susu.proxy.server.web.dto.MappingDTO;
import com.susu.proxy.server.web.servlet.InstantiationComponent;
import java.util.ArrayList;
import java.util.List;

public class ProxyService implements InstantiationComponent {

    private final PortInstantiationStrategy strategy;

    public ProxyService(PortInstantiationStrategy strategy) {
        this.strategy = strategy;
        instantiationComponent();
    }


    public List<MappingDTO> selectList() {
        List<PortMapping> mappings = strategy.getAllMapping();
        List<MappingDTO> result = new ArrayList<>();

        for (PortMapping mapping : mappings) {
            MappingDTO dto = new MappingDTO();
            dto.setProtocol(mapping.getProtocol());
            dto.setClientPort(mapping.getClientPort());
            dto.setClientIp(mapping.getClientIp());
            dto.setServerPort(mapping.getServerPort());
            result.add(dto);
        }

        return result;
    }

}
