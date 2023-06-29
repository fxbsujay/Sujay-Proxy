package com.susu.proxy.core;

import com.susu.proxy.core.common.Constants;
import com.susu.proxy.core.common.eum.PacketType;
import com.susu.proxy.core.netty.AbstractChannelHandler;
import com.susu.proxy.core.netty.NetServer;
import com.susu.proxy.core.netty.msg.NetPacket;
import com.susu.proxy.core.netty.msg.NetRequest;
import com.susu.proxy.core.task.TaskScheduler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>Description: Server NetWork Test</p>
 *
 * @author sujay
 * @since 15:56 2023/6/29
 * @version 1.0 JDK1.8
 */
@Slf4j
public class NetServerTests {

    @Test
    public void simpleServerTest() {
        NetServer server = new NetServer("Simple-Server");

        try {
            Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
            server.start(8899);
        } catch (Exception e) {
            log.info("Tracker Application Start Error!!");
            System.exit(1);
        }
    }

    @Test
    public void serverStartTest() {
        TaskScheduler scheduler = new TaskScheduler("Server-TaskScheduler");
        NetServer server = new NetServer("Test-Server",scheduler);

        server.addHandler(new AbstractChannelHandler() {
            @Override
            protected boolean handlePackage(ChannelHandlerContext ctx, NetPacket packet) throws Exception {
                NetRequest request = new NetRequest(ctx, packet);
                log.info("Handle Package: {}", new String(packet.getBody()));

                ctx.channel().writeAndFlush(NetPacket.buildPacket("Nice to meet you !".getBytes(), PacketType.EMPTY));
                ctx.writeAndFlush(NetPacket.buildPacket("Nice to meet you !!".getBytes(), PacketType.EMPTY));
                request.sendResponse(NetPacket.buildPacket("Nice to meet you !!!".getBytes(), PacketType.EMPTY), null);
                return false;
            }

            @Override
            protected Executor getExecutor() {
                return new ThreadPoolExecutor(Constants.HANDLE_THREAD_EXECUTOR_CORE_SIZE,Constants.HANDLE_THREAD_EXECUTOR_CORE_SIZE_MAX,
                        60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(Constants.HANDLE_THREAD_EXECUTOR_QUEUE_SIZE_MAX));
            }

            @Override
            protected Set<Integer> interestPackageTypes() {
                return new HashSet<>();
            }
        });

        try {
            Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
            server.start(8899);
        } catch (Exception e) {
            log.info("Tracker Application Start Error!!");
            System.exit(1);
        }
    }
}
