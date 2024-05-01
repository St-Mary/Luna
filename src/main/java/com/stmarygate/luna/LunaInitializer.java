package com.stmarygate.luna;

import com.stmarygate.coral.network.BaseChannel;
import com.stmarygate.coral.network.codec.PacketDecoder;
import com.stmarygate.coral.network.codec.PacketEncoder;
import com.stmarygate.coral.utils.SSLContextUtils;
import com.stmarygate.luna.handlers.LunaLoginPacketHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslHandler;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Initializes a {@link SocketChannel} by configuring its {@link ChannelPipeline} with necessary */
public class LunaInitializer extends ChannelInitializer<SocketChannel> {

  private final Logger LOGGER = LoggerFactory.getLogger(LunaInitializer.class);

  public LunaInitializer() {}

  /**
   * Initializes the given {@link SocketChannel} by configuring its {@link ChannelPipeline}. Adds a
   * {@link PacketDecoder}, {@link PacketEncoder}, and the specified {@link BaseChannel} handler.
   *
   * @param ch The {@link SocketChannel} to be initialized.
   */
  @Override
  protected void initChannel(SocketChannel ch) {
    LunaChannel channel = new LunaChannel(LunaLoginPacketHandler.class);
    ChannelPipeline pipeline = ch.pipeline();
    try {
      SSLContext sslContext =
          SSLContextUtils.createAndInitSSLContext(
              new FileInputStream("./ssl/server.jks"), Constants.STOREPASS);

      SSLEngine engine = sslContext.createSSLEngine();
      engine.setUseClientMode(false);

      pipeline.addFirst("ssl", new SslHandler(engine));
      pipeline.addLast("decoder", new PacketDecoder());
      pipeline.addLast("encoder", new PacketEncoder());
    } catch (SSLException e) {
      LOGGER.error("SSL error: ", e);
    } catch (FileNotFoundException e) {
      LOGGER.error("SSL file not found: ", e);
    } catch (Exception e) {
      LOGGER.error("Error: ", e);
    }

    // Add the business logic handler
    pipeline.addLast("handler", channel);
  }
}
