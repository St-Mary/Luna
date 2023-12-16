package com.stmarygate.gameserver.handlers;

import com.stmarygate.common.network.BaseChannel;
import com.stmarygate.common.network.ClientSession;
import com.stmarygate.common.network.PacketHandler;
import com.stmarygate.common.network.packets.Packet;
import com.stmarygate.common.network.packets.PacketBuffer;
import com.stmarygate.common.network.packets.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteOrder;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LunaLoginChannel extends BaseChannel {

    @Getter
    @Setter
    private final PacketHandler handler;
    private final Logger LOGGER = LoggerFactory.getLogger(LunaLoginChannel.class);
    @Getter
    protected ClientSession session;

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
        LOGGER.info("Channel active is called");
        this.session = new ClientSession(ctx.channel());
    }

    public static class PacketEncoderLuna extends MessageToByteEncoder<Packet> {
        public PacketEncoderLuna() {
            super(Packet.class);
        }

        @Override
        protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) {
            try {
                msg.encode(new PacketBuffer(out, Protocol.getInstance().getPacketId(msg), Packet.PacketAction.WRITE));
                ctx.channel().flush();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public static class PacketDecoderLuna extends MessageToMessageDecoder<ByteBuf> {
        private static final Logger LOGGER = LoggerFactory.getLogger(PacketDecoderLuna.class);
        private ByteBuf buffer;

        public PacketDecoderLuna() {
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
            if(this.buffer == null || this.buffer.readableBytes() == 0) {
                this.buffer = msg.duplicate().retain();
            } else {
                this.buffer = Unpooled.copiedBuffer(this.buffer, msg).retain();
            }

            while(this.buffer.readableBytes() > Packet.HEADER_SIZE) {
                System.out.print("Buffer at start: ");
                printBuffer(true, this.buffer);
                int id = this.buffer.readShort();
                int size = this.buffer.readShort();
                if(size > this.buffer.readableBytes()) {
                    // Not enough data to read the packet, wait for more data
                    return;
                }

                ByteBuf slice = this.buffer.readSlice(size);

                Packet packet = Protocol.getInstance().getPacket(id);
                PacketBuffer packetBuffer = new PacketBuffer(slice, id, Packet.PacketAction.READ);
                packet.decode(packetBuffer);
                out.add(packet);
            }
        }
    }

    public static void printBuffer(boolean hex, ByteBuf data) {
        byte[] buf = new byte[data.writerIndex()];
        data.getBytes(0, buf);

        System.out.print("[");
        for (int i=0; i<buf.length; i++)
            System.out.print((hex ? "0x" + Integer.toHexString(buf[i] & 0xFF).toUpperCase() : String.valueOf(buf[i])) + (i == buf.length-1 ? "" : ", "));
        System.out.println("]");
    }
}
