package com.stmarygate.luna.handlers;

import com.stmarygate.coral.network.BaseChannel;
import com.stmarygate.coral.network.PacketHandler;

public class LunaGamePacketHandler extends PacketHandler {
  /**
   * Create a new packet handler.
   *
   * @param channel The channel from which the packet handler was created.
   */
  public LunaGamePacketHandler(BaseChannel channel) {
    super(channel);
  }
}
