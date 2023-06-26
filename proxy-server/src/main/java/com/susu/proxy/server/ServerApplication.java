package com.susu.proxy.server;

import com.susu.proxy.core.netty.AbstractChannelHandler;
import com.susu.proxy.core.netty.NetServer;
import com.susu.proxy.core.netty.msg.NetPacket;
import com.susu.proxy.core.task.TaskScheduler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**r
 * Hello world!
 *
 */
@Slf4j
public class ServerApplication {
    public static void main( String[] args ) {
        TaskScheduler scheduler = new TaskScheduler("TaskScheduler");
        NetServer server = new NetServer("Server",scheduler);

        server.addHandler(new AbstractChannelHandler() {
            @Override
            protected boolean handlePackage(ChannelHandlerContext ctx, NetPacket packet) throws Exception {

                System.out.println(packet.getData());
                return false;
            }

            @Override
            protected Set<Integer> interestPackageTypes() {
                return new HashSet<>();
            }
        });

        try {
            Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
            server.start(9988);
        } catch (Exception e) {
            log.info("Tracker Application Start Error!!");
            System.exit(1);
        }
    }
}
