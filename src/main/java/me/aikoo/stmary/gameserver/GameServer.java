package me.aikoo.stmary.gameserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

public class GameServer {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[GameServerConstants.GAME_SERVER_PACKET_SIZE];


    public GameServer() {
        try {
            socket = new DatagramSocket(4445);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            System.out.println("[" + timestamp + "] Game Server started on port :" + socket.getPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() throws IOException {
        running = true;

        while (running) {
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            String received
                    = new String(packet.getData(), 0, buf.length, StandardCharsets.UTF_8).replaceAll("\0", "");

            logReceivedPacket(packet, received);

            if (received.equals(GameServerConstants.GAME_SERVER_END_PACKET)) {
                System.out.println("Stopping server...");
                running = false;
                continue;
            }
            socket.send(packet);
        }
        socket.close();
    }

    private void logReceivedPacket(DatagramPacket packet, String received) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("[" + timestamp + " ,IP: " + packet.getAddress() + " ,Port: " + packet.getPort() +"]  Received packet: " + received);
    }
}
