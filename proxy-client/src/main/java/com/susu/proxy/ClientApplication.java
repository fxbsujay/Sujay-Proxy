package com.susu.proxy;


import com.susu.proxy.core.common.eum.PacketType;
import com.susu.proxy.core.netty.NetClient;
import com.susu.proxy.core.netty.listener.NetPacketListener;
import com.susu.proxy.core.netty.msg.NetPacket;
import com.susu.proxy.core.netty.msg.NetRequest;
import com.susu.proxy.core.task.TaskScheduler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientApplication {

    public static void main( String[] args ) {
        TaskScheduler scheduler = new TaskScheduler("Client-TaskScheduler");
        NetClient client = new NetClient("Client", scheduler);

        client.addPackageListener(new NetPacketListener() {
            @Override
            public void onMessage(NetRequest packet) throws Exception {
                log.info(packet.getRequest().getData());
            }
        });
        client.addConnectListener( isConnected -> {
            log.info("Tracker Client Connect Start : {}", isConnected);
            if (isConnected) {
                client.send(NetPacket.buildPacket("A", PacketType.EMPTY));

                client.send(NetPacket.buildPacket("B", PacketType.EMPTY));
            }
        });
        client.addClientFailListener(() -> {
            log.info("Tracker Server Down !!");
        });
        client.start("localhost",9986);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
