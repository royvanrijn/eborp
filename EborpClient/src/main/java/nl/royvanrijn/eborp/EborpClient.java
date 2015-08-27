package nl.royvanrijn.eborp;

import java.util.Properties;

/**
 * The simple client, which sends a message to the echo server and waits for
 * response
 */
public class EborpClient {

    private final ProbeReader probeReader;

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Eborp client");
        EborpClient client = new EborpClient();
        client.start();
    }

    public EborpClient() {
        Properties properties = new PropertiesReader().getProperties();
        ServerConfig serverConfig = new AutoDetectingServerConfig(properties);
        DataSink dataSink = new UdpDataSink(serverConfig);
        probeReader = new ProbeReader(dataSink, properties);
    }

    private void start() {
        probeReader.readProbe();
    }
}
