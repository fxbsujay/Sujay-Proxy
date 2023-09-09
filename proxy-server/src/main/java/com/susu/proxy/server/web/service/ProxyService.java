package com.susu.proxy.server.web.service;

import com.susu.proxy.core.common.eum.PacketType;
import com.susu.proxy.core.common.eum.ProtocolType;
import com.susu.proxy.core.common.eum.ProxyStateType;
import com.susu.proxy.core.common.model.CloseProxyRequest;
import com.susu.proxy.core.common.model.ProxyRequest;
import com.susu.proxy.core.common.utils.StringUtils;
import com.susu.proxy.core.netty.msg.NetPacket;
import com.susu.proxy.server.client.MasterClientManager;
import com.susu.proxy.core.common.entity.PortMapping;
import com.susu.proxy.server.proxy.PortInstantiationStrategy;
import com.susu.proxy.server.web.dto.MappingDTO;
import com.susu.proxy.server.web.servlet.InstantiationComponent;
import com.susu.proxy.server.web.servlet.SysException;
import java.util.ArrayList;
import java.util.List;

public class ProxyService implements InstantiationComponent {

    private final MasterClientManager clientManager;

    private final PortInstantiationStrategy strategy;

    public ProxyService(PortInstantiationStrategy strategy, MasterClientManager clientManager) {
        this.clientManager = clientManager;
        this.strategy = strategy;
        instantiationComponent();
    }

    public List<MappingDTO> selectList(String port) {
        List<PortMapping> mappings = strategy.getAllMapping();
        List<MappingDTO> result = new ArrayList<>();

        for (PortMapping mapping : mappings) {

            if (StringUtils.isNotBlank(port) && !port.equals(String.valueOf(mapping.getClientPort()))) {
                continue;
            }

            MappingDTO dto = new MappingDTO();
            dto.setProtocol(mapping.getProtocol().getName());
            dto.setClientPort(mapping.getClientPort());
            dto.setClientIp(mapping.getClientIp());
            dto.setServerPort(mapping.getServerPort());
            dto.setBinding(mapping.isBinding());

            if (clientManager.isExist(mapping.getClientIp()) && mapping.isBinding()) {
               if (strategy.isConnectionExists(mapping.getServerPort())) {
                   dto.setState(ProxyStateType.RUNNING.getName());
               } else {
                   dto.setState(ProxyStateType.READY.getName());
               }
            } else {
                dto.setState(ProxyStateType.CLOSE.getName());
            }
            result.add(dto);
        }

        return result;
    }

    public void save(MappingDTO dto) {
        PortMapping mapping = new PortMapping();
        mapping.setClientIp(dto.getClientIp());

        ProtocolType protocol = ProtocolType.getEnum(dto.getProtocol());
        if (protocol == null) {
            throw new SysException("协议类型不支持");
        }

        if (dto.getServerPort() == null || strategy.isExist(dto.getServerPort())) {
            throw new SysException("该端口已存在：" + dto.getServerPort());
        }

        mapping.setProtocol(protocol);
        mapping.setServerPort(dto.getServerPort());
        mapping.setClientPort(dto.getClientPort());

        boolean createResult = strategy.createMapping(mapping);

        if (!createResult) {
            throw new SysException("创建失败，请查看端口" + mapping.getServerPort() + "是否被占用");
        }

        if (clientManager.isExist(mapping.getClientIp())) {

            ProxyRequest request = ProxyRequest.newBuilder()
                    .setProtocol(mapping.getProtocol().getName())
                    .setClientPort(mapping.getClientPort())
                    .setServerPort(mapping.getServerPort())
                    .setClientIp(mapping.getClientIp())
                    .build();

            NetPacket nettyPacket = NetPacket.buildPacket(request.toByteArray(), PacketType.SERVER_CREATE_PROXY);

            try {
                clientManager.send(mapping.getClientIp(), nettyPacket);
            } catch (InterruptedException e) {
                throw new SysException(e.getMessage());
            }
        }
    }

    public void delete(Integer port) {
        PortMapping mapping = strategy.removeMapping(port);

        if (mapping == null || !clientManager.isExist(mapping.getClientIp())) {
            return;
        }

        CloseProxyRequest request = CloseProxyRequest.newBuilder()
                .setClientIp(mapping.getClientIp())
                .setClientPort(mapping.getClientPort())
                .build();

        NetPacket nettyPacket = NetPacket.buildPacket(request.toByteArray(), PacketType.SERVER_REMOVE_PROXY);

        try {
            clientManager.send(mapping.getClientIp(),nettyPacket);
        } catch (InterruptedException e) {
            throw new SysException(e.getMessage());
        }

    }
}
