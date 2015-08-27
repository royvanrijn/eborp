package nl.royvanrijn.eborp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.Charset;

/**
 *
 */
public class AutoDetectingServerConfig implements ServerConfig {

    private static final String MULTICAST_GROUP = "225.1.1.10";
    private static final int ANNOUNCE_PORT = 7770;
    private static final int DATA_PORT = 7771;

    private InetAddress server;

    public AutoDetectingServerConfig() {
        System.out.println("Start autodetecting server address");

        try (MulticastSocket socket = new MulticastSocket(ANNOUNCE_PORT)) {
            InetAddress group = InetAddress.getByName(MULTICAST_GROUP);
            socket.joinGroup(group);

            System.out.println("Sending announcement");
            byte[] message = "announce-client".getBytes(Charset.forName("UTF-8"));
            socket.send(new DatagramPacket(message, message.length, group, ANNOUNCE_PORT));

            boolean received = false;

            do {
                System.out.println("Waiting for server reply...");
                final byte[] buf = new byte[256];
                final DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String reply = new String(packet.getData(), Charset.forName("UTF-8"));
                if (reply.startsWith("announcement-reply")) {
                    server = packet.getAddress();
                    System.out.println("Received server address: " + server.getHostAddress());
                    received = true;
                }
            } while (!received);

            socket.leaveGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Stop autodetecting server address");
    }

    @Override
    public InetAddress getInetAddress() {
        return server;
    }

    @Override
    public int getPort() {
        return DATA_PORT;
    }
}
