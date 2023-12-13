package com.stmarygate.gameserver;

import com.stmarygate.common.network.BaseChannel;
import com.stmarygate.common.network.BaseInitializer;
import com.stmarygate.common.network.ClientSession;
import com.stmarygate.common.network.PacketHandler;
import com.stmarygate.common.network.packets.Packet;
import com.stmarygate.common.network.packets.client.PacketVersion;
import com.stmarygate.gameserver.Luna;
import com.stmarygate.gameserver.handlers.BaseInitializer2;
import com.stmarygate.gameserver.handlers.LunaClientChannelHandler;
import com.stmarygate.gameserver.handlers.LunaLoginPacketHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestClient.class);
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8446;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            BaseChannel channel = new LunaClientChannelHandler(LunaLoginPacketHandler.class);
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.handler(new BaseInitializer2(channel));

            ChannelFuture f = b.connect("localhost", port).sync();
            LOGGER.info("Client started");
            channel.getSession().write(new PacketVersion(0, 0, 1, "SNAPSHOT"));
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
