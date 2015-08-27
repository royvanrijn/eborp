package nl.royvanrijn.eborp.matching;

import java.util.Arrays;


public class Matcher {

    public static void main(String[] args) {
        new Matcher().run();
    }

    private static final int CAPTURE_DEVICES = 4;
    private static final int MIN = -35;
    private static final int MAX = -90;

    private void run() {

        /**
         * Collect (from db?) the records of the latest N seconds
         *   Group by MAC address:
         *     Determine the best RSSI strength for each RPi per MAC (for example: MAC1 - RPi1: -37,-38,-70,-37,-38,-39,-38 => -38)
         *       (Remove outliers, use median?)
         *     Match the series of best RPi RSSIs against the known Capture Points (CP)
         *
         * Test later: How about keeping track of movement? Best matches over time?
         * Visualize the results, how?
         * Maybe add position to RPis as well, triangulate between best three matches based on calculated error?
         *
         * Live processing, keep list of last 5 minutes, FIFO, every N seconds repeat the calculation, update last known position (dont jump!)
         * Store the complete result to improve matching in the future (?)
         */

        int[] resultsForRpi1 = new int[] {-79,-38,-79,-37,-38,-39, -37,-39, -36, -40};
        // Sort the results:
        Arrays.sort(resultsForRpi1);
        // Calculate the average strength from the lowest half (skip the outliers)
        int total = 0;
        for(int i = resultsForRpi1.length - (resultsForRpi1.length / 2); i < resultsForRpi1.length; i++) {
            total += resultsForRpi1[i];
        }
        System.out.println(Arrays.toString(resultsForRpi1));
        System.out.println("Median for strengths: "+(resultsForRpi1[resultsForRpi1.length/2]));
        System.out.println("Avg of best strength: "+(total/(resultsForRpi1.length/2)));


        // Example of the matching algorithm:

        // Given some (mock) capture-points, using 4 RPis:
        CapturePoint[] capturePoints = new CapturePoint[5];
        capturePoints[0] = new CapturePoint(-40, -55, -79, null);
        capturePoints[1] = new CapturePoint(null, -45, -59, -59);
        capturePoints[2] = new CapturePoint(-53, null, -59, -37);
        capturePoints[3] = new CapturePoint(-38, -75, -70, null);
        capturePoints[4] = new CapturePoint(-53, -45, -75, -77);

        // And one 'measured' capture:
        CapturePoint measurement = new CapturePoint(-53,-55,null, -77);

        // Calculate the distance between given and captured:
        for(int i = 0; i<capturePoints.length;i++) {
            //Compare the measured RPi values against the known capturePoints:
            System.out.println("Score " + i + ": " + calculateDistance(capturePoints[i], measurement));
        }

        // The best matching capturepoint is our match, in the above case: capturePoints[4]
    }

    /**
     * Returns a score based on the difference between two measured points.
     *
     * If no measurement is found, give a small penalty (needs to be tweaked)
     * Maybe use MIN and MAX from actual measurements?
     *
     * MIN: The best possible RSSI found up to now
     * MAX: The worst possible RSSI measured
     */
    private int calculateDistance(CapturePoint r1, CapturePoint r2) {

        int error = 0;

        for(int rpi = 0; rpi < CAPTURE_DEVICES; rpi++) {
            Integer s1 = r1.strength[rpi];
            Integer s2 = r2.strength[rpi];

            if(s1 == s2) {
                //Powers are null or exactly the same, no error!
            } else if(s1 == null) {
                // s1 has no measurement but s2 does, give small penalty:
                error += Math.abs(MAX - s2) * Math.abs(MAX - s2);
            } else if(s2 == null) {
                // s2 has no measurement but s1 does, give small penalty:
                error += Math.abs(MAX - s1) * Math.abs(MAX - s1);
            } else {
                // subtract the absolute minimal measurement and add the squared error:
                s1 -= MIN;
                s2 -= MIN;
                error += Math.abs(s1 * s1 - s2 * s2);
            }
        }
        return error;
    }

    private class CapturePoint {
        private final Integer[] strength;
        public CapturePoint(Integer... strength) {
            this.strength = strength;
        }
    }
}
