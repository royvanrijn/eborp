package nl.royvanrijn.eborp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;

/**
 *
 */
public class UdpDataSink implements DataSink {

    private final DatagramChannel channel;

    public UdpDataSink(ServerConfig serverConfig) {
        System.out.println("Opening UDP channel for sending");
        try {
            channel = DatagramChannel.open(StandardProtocolFamily.INET);
            channel.connect(new InetSocketAddress(serverConfig.getInetAddress(), serverConfig.getPort()));
        } catch (IOException e) {
            throw new IllegalStateException("Unable to start UDP channel for sending", e);
        }
    }

    @Override
    public void put(String data) {
        ByteBuffer buffer = ByteBuffer.wrap(data.getBytes(Charset.forName("UTF-8")));

        try {
            channel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
