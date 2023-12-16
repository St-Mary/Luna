package com.stmarygate.gameserver.handlers;

import com.stmarygate.common.network.BaseChannel;
import com.stmarygate.common.network.PacketHandler;
import com.stmarygate.common.network.packets.Packet;
import com.stmarygate.common.network.packets.PacketBuffer;
import com.stmarygate.common.network.packets.Protocol;
import com.stmarygate.common.network.packets.client.PacketVersion;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.PendingWriteQueue;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LunaClientChannelHandler extends BaseChannel {

    static Logger LOGGER = LoggerFactory.getLogger(LunaClientChannelHandler.class);

    public LunaClientChannelHandler(Class<? extends PacketHandler> clazz) {
        super(clazz);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        super.channelActive(ctx);
    }

  static class PacketDecoder2 extends MessageToMessageDecoder<ByteBuf> {
    private static final int HEADER_SIZE = 4;
    private ByteBuf buffer;

    public PacketDecoder2() {
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out)
        throws Exception {
      if (this.buffer == null || this.buffer.readableBytes() == 0) {
        this.buffer = msg.duplicate().retain();
      } else {
        this.buffer = Unpooled.copiedBuffer(this.buffer, msg).retain();
      }

      while (this.buffer.readableBytes() > HEADER_SIZE) {
        int id = this.buffer.readShort();
        int size = this.buffer.readShort();

        if (size > this.buffer.readableBytes()) {
          // Not enough data to read the packet, wait for more data
          return;
        }

        ByteBuf slice = this.buffer.readSlice(size);
        Packet packet = Protocol.getInstance().getPacket(id);

        packet.decode(new PacketBuffer(slice, id, Packet.PacketAction.READ));
        out.add(packet);
      }
     }
    }

    public static class PacketEncoder2 extends MessageToByteEncoder<Packet> {
        public PacketEncoder2() {
            super(Packet.class);
        }

        @Override
        protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
            try {
                msg.encode(new PacketBuffer(out, Protocol.getInstance().getPacketId(msg), Packet.PacketAction.WRITE));
                ctx.channel().flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
