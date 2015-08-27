package nl.royvanrijn.eborp;

import java.io.IOException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;

/**
 *
 */
public interface DetectionListener {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    void onDetection(String data) throws IOException;
}
