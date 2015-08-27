package nl.royvanrijn.eborp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 *
 */
public class ProbeReader {

    private final DataSink dataSink;
    private final Properties properties;

    public ProbeReader(DataSink dataSink, Properties properties) {
        this.dataSink = dataSink;
        this.properties = properties;
    }

    public void readProbe() {
        System.out.println("Start reading probe data...");

        final BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String input;
            try {
                input = inReader.readLine();
            } catch (IOException e) {
                throw new IllegalStateException("Unable to read input", e);
            }

            String json = parseJson(input);

            if (json != null) {
                dataSink.put(json);
            }
        }
    }

    private String parseJson(String input) {
        String[] values = input.replace(" ", "").split(",");
        if (values.length < 2) {
            return null;
        }
        String time = values[0];
        String MAC = values[1];
        Integer dBm = null;
        if (values.length >= 3) {
            dBm = Integer.parseInt(values[2]);
        }

        long epochInMillis = (long) (Double.parseDouble(time) * 1000L);

        return "{\"epoch\":" + epochInMillis
                + ",\"mac\":\"" + MAC + "\""
                + ",\"dbm\":" + dBm
                + ",\"source\":\"" + properties.get(PropertiesReader.SOURCE) + "\""
                + "}";
    }
}
