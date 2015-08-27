package nl.royvanrijn.eborp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

/**
 * The simple client, which sends a message to the echo server and waits for
 * response
 */
public class EborpClient {

	private Properties properties = new PropertiesReader().getProperties();

	public static void main(String[] args) throws Exception {
		System.out.println("Starting Eborp Client.");
		EborpClient client = new EborpClient();
		client.start();
	}

	private void start() throws Exception {

		final BufferedReader inReader = new BufferedReader(
				new InputStreamReader(System.in));
		while (true) {
            try {
                String input = inReader.readLine();
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

		String json =
				"{\"epoch\":" + epochInMillis
				+ ",\"mac\":\""+ MAC +"\""
				+ ",\"dbm\":"+ dBm
				+ ",\"source\":\""+ properties.get(PropertiesReader.SOURCE)+"\""
				+ "}";
		return json;
	}

	private void post(String data) {
		try {
			URL restServiceURL = new URL(""+ properties.get(PropertiesReader.SERVER_ADDRESS));

			HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();

			httpConnection.setConnectTimeout(2); //Don' wait too long...

			httpConnection.setDoOutput(true);
			httpConnection.setRequestMethod("POST");
		    httpConnection.setRequestProperty("Content-Type", "application/json");

			OutputStream outputStream = httpConnection.getOutputStream();
			outputStream.write(data.getBytes());
			outputStream.flush();

			int response = httpConnection.getResponseCode();
			if(response < 200 || response >= 300) {
				//Not good... what do we do now?
			}
			httpConnection.disconnect();

		} catch (Exception e) {
			//Silently ignore more...
		}
	}
}
