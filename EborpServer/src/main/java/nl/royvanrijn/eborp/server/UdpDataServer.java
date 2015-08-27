package nl.royvanrijn.eborp.server;

import nl.royvanrijn.eborp.DetectionListener;
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
public class UdpDataServer extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(UdpDataServer.class);

    private static final int DATA_PORT = 7771;

    private final DetectionListener detectionListener;

    public UdpDataServer(DetectionListener detectionListener) {
        this.detectionListener = detectionListener;
    }

    @Override
    public void run() {
        LOG.info("Starting UDP data server");

        final DatagramChannel channel;
        try {
            channel = DatagramChannel.open(StandardProtocolFamily.INET);
            channel.socket().bind(new InetSocketAddress(DATA_PORT));
        } catch (IOException e) {
            throw new IllegalStateException("Unable to start UDP socket on port " + DATA_PORT, e);
        }

        final ByteBuffer buffer = ByteBuffer.allocate(256);

        while (true) {
            try {
                channel.receive(buffer);
                buffer.flip();

                String data = new String(buffer.array(), Charset.forName("UTF-8"));
                detectionListener.onDetection(data);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                buffer.clear();
            }
        }
    }
}
