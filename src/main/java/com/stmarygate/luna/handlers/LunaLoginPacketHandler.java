package com.stmarygate.luna.handlers;

import com.stmarygate.coral.entities.Account;
import com.stmarygate.coral.network.BaseChannel;
import com.stmarygate.coral.network.codes.LoginResultCode;
import com.stmarygate.coral.network.packets.PacketHandler;
import com.stmarygate.coral.network.packets.client.PacketLoginUsingCredentials;
import com.stmarygate.coral.network.packets.client.PacketLoginUsingJWT;
import com.stmarygate.coral.network.packets.client.PacketVersion;
import com.stmarygate.coral.network.packets.server.PacketLoginResult;
import com.stmarygate.coral.network.packets.server.PacketVersionResult;
import com.stmarygate.coral.utils.BCryptEncryptionUtils;
import com.stmarygate.luna.constants.Constants;
import com.stmarygate.luna.database.DatabaseManager;
import org.jetbrains.annotations.NotNull;

public class LunaLoginPacketHandler extends PacketHandler {

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
  @Override
  public void handlePacketVersion(PacketVersion packet) {
    // Check if the version is accepted.
    boolean accepted =
        packet.getMajor() == Constants.VERSION_MAJOR
            && packet.getMinor() == Constants.VERSION_MINOR
            && packet.getPatch() == Constants.VERSION_PATCH
            && packet.getBuildVersion().equals(Constants.VERSION_BUILD);

    // Send the version result packet.
    try {
      this.getChannel()
          .getSession()
          .write(
              new PacketVersionResult(
                  accepted,
                  Constants.VERSION_MAJOR,
                  Constants.VERSION_MINOR,
                  Constants.VERSION_PATCH,
                  Constants.VERSION_BUILD));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Handle the login packet using credentials.
   *
   * @param packet The login packet using credentials instance.
   */
  @Override
  public void handlePacketLoginUsingCredentials(@NotNull PacketLoginUsingCredentials packet) {
    String username = packet.getUsername();
    String password = packet.getPassword();

    Account dbAccount = DatabaseManager.findByUsername(username, Account.class);

    if (dbAccount == null) {
      this.getChannel()
          .sendPacket(
              new PacketLoginResult(false, null, LoginResultCode.FAILURE_NO_ACCOUNT.getCode(), ""));
      return;
    }

    boolean matching =
        BCryptEncryptionUtils.check(Constants.PASSWORD_RANDOM, password, dbAccount.getPassword());

    int code =
        matching
            ? LoginResultCode.SUCCESS.getCode()
            : LoginResultCode.FAILURE_INCORRECT_PASSWORD.getCode();

    String token = matching ? dbAccount.getJwt() : "";
    Account account = matching ? dbAccount : null;

    this.getChannel().sendPacket(new PacketLoginResult(matching, account, code, token));

    if (matching) {
      this.getChannel().setHandler(new LunaGamePacketHandler(this.getChannel()));
    }
  }

  /**
   * Handle the login packet using JWT.
   *
   * @param packet The login packet using JWT instance.
   */
  @Override
  public void handlePacketLoginUsingJWT(@NotNull PacketLoginUsingJWT packet) {
    System.out.println("Handling login using JWT");
    String jwt = packet.getJwt();

    Account dbAccount = DatabaseManager.findByJwt(jwt, Account.class);

    if (dbAccount == null) {
      this.getChannel()
          .sendPacket(
              new PacketLoginResult(false, null, LoginResultCode.FAILURE_NO_ACCOUNT.getCode(), ""));
      return;
    }

    this.getChannel().setHandler(new LunaGamePacketHandler(this.getChannel()));
    this.getChannel()
        .sendPacket(new PacketLoginResult(true, dbAccount, LoginResultCode.SUCCESS.getCode(), jwt));
  }
}
