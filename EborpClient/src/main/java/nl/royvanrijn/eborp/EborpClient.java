package nl.royvanrijn.eborp;

import nl.royvanrijn.eborp.server.AutoDetectingServerConfig;
import nl.royvanrijn.eborp.server.ServerConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Properties;

/**
 * The simple client, which sends a message to the echo server and waits for
 * response
 */
public class EborpClient {

    private final Properties properties;
	private final ServerConfig serverConfig;
	private final DatagramSocket socket;

	public static void main(String[] args) throws Exception {
		System.out.println("Starting Eborp Client.");
		EborpClient client = new EborpClient();
		client.start();
	}

	public EborpClient() {
		serverConfig = new AutoDetectingServerConfig();
        properties = new PropertiesReader().getProperties();

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new IllegalStateException("Unable to start UDP socket for sending ", e);
        }
    }

	private void start() {

		final BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			String input;
			try {
				input = inReader.readLine();
			} catch (IOException e) {
				throw new IllegalStateException("Unable to read input", e);
			}

			try {
                String json = parseJson(input);

                if(json != null) {
                    post(json);
                }
            } catch(Exception e) {
            	e.printStackTrace();
                //Silently ignore... we do this to ensure continuity.
            }
		}
	}

	private String parseJson(String input) {
		String[] values = input.replace(" ", "").split(",");
		if(values.length < 2) {
		    return null;
		}
		String time = values[0];
		String MAC = values[1];
		Integer dBm = null;
		if(values.length >= 3) {
		    dBm = Integer.parseInt(values[2]);
		}

		long epochInMillis = (long)(Double.parseDouble(time) * 1000L);

        return "{\"epoch\":" + epochInMillis
        + ",\"mac\":\""+ MAC +"\""
        + ",\"dbm\":"+ dBm
        + ",\"source\":\""+ properties.get(PropertiesReader.SOURCE)+"\""
        + "}";
	}

	private void post(String data) {
		try {

			final byte[] bytes = data.getBytes();
			socket.send(new DatagramPacket(bytes, bytes.length, serverConfig.getInetAddress(), serverConfig.getPort()));

		} catch (Exception e) {
            e.printStackTrace();
		}
	}
}
