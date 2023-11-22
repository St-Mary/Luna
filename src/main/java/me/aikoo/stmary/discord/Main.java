package me.aikoo.stmary.discord;

import me.aikoo.stmary.discord.core.bot.StMaryClient;
import me.aikoo.stmary.gameserver.GameClient;
import me.aikoo.stmary.gameserver.GameServer;

import java.io.IOException;

/** The main class of the application. */
public class Main {

  /**
   * Start StMary's Gate
   *
   * @param args The arguments passed to the application.
   */
  public static void main(String[] args) {
    new StMaryClient();

    GameServer gameServer = new GameServer();

    try {
        // Create two threads, one for sending and one for receiving
        Thread serverThread = new Thread(() -> {
          try {
            gameServer.run();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });

        serverThread.start();

      GameClient gameClient = new GameClient();
      String msg = gameClient.sendPacket("Hello, World!");
      System.out.println("Server response: " + msg);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
