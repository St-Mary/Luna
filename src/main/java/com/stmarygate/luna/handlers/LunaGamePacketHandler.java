package com.stmarygate.luna.handlers;

import com.stmarygate.coral.entities.Account;
import com.stmarygate.coral.entities.Player;
import com.stmarygate.coral.network.BaseChannel;
import com.stmarygate.coral.network.packets.PacketHandler;
import com.stmarygate.coral.network.packets.client.PacketGameTest;
import com.stmarygate.coral.network.packets.client.PacketGetPlayerInformations;
import com.stmarygate.coral.network.packets.server.PacketGetPlayerInformationsResult;
import com.stmarygate.luna.database.DatabaseManager;
import java.io.WriteAbortedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A {@link PacketHandler} which handles all packets for the game. */
public class LunaGamePacketHandler extends PacketHandler {
  Logger LOGGER = LoggerFactory.getLogger(LunaGamePacketHandler.class);

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

  public void handlePacketGetPlayerInformations(PacketGetPlayerInformations packet) {
    LOGGER.info("Getting player informations");
    if (packet.getId() == 0L) return;
    Account account = DatabaseManager.findById(packet.getId(), Account.class);
    Player player = account.getPlayer();

    try {
      this.getChannel().getSession().write(new PacketGetPlayerInformationsResult(player));
    } catch (WriteAbortedException e) {
      throw new RuntimeException(e);
    }
  }
}
