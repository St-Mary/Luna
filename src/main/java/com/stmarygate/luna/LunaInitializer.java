package com.stmarygate.luna;

import com.stmarygate.coral.network.BaseChannel;
import com.stmarygate.coral.network.BaseInitializer;
import com.stmarygate.coral.network.codec.PacketDecoder;
import com.stmarygate.coral.network.codec.PacketEncoder;
import com.stmarygate.coral.utils.SSLContextUtils;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslHandler;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.*;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;

public class LunaInitializer extends BaseInitializer {
  /** The {@link BaseChannel} responsible for handling business logic. */
  private final BaseChannel channel;

  public LunaInitializer(BaseChannel channel) {
    super(channel);
    this.channel = channel;
  }

  /**
   * Initializes the given {@link SocketChannel} by configuring its {@link ChannelPipeline}. Adds a
   * {@link PacketDecoder}, {@link PacketEncoder}, and the specified {@link BaseChannel} handler.
   *
   * @param ch The {@link SocketChannel} to be initialized.
   */
  @Override
  protected void initChannel(SocketChannel ch) {
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
      throw new RuntimeException(e);
    } catch (FileNotFoundException e) {

      throw new RuntimeException(e);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    // Add the business logic handler
    pipeline.addLast("handler", this.channel);
  }
}
