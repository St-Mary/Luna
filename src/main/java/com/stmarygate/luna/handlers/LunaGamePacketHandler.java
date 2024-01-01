package com.stmarygate.luna.handlers;

import com.stmarygate.coral.network.BaseChannel;
import com.stmarygate.coral.network.packets.PacketHandler;
import com.stmarygate.coral.network.packets.client.PacketGameTest;

/** A {@link PacketHandler} which handles all packets for the game. */
public class LunaGamePacketHandler extends PacketHandler {
  /**
   * Create a new packet handler.
   *
   * @param channel The channel from which the packet handler was created.
   */
  public LunaGamePacketHandler(BaseChannel channel) {
    super(channel);
  }

  public void handlePacketGameTest(PacketGameTest packet) {
    System.out.println("Game test result: " + packet.getResult());
  }
}
