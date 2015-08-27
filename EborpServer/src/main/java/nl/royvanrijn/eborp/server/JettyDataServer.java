package nl.royvanrijn.eborp.server;

import nl.royvanrijn.eborp.DetectionEndpoint;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 *
 */
public class JettyDataServer {

    public void start() throws Exception {
        System.out.println("Starting Jetty data server");

        /**
         * TODO:
         *
         * This is a very simple receiver service, how can we connect the Raspberry Pi's without needing to set IP addresses?
         * Does the server have a fixed IP? Will they communicate using LAN? WLAN? (LAN is easiest for now)
         * Discovery? This needs to be plug and play.
         */
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        Server jettyServer = new Server(7777);
        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");

        jerseyServlet.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", DetectionEndpoint.class.getCanonicalName());

        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
    }
}
