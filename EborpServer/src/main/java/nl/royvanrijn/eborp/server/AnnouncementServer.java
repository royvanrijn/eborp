package nl.royvanrijn.eborp.server;

import nl.royvanrijn.eborp.DetectionEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.Charset;

/**
 *
 */
public class AnnouncementServer extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(DetectionEndpoint.class);

    private static final String MULTICAST_GROUP = "225.1.1.10";
    private static final int PORT = 7770;

    public void run() {

        LOG.info("Start client announcement server");
        try {
            MulticastSocket socket = new MulticastSocket(PORT);
            final InetAddress group = InetAddress.getByName(MULTICAST_GROUP);
            socket.joinGroup(group);

            while (true) {
                LOG.info("Waiting for client announcements...");
                final byte[] bytes = new byte[256];
                final DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                socket.receive(packet);

                String contents = new String(packet.getData());
                if (contents.startsWith("announce-client")) {
                    final InetAddress address = packet.getAddress();
                    LOG.info("Received announcement from client with IP address: " + address.getHostAddress());

                    final byte[] reply = "announcement-reply".getBytes(Charset.forName("UTF-8"));
                    socket.send(new DatagramPacket(reply, reply.length, group, PORT));
                    LOG.info("Reply sent");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
