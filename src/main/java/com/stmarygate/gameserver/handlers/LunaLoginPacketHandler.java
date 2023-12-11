package com.stmarygate.gameserver.handlers;

import com.stmarygate.common.network.BaseChannel;
import com.stmarygate.common.network.PacketHandler;
import com.stmarygate.common.network.packets.client.PacketVersion;
import com.stmarygate.common.network.packets.server.PacketVersionResult;
import com.stmarygate.gameserver.Constant;

public class LunaLoginPacketHandler extends PacketHandler {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LunaLoginPacketHandler.class);

    public LunaLoginPacketHandler(BaseChannel channel) {
        super(channel);
    }

    public void handlePacketVersion(PacketVersion packet) {
        LOGGER.info("Received version result packet: {build={}.{}.{} ({})}", packet.getMajor(), packet.getMinor(), packet.getPatch(), packet.getBuildVersion());
        boolean accepted = packet.getMajor() == Constant.VERSION_MAJOR && packet.getMinor() == Constant.VERSION_MINOR && packet.getPatch() == Constant.VERSION_PATCH && packet.getBuildVersion().equals(Constant.VERSION_BUILD);
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
