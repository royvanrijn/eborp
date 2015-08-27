package nl.royvanrijn.eborp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.Enumeration;

/**
 *
 */
public class AnnouncementServer extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(AnnouncementServer.class);

    private static final String MULTICAST_GROUP = "225.1.1.10";
    private static final int PORT = 7770;

    private final String niName;

    public AnnouncementServer(String networkInterface) {
        super("AnnouncementServer");
        this.niName = networkInterface;
    }

    public void run() {

        try {
            final NetworkInterface networkInterface = getNetworkInterface();

            LOG.info("Start client announcement server on interface {}", networkInterface.getDisplayName());

            final DatagramChannel receiveChannel = DatagramChannel.open(StandardProtocolFamily.INET)
                    .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                    .bind(new InetSocketAddress(PORT))
                    .setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);


            final InetAddress group = InetAddress.getByName(MULTICAST_GROUP);
            receiveChannel.join(group, networkInterface);

            LOG.info("Waiting for client announcements on multicast address {}...", group);

            final ByteBuffer buffer = ByteBuffer.allocate(32);

            while (true) {
                final SocketAddress sender = receiveChannel.receive(buffer);
                buffer.flip();

                String contents = new String(buffer.array());
                buffer.clear();

                if (contents.startsWith("announce-client")) {
                    LOG.info("Received announcement from client with IP address: " + sender);
                    sendReply();
                    LOG.info("Reply sent");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendReply() throws IOException {
        try {
            final DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);

            ByteBuffer buffer = ByteBuffer.wrap("announcement-reply".getBytes(Charset.forName("UTF-8")));
            channel.send(buffer, new InetSocketAddress(MULTICAST_GROUP, PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private NetworkInterface getNetworkInterface() throws SocketException {
        try {
            final NetworkInterface networkInterface;

            if (niName == null) {
                final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                if (interfaces.hasMoreElements()) {
                    networkInterface = interfaces.nextElement();
                } else {
                    throw new IllegalStateException("No usable network interface found");
                }
            } else {
                networkInterface = NetworkInterface.getByName(niName);
            }
            return networkInterface;
        } catch (SocketException e) {
            throw new IllegalStateException("Unable to find network interface", e);
        }
    }
}
