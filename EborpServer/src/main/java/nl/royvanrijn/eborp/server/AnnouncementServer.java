package nl.royvanrijn.eborp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;

/**
 *
 */
public class AnnouncementServer extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(AnnouncementServer.class);

    private static final String MULTICAST_GROUP = "225.1.1.10";
    private static final int PORT = 7770;

    public AnnouncementServer() {
        super("AnnouncementServer");
    }

    public void run() {
        LOG.info("Opening UDP channel for broadcasting server address");
        final DatagramChannel channel;
        try {
            channel = DatagramChannel.open(StandardProtocolFamily.INET);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to open UDP channel for broadcasting server address");
        }

        while (true) {
            try {
                ByteBuffer buffer = ByteBuffer.wrap("announcement".getBytes(Charset.forName("UTF-8")));
                channel.send(buffer, new InetSocketAddress(MULTICAST_GROUP, PORT));
                sleep(10000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
