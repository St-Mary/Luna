package com.stmarygate.luna;

import com.stmarygate.coral.network.BaseChannel;
import com.stmarygate.coral.network.BaseInitializer;
import com.stmarygate.coral.network.codec.PacketDecoder;
import com.stmarygate.coral.network.codec.PacketEncoder;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import java.io.File;
import java.security.*;
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
      SslContext sslContext =
          SslContextBuilder.forServer(new File("./ssl/csr.pem"), new File("./ssl/privkey.pem"))
              .build();

      SSLEngine engine = sslContext.newEngine(ch.alloc());
      engine.setUseClientMode(false);
      engine.setNeedClientAuth(false);
      // Add engine to pipeline
      pipeline.addFirst("ssl", new SslHandler(engine));
      // Add packet decoding and encoding handlers
      pipeline.addLast("decoder", new PacketDecoder());
      pipeline.addLast("encoder", new PacketEncoder());
    } catch (SSLException e) {
      throw new RuntimeException(e);
    }

    // Add the business logic handler
    pipeline.addLast("handler", this.channel);
  }
}
