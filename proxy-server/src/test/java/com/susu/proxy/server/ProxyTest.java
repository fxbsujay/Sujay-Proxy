package com.susu.proxy.server;

import com.susu.proxy.core.common.eum.ProtocolType;
import com.susu.proxy.core.common.utils.ConfigLoadUtils;
import com.susu.proxy.core.common.utils.IpUtils;
import com.susu.proxy.core.task.TaskScheduler;
import com.susu.proxy.server.web.dto.MappingDTO;
import com.susu.proxy.server.web.service.ProxyService;
import com.susu.proxy.server.web.servlet.DispatchComponentProvider;
import org.junit.Test;

public class ProxyTest {

    private final TaskScheduler scheduler = new TaskScheduler("Test-Task");

    private final ProtocolType protocol = ProtocolType.TCP;

    @Test
    public void createProxyTests() throws InterruptedException {

        ConfigLoadUtils.refreshConfig(new String[0]);
        ServerApplication application = new ServerApplication();

        scheduler.scheduleOnce("creatProxy", () -> {
            ProxyService service = (ProxyService) DispatchComponentProvider.getInstance().getComponent(ProxyService.class.getSimpleName());

            MappingDTO dto = new MappingDTO();
            dto.setProtocol(protocol.getName());
            dto.setClientIp(IpUtils.getIp());
            dto.setServerPort(8899);
            dto.setClientPort(3309);

            service.save(dto);

        },2000);

        application.start();
    }
}
