package nl.royvanrijn.eborp.server;

import nl.royvanrijn.eborp.DetectionListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 *
 */
public class UdpDataServer extends Thread {

    private static final int DATA_PORT = 7771;

    private final DetectionListener detectionListener;

    public UdpDataServer(DetectionListener detectionListener) {
        this.detectionListener = detectionListener;
    }

    @Override
    public void run() {
        System.out.println("Starting UDP data server");

        DatagramSocket socket;
        try {
            socket = new DatagramSocket(DATA_PORT);
        } catch (SocketException e) {
            throw new IllegalStateException("Unable to start UDP socket on port " + DATA_PORT, e);
        }

        while (true) {
            try {
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String data = new String(packet.getData());
                detectionListener.onDetection(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
