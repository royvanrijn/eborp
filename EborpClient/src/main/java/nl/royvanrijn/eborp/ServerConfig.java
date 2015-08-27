package nl.royvanrijn.eborp;

import java.net.InetAddress;

/**
 *
 */
public interface ServerConfig {
    InetAddress getInetAddress();

    int getPort();
}
