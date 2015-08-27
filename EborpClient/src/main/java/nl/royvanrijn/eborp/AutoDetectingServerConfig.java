package nl.royvanrijn.eborp;

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
import java.util.Properties;

/**
 *
 */
public class AutoDetectingServerConfig implements ServerConfig {

    private static final String MULTICAST_GROUP = "225.1.1.10";
    private static final int ANNOUNCE_PORT = 7770;
    private static final int DATA_PORT = 7771;

    private final Properties properties;

    private InetAddress server;

    public AutoDetectingServerConfig(Properties properties) {
        this.properties = properties;

        System.out.println("Start autodetecting server address");

        try (final DatagramChannel receiveChannel = createReceiveChannel()) {
            System.out.println("Waiting for server announcement...");

            boolean received = false;
            ByteBuffer buffer = ByteBuffer.allocate(32);

            while (!received) {
                buffer.clear();
                final SocketAddress sender = receiveChannel.receive(buffer);
                buffer.flip();

                String reply = new String(buffer.array(), Charset.forName("UTF-8"));

                if (reply.startsWith("announcement")) {
                    server = ((InetSocketAddress) sender).getAddress();
                    System.out.println("Received server address: " + server);
                    received = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finished autodetecting server address");
    }

    private DatagramChannel createReceiveChannel() throws IOException {
        final String nicName = properties.getProperty(PropertiesReader.NETWORK_INTERFACE);
        final NetworkInterface networkInterface = getNetworkInterface(nicName);

        final DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(new InetSocketAddress(ANNOUNCE_PORT))
                .setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);

        final InetAddress group = InetAddress.getByName(MULTICAST_GROUP);
        channel.join(group, networkInterface);

        return channel;
    }

    private NetworkInterface getNetworkInterface(String nicName) {
        try {
            NetworkInterface networkInterface;
            if (nicName == null) {
                final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                if (interfaces.hasMoreElements()) {
                    networkInterface = interfaces.nextElement();
                } else {
                    throw new IllegalStateException("No usable network interface found");
                }
            } else {
                networkInterface = NetworkInterface.getByName(nicName);
            }
            return networkInterface;
        } catch (SocketException e) {
            throw new IllegalStateException("Unable to find network interface", e);
        }
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
