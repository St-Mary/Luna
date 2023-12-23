package com.stmarygate.luna.handlers;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LunaChannelHandler implements ChannelHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(LunaChannelHandler.class);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        LOGGER.info("New connection from: " + ctx.channel().remoteAddress().toString());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        LOGGER.info("Connection closed: " + ctx.channel().remoteAddress().toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("Exception caught. Address:" + ctx.channel().remoteAddress().toString() + "\n" + cause);
    }
}
