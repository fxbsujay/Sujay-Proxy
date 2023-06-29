package com.susu.proxy.core;

import com.susu.proxy.core.common.eum.PacketType;
import com.susu.proxy.core.netty.NetClient;
import com.susu.proxy.core.netty.msg.NetPacket;
import com.susu.proxy.core.task.TaskScheduler;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * <p>Description: Client NetWork Test</p>
 * @author sujay
 * @since 15:56 2023/6/29
 * @version 1.0 JDK1.8
 */
@Slf4j
public class NetClientTest {

    @Test
    public void simpleClientTest() {
        NetClient client = new NetClient("Test-Client");

        try {
            client.start("localhost",8899);
            client.ensureStart();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            client.shutdown();
        }
    }

    @Test
    public void clientStartTest() {
        TaskScheduler scheduler = new TaskScheduler("Client-TaskScheduler");
        NetClient client = new NetClient("Test-Client", scheduler);

        client.addPackageListener(request -> log.info("Handle Package: {}", new String(request.getRequest().getBody())));

        client.addConnectListener( isConnected -> {
            log.info("Tracker Client Connect Start : {}", isConnected);
            if (isConnected) {
                client.send(NetPacket.buildPacket("Hello World !!".getBytes(), PacketType.EMPTY));
            }
        });

        client.addClientFailListener(() -> {
            log.info("Tracker Server Down !!");
        });

        client.start("localhost",8899);

        NetPacket response = null;
        try {
            response = client.sendSync(NetPacket.buildPacket("Sync Message Test !!".getBytes(), PacketType.EMPTY));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("Response : {}", response);

        try {
            client.ensureStart();
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            scheduler.shutdown();
            client.shutdown();
        }

    }
}
