package com.stmarygate.gameserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class GameClient {
  private DatagramSocket socket;
  private InetAddress address;

  private byte[] buf;

  public GameClient() {
    try {
      socket = new DatagramSocket();
      address = InetAddress.getByName("localhost");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String sendPacket(String msg) throws IOException {
    buf = msg.getBytes();
    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
    socket.send(packet);
    packet = new DatagramPacket(buf, buf.length);
    socket.receive(packet);
    return new String(packet.getData(), 0, packet.getLength());
  }

  public void close() {
    socket.close();
  }
}
