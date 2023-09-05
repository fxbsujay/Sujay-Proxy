package com.susu.proxy.server;

import com.susu.proxy.core.common.eum.ProtocolType;
import com.susu.proxy.core.common.utils.ConfigLoadUtils;
import com.susu.proxy.core.task.TaskScheduler;
import com.susu.proxy.server.web.dto.MappingDTO;
import com.susu.proxy.server.web.service.ProxyService;
import com.susu.proxy.server.web.servlet.DispatchComponentProvider;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class ProxyTest {

    private final TaskScheduler scheduler = new TaskScheduler("Test-Task");

    private final ProtocolType protocol = ProtocolType.HTTP;

    @Test
    public void createProxyTests() throws InterruptedException {

        ConfigLoadUtils.refreshConfig();
        ServerApplication application = new ServerApplication();

        scheduler.scheduleOnce("creatProxy", () -> {
            ProxyService service = (ProxyService) DispatchComponentProvider.getInstance().getComponent(ProxyService.class.getSimpleName());

            MappingDTO dto = new MappingDTO();
            dto.setProtocol(protocol.getName());
            dto.setClientIp("172.23.96.1");
            dto.setServerPort(8896);
            dto.setClientPort(8846);

            service.save(dto);

        },2000);

        application.start();
    }
}
