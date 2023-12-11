package com.stmarygate.gameserver;

import com.stmarygate.common.network.BaseChannel;
import com.stmarygate.common.network.BaseInitializer;
import com.stmarygate.common.network.PacketHandler;
import com.stmarygate.common.network.packets.Packet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Luna {

  private static final Logger LOGGER = LoggerFactory.getLogger(Luna.class);;
  private static final EventLoopGroup bossGroup = new NioEventLoopGroup();
  private static final EventLoopGroup workerGroup = new NioEventLoopGroup();
  private static final ServerBootstrap bootstrap = new ServerBootstrap();

  /**
   * Start StMary's Gate
   *
   * @param args The arguments passed to the application.
   */
  public static void main(String[] args) {
    LOGGER.info("Starting StMary's Gate");
    start(2222);
  }

  private static void start(int port) {
    Long time = System.currentTimeMillis();
    BaseChannel channel = new BaseChannel(PacketHandler.class);

    bootstrap
        .group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .localAddress(port)
        .childHandler(new BaseInitializer(channel, Packet.PacketType.CLIENT_MSG));

    try {
      LOGGER.info("StMary's Gate started on port {}", port);
      LOGGER.info("Startup took {}ms", System.currentTimeMillis() - time);
      bootstrap.bind().awaitUninterruptibly().channel().closeFuture().awaitUninterruptibly();
    } catch (Exception e) {
      LOGGER.error("Failed to start StMary's Gate", e);
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}
