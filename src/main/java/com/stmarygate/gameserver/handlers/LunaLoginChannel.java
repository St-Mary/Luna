package com.stmarygate.gameserver.handlers;

import com.stmarygate.common.network.BaseChannel;
import com.stmarygate.common.network.ClientSession;
import com.stmarygate.common.network.PacketHandler;
import com.stmarygate.common.network.packets.Packet;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public class LunaLoginChannel extends BaseChannel {

    @Getter
    @Setter
    private final PacketHandler handler;

    @Getter
    protected ClientSession session;

    private final Logger LOGGER = LoggerFactory.getLogger(LunaLoginChannel.class);

    public LunaLoginChannel(Class<? extends PacketHandler> clazz) {
        super(clazz);
        try {
            this.handler = clazz.getDeclaredConstructor(BaseChannel.class).newInstance(this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("PacketHandler " + clazz.getSimpleName() + " does not contain the required constructor.");
        }
    }

    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) {
        LOGGER.info("Channel read is called");
        this.handler.handlePacket(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.session = new ClientSession(ctx.channel());
    }
}
