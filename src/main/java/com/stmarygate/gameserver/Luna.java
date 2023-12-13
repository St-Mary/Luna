package com.stmarygate.gameserver;

import com.stmarygate.common.network.BaseChannel;
import com.stmarygate.common.network.BaseInitializer;
import com.stmarygate.common.network.PacketHandler;
import com.stmarygate.common.network.packets.Packet;
import com.stmarygate.common.network.packets.PacketBuffer;
import com.stmarygate.common.network.packets.Protocol;
import com.stmarygate.gameserver.handlers.BaseInitializer2;
import com.stmarygate.gameserver.handlers.LunaLoginChannel;
import com.stmarygate.gameserver.handlers.LunaLoginPacketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Luna {

  private static final Logger LOGGER = LoggerFactory.getLogger(Luna.class);
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup();
  private static final EventLoopGroup workerGroup = new NioEventLoopGroup();
  private static final ServerBootstrap bootstrap = new ServerBootstrap();

  /**
   * Start StMary's Gate
   *
   * @param args The arguments passed to the application.
   */
  public static void main(String[] args) throws InterruptedException {
    LOGGER.info("Starting StMary's Gate");
    Thread th = new Thread(() -> startServer(8446));
    th.start();
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    try {
      TestClient.main(null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void startServer(int port) {
    long time = System.currentTimeMillis();
    BaseInitializer baseInitializer = new BaseInitializer(new LunaLoginChannel(LunaLoginPacketHandler.class));

    bootstrap
        .group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(baseInitializer);

    try {
      ChannelFuture future = bootstrap.bind(port).sync();
      LOGGER.info("StMary's Gate started on port {}", port);
      LOGGER.info("Startup took {}ms", System.currentTimeMillis() - time);
      future.channel().closeFuture().sync();
    } catch (Exception e) {
      LOGGER.error("Failed to start StMary's Gate", e);
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}
