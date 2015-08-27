package nl.royvanrijn.eborp;

import java.io.IOException;

public interface DetectionListener {

    void onDetection(String data) throws IOException;
}
