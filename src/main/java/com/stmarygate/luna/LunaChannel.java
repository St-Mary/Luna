package com.stmarygate.luna;

import com.stmarygate.coral.network.BaseChannel;
import com.stmarygate.coral.network.packets.PacketHandler;
import com.stmarygate.coral.utils.Utils;
import io.netty.channel.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ChannelHandler} which handles all events for a {@link Channel} and delegates them to the
 * specified {@link PacketHandler}.
 */
public class LunaChannel extends BaseChannel {
  private final Logger LOGGER = LoggerFactory.getLogger(LunaChannel.class);

  /**
   * Create a new packet handler.
   *
   * @param clazz The channel from which the packet handler was created.
   */
  public LunaChannel(Class<? extends PacketHandler> clazz) {
    super(clazz);
  }

  /**
   * This method is called whenever a {@link Channel} is active and able to perform I/O operations.
   *
   * @param ctx The context which this {@link ChannelHandler} belongs to
   */
  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    Utils.logChannel(LOGGER, Utils.getRemote(ctx), "Channel active");
    super.channelActive(ctx);
  }

  /**
   * This method is called whenever a {@link Channel} becomes inactive and so is no longer able to
   * perform I/O operations.
   *
   * @param ctx The context which this {@link ChannelHandler} belongs to
   * @throws Exception If something goes wrong
   */
  @Override
  public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
    Utils.logChannel(LOGGER, Utils.getRemote(ctx), "Channel inactive");
    super.session.close();
    super.channelInactive(ctx);
  }

  /**
   * This method is called whenever an exception is thrown in the {@link ChannelPipeline}.
   *
   * @param ctx The context which this {@link ChannelHandler} belongs to
   * @param cause The cause of the exception
   * @throws Exception If something goes wrong
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    Utils.logChannel(LOGGER, Utils.getRemote(ctx), "Exception caught: " + cause.getMessage());
    cause.printStackTrace();
  }

  /**
   * This method is called whenever a {@link Channel} is registered to its {@link EventLoop} and so
   * the {@link Channel} is ready to handle I/O.
   *
   * @param ctx The context which this {@link ChannelHandler} belongs to
   * @throws Exception If something goes wrong
   */
  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    Utils.logChannel(LOGGER, Utils.getRemote(ctx), "Channel registered");
    super.channelRegistered(ctx);
  }

  /**
   * This method is called whenever a {@link Channel} is unregistered from its {@link EventLoop} and
   * so the {@link Channel} is no longer active and registered.
   *
   * @param ctx The context which this {@link ChannelHandler} belongs to
   * @throws Exception If something goes wrong
   */
  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    Utils.logChannel(LOGGER, Utils.getRemote(ctx), "Channel unregistered");
    super.channelUnregistered(ctx);
  }

  /**
   * This method is called whenever a {@link Channel} receives a message from the {@link Channel}.
   *
   * @param ctx The context which this {@link ChannelHandler} belongs to
   * @param msg The message to handle
   * @throws Exception If something goes wrong
   */
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    Utils.logChannel(LOGGER, Utils.getRemote(ctx), "Received packet: " + msg.toString());
    super.channelRead(ctx, msg);
  }

  /**
   * This method is called whenever a {@link Channel} has read a message from the {@link Channel}.
   *
   * @param ctx The context which this {@link ChannelHandler} belongs to
   * @throws Exception If something goes wrong
   */
  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    super.channelReadComplete(ctx);
  }

  /**
   * This method is called whenever a user event was triggered.
   *
   * @param ctx The context which this {@link ChannelHandler} belongs to
   * @param evt The event which has been fired
   * @throws Exception If something goes wrong
   */
  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    Utils.logChannel(LOGGER, Utils.getRemote(ctx), "User event triggered: " + evt.toString());
    super.userEventTriggered(ctx, evt);
  }

  /**
   * This method is called whenever the writability state of a {@link Channel} changes. You can
   * check if a {@link Channel} is writable
   *
   * @param ctx The context which this {@link ChannelHandler} belongs to
   * @throws Exception If something goes wrong
   */
  @Override
  public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
    Utils.logChannel(LOGGER, Utils.getRemote(ctx), "Channel writability changed");
    super.channelWritabilityChanged(ctx);
  }

  /**
   * This method is called once the {@link ChannelHandler} was added to the actual context and it's
   * ready to handle
   *
   * @param ctx The context which this {@link ChannelHandler} belongs to
   * @throws Exception If something goes wrong
   */
  @Override
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    Utils.logChannel(LOGGER, Utils.getRemote(ctx), "Handler added");
    super.handlerAdded(ctx);
  }

  /**
   * This method is called once the {@link ChannelHandler} was removed from the actual context and
   *
   * @param ctx The context which this {@link ChannelHandler} belongs to
   * @throws Exception If something goes wrong
   */
  @Override
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    Utils.logChannel(LOGGER, Utils.getRemote(ctx), "Handler removed");
    super.handlerRemoved(ctx);
  }
}
