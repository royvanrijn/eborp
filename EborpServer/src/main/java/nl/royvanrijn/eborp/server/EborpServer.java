package nl.royvanrijn.eborp.server;

import nl.royvanrijn.eborp.DetectionEndpoint;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

public class EborpServer {

	public static void main(String[] args) throws Exception {
        final Options options = new Options();
        options.addOption("n", true, "The name of the network interface to listen on");

        final CommandLineParser parser = new DefaultParser();
        final CommandLine commandLine = parser.parse(options, args);

        final EborpServer server = new EborpServer();
		server.start(commandLine);
	}

	private void start(CommandLine commandLine) throws Exception {
		startAnnouncementServer(commandLine);
		startDataServer();
	}

	private void startAnnouncementServer(CommandLine commandLine) {
        String networkInterface = commandLine.getOptionValue("n");
        AnnouncementServer server = new AnnouncementServer(networkInterface);
        server.start();
	}

	private void startDataServer() {
		UdpDataServer server = new UdpDataServer(new DetectionEndpoint());
		server.start();
	}

}
