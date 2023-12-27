package com.stmarygate.luna;

import com.stmarygate.coral.network.BaseChannel;
import com.stmarygate.coral.network.BaseInitializer;
import com.stmarygate.coral.network.codec.PacketDecoder;
import com.stmarygate.coral.network.codec.PacketEncoder;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import java.io.File;
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

    // Add packet decoding and encoding handlers
    pipeline.addLast("decoder", new PacketDecoder());
    pipeline.addLast("encoder", new PacketEncoder());

    try {
      SslContext sslContext =
          SslContextBuilder.forServer(new File("./ssl/certificate.pem"), new File("./ssl/key.pem"))
              .build();
      pipeline.addLast("ssl", sslContext.newHandler(ch.alloc()));
    } catch (SSLException e) {
      throw new RuntimeException(e);
    }

    // Add the business logic handler
    pipeline.addLast("handler", this.channel);
  }
}
