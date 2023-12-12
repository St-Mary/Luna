package com.stmarygate.gameserver.handlers;

import com.stmarygate.common.network.BaseChannel;
import com.stmarygate.common.network.codec.PacketDecoder;
import com.stmarygate.common.network.codec.PacketEncoder;
import com.stmarygate.common.network.packets.Packet;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BaseInitializer2 extends ChannelInitializer<SocketChannel> {
        private final BaseChannel channel;
        private final Packet.PacketType packetType;

        @Override
        protected void initChannel(SocketChannel ch) {
        ChannelPipeline pl = ch.pipeline();

    pl.addLast("decoder", new LunaClientChannelHandler.PacketDecoder2(this.packetType));
    pl.addLast("encoder", new LunaClientChannelHandler.PacketEncoder2());
        pl.addLast("handler", this.channel);
    }
}

