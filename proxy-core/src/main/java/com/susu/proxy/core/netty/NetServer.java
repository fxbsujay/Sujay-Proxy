package com.susu.proxy.core.netty;

import com.susu.proxy.core.task.TaskScheduler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>Description: Server NetWork</p>
 * <p>Description: Netty 的 服务端实现 网络服务</p>
 *
 * @author sujay
 * @since 15:56 2023/6/29
 * @version 1.0 JDK1.8
 */
@Slf4j
public class NetServer {

    /**
     * 服务名称
     */
    private String name;

    /**
     * 任务调度器
     */
    private TaskScheduler taskScheduler;

    /**
     * 消息管理器
     */
    private BaseChannelHandler baseChannelHandler;

    private EventLoopGroup boss;

    private EventLoopGroup worker;

    private ServerBootstrap bootstrap;

    private final AtomicBoolean stated = new AtomicBoolean(false);

    public NetServer(String name) {
        this(name, new TaskScheduler("NetServer-Scheduler"));
    }

    /**
     * @param name 启动的节点名称
     */
    public NetServer(String name, TaskScheduler taskScheduler) {
        this.name = name;
        this.boss = new NioEventLoopGroup();
        this.worker = new NioEventLoopGroup();
        this.baseChannelHandler = new BaseChannelHandler();
        this.taskScheduler = taskScheduler;
    }

    /**
     * 异步绑定端口
     * <p>Description: start server </p>
     *
     * @param port 端口
     */
    public void startAsync(Integer... port) {
        taskScheduler.scheduleOnce(name, () -> {
            try {
                start(Arrays.asList(port));
            } catch (InterruptedException e) {
                log.info("NetServer internalBind is Interrupted !!");
            }
        }, 0);
    }

    /**
     * 启动服务, 会堵塞线程
     * <p>Description: start server </p>
     *
     * @param ports 端口
     * @exception InterruptedException 绑定端口异常
     */
    public void start(int port) throws InterruptedException {
        start(Collections.singletonList(port));
    }

    /**
     * 启动服务
     * <p>Description: start server </p>
     *
     * @param ports 端口
     * @exception InterruptedException 绑定端口异常
     */
    public void start(List<Integer> ports) throws InterruptedException {
        if (!stated.get()) {
            bootstrap = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childHandler(baseChannelHandler);
        } else {
            log.error("NetServer failed to start, already starting : {}", name);
            return;
        }

        stated.set(true);

        log.info("NetServer started : {}", name);

        if (ports == null || ports.isEmpty()) {
            return;
        }

        try {
            bind(ports);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    /**
     * 绑定端口
     * <p>Description: bind port </p>
     *
     * @param ports 端口号
     * @throws InterruptedException 绑定端口异常
     */
    public void bindAsync(List<Integer> ports) throws InterruptedException {

        synchronized (NetServer.this) {
            while (bootstrap == null) {
                wait(20);
            }
        }

        taskScheduler.scheduleOnce("", () -> {
            try {
                bind(ports);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void bindAsync(int port) throws InterruptedException {
        bindAsync(Collections.singletonList(port));
    }

    public void bind(List<Integer> ports) throws InterruptedException {
        List<ChannelFuture> channelFeature = new ArrayList<>();
        for (Integer port : ports) {
            ChannelFuture future = bootstrap.bind(port).sync();
            log.info("[{}] NetServer bind port : {}", name, port);
            channelFeature.add(future);
        }

        for (ChannelFuture future : channelFeature) {
            future.channel().closeFuture().addListener((ChannelFutureListener) f -> f.channel().close());
        }

        for (ChannelFuture future : channelFeature) {
            future.channel().closeFuture().sync();
        }
    }

    /**
     * 添加自定义的handler
     */
    public void addHandler(AbstractChannelHandler handlers) {
        baseChannelHandler.addHandler(handlers);
    }

    public void setBaseChannelHandler(BaseChannelHandler baseChannelHandler) {
        this.baseChannelHandler = baseChannelHandler;
    }

    /**
     * 关闭服务端
     * <p>Description: shutdown server </p>
     */
    public void shutdown() {
        log.info("Shutdown NetServer : {}", name);
        if (stated.compareAndSet(true, false)) {

            if (boss != null && worker != null) {
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            }
        }
    }
}
