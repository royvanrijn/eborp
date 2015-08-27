package nl.royvanrijn.eborp.server;

import java.net.InetAddress;

/**
 *
 */
public interface ServerConfig {
    InetAddress getInetAddress();

    int getPort();
}
