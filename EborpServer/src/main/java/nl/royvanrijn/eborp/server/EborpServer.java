package nl.royvanrijn.eborp.server;

import nl.royvanrijn.eborp.DetectionEndpoint;

public class EborpServer {

	public static void main(String[] args) throws Exception {
		final EborpServer server = new EborpServer();
		server.start();
	}

	private void start() throws Exception {
		startAnnouncementServer();
		startDataServer();
//		startJetty();
	}

	private void startAnnouncementServer() {
		AnnouncementServer server = new AnnouncementServer();
		server.start();
	}

	private void startDataServer() {
		UdpDataServer server = new UdpDataServer(new DetectionEndpoint());
		server.start();
	}

	private void startJetty() throws Exception {
		JettyDataServer server = new JettyDataServer();
		server.start();
	}
}
