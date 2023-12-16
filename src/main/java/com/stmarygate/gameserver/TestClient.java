package com.stmarygate.gameserver;

import com.stmarygate.common.network.BaseChannel;
import com.stmarygate.common.network.BaseInitializer;
import com.stmarygate.gameserver.handlers.client.ClientLoginPacketHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(TestClient.class);
  private static final EventLoopGroup workerGroup = new NioEventLoopGroup();
  private static final BaseChannel baseChannel = new BaseChannel(ClientLoginPacketHandler.class);
  private static final BaseInitializer baseInitializer = new BaseInitializer(baseChannel);

  /**
   * Start the test client.
   *
   * @param args The arguments passed to the application.
   */
  public static void main(String[] args) {
    try {
      long time = System.currentTimeMillis();

      Bootstrap b = new Bootstrap();
      configureBootstrap(b);

      try {
        ChannelFuture f = b.connect("localhost", Constants.PORT).sync();
        LOGGER.info("Time start: " + (System.currentTimeMillis() - time) + "ms");
        f.channel().closeFuture().sync();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    } finally {
      workerGroup.shutdownGracefully();
    }
  }

  /**
   * Configure the bootstrap.
   *
   * @param b The bootstrap to configure.
   */
  private static void configureBootstrap(Bootstrap b) {
    b.group(workerGroup);
    b.channel(NioSocketChannel.class);
    b.handler(baseInitializer);
  }
}
