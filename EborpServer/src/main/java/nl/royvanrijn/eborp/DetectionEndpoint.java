package nl.royvanrijn.eborp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("/detection")
public class DetectionEndpoint {
    private static final Logger log = LoggerFactory.getLogger(DetectionEndpoint.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void onDetection(String data) {
        log.info(data);

        /**
         * TODO:
         *
         *  - Parse the JSON into something useful
         *  - Temporary store and matching of the received detections
         *  - Store the results for future analysis
         *
         * Data:
         *
         * Step 1: A single detection:
         *            {"epoch":1430962085987,"MAC":"2c:54:cf:ea:69:0d","dBm":-89,"ID":e9f5bff0-bf12-45b3-b532-0d4fccced24a}
         *            {"epoch":1430962085987,"MAC":"2c:54:cf:ea:69:0d","dBm":-89,"ID":e9f5bff0-bf12-45b3-b532-0d4fccced24a}
         *            {"epoch":1430962085987,"MAC":"2c:54:cf:ea:69:0d","dBm":-89,"ID":e9f5bff0-bf12-45b3-b532-0d4fccced24a}
         *            {"epoch":1430962085987,"MAC":"2c:54:cf:ea:69:0d","dBm":-89,"ID":e9f5bff0-bf12-45b3-b532-0d4fccced24a}
         * 		Meaning:
         * 	        epoch:  Time of measurement
         * 	        MAC:    The found MAC address
         * 	        dBm:    The RSSI/signal strength
         * 	        ID:     Unique id of the Raspberry Pi
         * Step 2: Group by MAC address:
         * 		"2c:54:cf:ea:69:0d":
         * 			e9f5bff0-bf12-45b3-b532-0d4fccced24a -89
         * 			akjfewjkhfekjhwkjewhfkjeretettetette -79
         * 			e9f5bff0-bf12-45b3-b532-0d4fccced24a -86
         * Step 3: Then group per RPi
         * 		"2c:54:cf:ea:69:0d":
         * 			e9f5bff0-bf12-45b3-b532-0d4fccced24a:
         * 				-89
         * 				-86
         * 			akjfewjkhfekjhwkjewhfkjeretettetette:
         * 				-79
         * Step 4: Calculate median/avg signal per RPi (see Matcher.java):
         * 		"2c:54:cf:ea:69:0d":
         * 			e9f5bff0-bf12-45b3-b532-0d4fccced24a: -87
         * 			akjfewjkhfekjhwkjewhfkjeretettetette: -79
         * Step 5: Create fingerprint per MAC (see Matcher.java):
         * 		"2c:54:cf:ea:69:0d":
         * 			[-87, -79]
         * Step 6: Match against existing prints
         *
         * How can we evict? Timed collection? DIY? Remove from tail add to head? List? Google Guava?
         * Do we use the detection time or the received time? Do we need to send detect-time? How do we sync RPis?
         *
         * - GUI's:
         *      - Create a webpage/service for mapping a room, capture reference points
         *          1) Take your mobile phone
         *          2) Open the mapping-webpage from the server
         *          3) Register your MAC address (from the phone)
         *          4) Start a 'recording session' for a location, for example 'room A'
         *          5) Walk around in a area, using your phone, capturing reference points/strengths
         *          6) Stop recording session (while still in that room)
         *          7) All the points captured by all the RPis with that MAC address serve as reference for the location
         *
         *      - Start actual tracking
         *          1) Every N seconds/minutes we match all the measured MAC addresses against the reference points
         *          2) Update the list of MAC -> best matching reference -> location
         *          3) Display a list of 'locations' (rooms) with the MAC addresses per location
         *          4) Allow people to register a nickname per MAC
         *          5) Add an audio signal to indicate when devices are entering/leaving the room
         *              "Hello RoyPhone", "Bye RoyPhone", "Hello RoyLaptop" etc
         *
         *      - Main goal:
         *          Determine accurately how many (and which) people are in which room, when do they enter and when do they leave
         *
         *      - Improve algorithm based on experiences, possible things to add:
         *          - Add more locations, divide rooms into quadrants or grids etc
         *          - Use multidimension scaling (MDS) to automatically create a map of RPi locations relative to each other (from room average?)
         *          - Try triangulation between locations with the MDS location
         *
         *      - Secondary (epic win) goal:
         *          Show a map of the rooms with people in it moving around using MDS automatic mapping/scaling
         *
         * Distances, wat meten we:
         * 1) Afstanden van telefoon tot RPis
         *
         * Locatie 1, gemiddelde afstanden: 10, 20, 10
         * Locatie 2, gemiddelde afstanden: 15, 18, 13
         * Locatie 3, gemiddelde afstanden: 12, 22, 18
         *
         */
    }
}