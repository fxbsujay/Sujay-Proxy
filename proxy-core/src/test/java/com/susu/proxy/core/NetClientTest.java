package com.susu.proxy.core;

import com.susu.proxy.core.common.eum.PacketType;
import com.susu.proxy.core.netty.NetClient;
import com.susu.proxy.core.netty.msg.NetPacket;
import com.susu.proxy.core.task.TaskScheduler;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import java.io.IOException;

/**
 * <p>Description: Client NetWork Test</p>
 * @author sujay
 * @since 15:56 2023/6/29
 * @version 1.0 JDK1.8
 */
@Slf4j
public class NetClientTest {

    @Test
    public void simpleClientTest() throws InterruptedException {
        NetClient client = new NetClient("Test-Client");
        client.addPackageListener(request -> log.info("Handle Package: {}", request.getRequest().bodyToString()));

        client.start("localhost",8845);
        client.send(NetPacket.buildPacket("Simple Client Message Test !!", PacketType.EMPTY));
        try {
            client.ensureStart();
            System.in.read();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            client.shutdown();
        }
    }

    @Test
    public void clientStartTest() {
        TaskScheduler scheduler = new TaskScheduler("Client-TaskScheduler");
        NetClient client = new NetClient("Test-Client", scheduler);

        client.addPackageListener(request -> log.info("Handle Package: {}", request.getRequest().bodyToString()));

        client.addConnectListener( isConnected -> {
            log.info("NetClient Connect Start : {}", isConnected);
            if (isConnected) {
                client.send(NetPacket.buildPacket("Hello World !!", PacketType.EMPTY));
            }
        });

        client.addClientFailListener(() -> {
            log.info("NetServer Down !!");
        });

        client.start("localhost",8845);

        NetPacket response = null;
        try {
            response = client.sendSync(NetPacket.buildPacket("Sync Message Test !!", PacketType.EMPTY));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("Response : {}", response);

        try {
            client.ensureStart();
            System.in.read();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            scheduler.shutdown();
            client.shutdown();
        }

    }
}
