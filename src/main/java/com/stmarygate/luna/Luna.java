package com.stmarygate.luna;

import com.stmarygate.luna.constants.Constants;
import com.stmarygate.luna.database.DatabaseManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Luna {

  private static final Logger LOGGER = LoggerFactory.getLogger(Luna.class);
  private static final EventLoopGroup bossGroup = new NioEventLoopGroup();
  private static final EventLoopGroup workerGroup = new NioEventLoopGroup();
  private static final ServerBootstrap bootstrap = new ServerBootstrap();
  private static final LunaInitializer baseInitializer = new LunaInitializer();
  private static ChannelFuture future;

  /**
   * Start StMary's Gate
   *
   * @param args The arguments passed to the application.
   */
  public static void main(String[] args) {
    startLunaThread();
  }

  /** Start the Luna server thread. */
  private static void startLunaThread() {
    DatabaseManager.getSessionFactory();
    Thread serverThread = new Thread(Luna::startServer);
    serverThread.setName("LunaServer");
    serverThread.start();
  }

  /** Start the Luna server. */
  private static void startServer() {
    long time = System.currentTimeMillis();

    try {
      configureBootstrap();
      startServerAndLog(time);
    } catch (Exception e) {
      LOGGER.error("Failed to start Luna", e);
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }

  /** Configure the Luna server. */
  private static void configureBootstrap() {
    bootstrap
        .group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(baseInitializer);
  }

  /**
   * Start the Luna server and log the startup time.
   *
   * @param startTime The time the server started.
   */
  private static void startServerAndLog(long startTime) {
    try {
      future = bootstrap.bind(Constants.PORT).sync();

      LOGGER.info("Luna server started on port {}", Constants.PORT);
      LOGGER.info("Startup took {}ms", System.currentTimeMillis() - startTime);

      future.channel().closeFuture().sync();
    } catch (Exception e) {
      LOGGER.error("Failed to start Luna", e);
    } finally {
      close();
    }
  }

  /** Close the Luna server. */
  public static void close() {
    LOGGER.info("Closing Luna server...");

    if (future != null) {
      try {
        future.channel().close().sync();
      } catch (InterruptedException e) {
        LOGGER.error("Error while closing channel", e);
      }
    }

    bossGroup.shutdownGracefully();
    workerGroup.shutdownGracefully();
    System.exit(0);
  }
}
