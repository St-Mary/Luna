package com.stmarygate.luna;

import com.stmarygate.coral.network.BaseChannel;
import com.stmarygate.coral.network.PacketHandler;
import com.stmarygate.coral.utils.Utils;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LunaChannel extends BaseChannel {
  private final Logger LOGGER = LoggerFactory.getLogger(LunaChannel.class);

  public LunaChannel(Class<? extends PacketHandler> clazz) {
    super(clazz);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    Utils.logChannel(LOGGER, Utils.getRemote(ctx), "Channel active");
    super.channelActive(ctx);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    if (ctx != null) {
      Utils.logChannel(LOGGER, Utils.getRemote(ctx), "Channel inactive");
      super.session.close();
      super.channelInactive(ctx);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    Utils.logChannel(LOGGER, Utils.getRemote(ctx), "Exception caught: " + cause.getMessage());
    super.exceptionCaught(ctx, cause);
  }

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    Utils.logChannel(LOGGER, Utils.getRemote(ctx), "Channel registered");
    super.channelRegistered(ctx);
  }

  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    Utils.logChannel(LOGGER, Utils.getRemote(ctx), "Channel unregistered");
    super.channelUnregistered(ctx);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    Utils.logChannel(LOGGER, Utils.getRemote(ctx), "Received message: " + msg.toString());
    super.channelRead(ctx, msg);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    super.channelReadComplete(ctx);
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    Utils.logChannel(LOGGER, Utils.getRemote(ctx), "User event triggered");
    super.userEventTriggered(ctx, evt);
  }

  @Override
  public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
    Utils.logChannel(LOGGER, Utils.getRemote(ctx), "Channel writability changed");
    super.channelWritabilityChanged(ctx);
  }

  @Override
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    Utils.logChannel(LOGGER, Utils.getRemote(ctx), "Handler added");
    super.handlerAdded(ctx);
  }

  @Override
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    Utils.logChannel(LOGGER, Utils.getRemote(ctx), "Handler removed");
    super.handlerRemoved(ctx);
  }
}
