package com.stmarygate.gameserver.handlers;

import com.stmarygate.common.network.BaseChannel;
import com.stmarygate.common.network.PacketHandler;

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
