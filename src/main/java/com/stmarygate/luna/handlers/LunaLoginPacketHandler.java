package com.stmarygate.luna.handlers;

import com.stmarygate.coral.network.BaseChannel;
import com.stmarygate.coral.network.PacketHandler;
import com.stmarygate.coral.network.packets.client.PacketLoginUsingCredentials;
import com.stmarygate.coral.network.packets.client.PacketVersion;
import com.stmarygate.coral.network.packets.server.PacketVersionResult;
import com.stmarygate.luna.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LunaLoginPacketHandler extends PacketHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(LunaLoginPacketHandler.class);

  /**
   * Create a new packet handler.
   *
   * @param channel The channel from which the packet handler was created.
   */
  public LunaLoginPacketHandler(BaseChannel channel) {
    super(channel);
  }

  /**
   * Handle the version packet.
   *
   * @param packet The version packet.
   */
  public void handlePacketVersion(PacketVersion packet) throws Exception {
    // Check if the version is accepted.
    boolean accepted =
        packet.getMajor() == Constants.VERSION_MAJOR
            && packet.getMinor() == Constants.VERSION_MINOR
            && packet.getPatch() == Constants.VERSION_PATCH
            && packet.getBuildVersion().equals(Constants.VERSION_BUILD);

    // Send the version result packet.
    this.getChannel()
        .getSession()
        .write(
            new PacketVersionResult(
                accepted,
                Constants.VERSION_MAJOR,
                Constants.VERSION_MINOR,
                Constants.VERSION_PATCH,
                Constants.VERSION_BUILD));
  }

  public void handlePacketLoginUsingCredentials(PacketLoginUsingCredentials packet) {
    LOGGER.info(
        "LoginPacket received. Username: {}, Password: {}",
        packet.getUsername(),
        packet.getPassword());
  }
}
