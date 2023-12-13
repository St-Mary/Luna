package com.stmarygate.gameserver.handlers;

import com.stmarygate.common.network.BaseChannel;
import com.stmarygate.common.network.PacketHandler;
import com.stmarygate.common.network.packets.Packet;
import com.stmarygate.common.network.packets.client.PacketVersion;
import com.stmarygate.common.network.packets.server.PacketVersionResult;
import com.stmarygate.gameserver.Constant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LunaLoginPacketHandler extends PacketHandler {

  private static final org.slf4j.Logger LOGGER =
      org.slf4j.LoggerFactory.getLogger(LunaLoginPacketHandler.class);

  public LunaLoginPacketHandler(BaseChannel channel) {
    super(channel);
  }

  @Override
  public void handlePacket(Packet packet) {
    try {
      System.out.println("fbeiujehfkejffhejehgdbs");
      Method method = this.getClass().getMethod("handle" + packet.getClass().getSimpleName(), packet.getClass());
      method.invoke(this, packet);
    } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
      throw new RuntimeException("Packet " + packet.getClass().getSimpleName() + " is not handled.");
    }
  }

  public void handlePacketVersion(PacketVersion packet) {
    LOGGER.info(
        "Received version result packet: {build={}.{}.{} ({})}",
        packet.getMajor(),
        packet.getMinor(),
        packet.getPatch(),
        packet.getBuildVersion());
    boolean accepted =
        packet.getMajor() == Constant.VERSION_MAJOR
            && packet.getMinor() == Constant.VERSION_MINOR
            && packet.getPatch() == Constant.VERSION_PATCH
            && packet.getBuildVersion().equals(Constant.VERSION_BUILD);
    this.getChannel()
        .getSession()
        .write(
            new PacketVersionResult(
                accepted,
                Constant.VERSION_MAJOR,
                Constant.VERSION_MINOR,
                Constant.VERSION_PATCH,
                Constant.VERSION_BUILD));
    LOGGER.info("Version result: {}", accepted ? "accepted" : "rejected");
  }
}
