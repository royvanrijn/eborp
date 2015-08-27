package nl.royvanrijn.eborp.matching;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Stream;


public class Matcher {

    public static void main(String[] args) {
        new Matcher().run();
    }

    private static final int CAPTURE_DEVICES = 4;

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
        ReferenceCapture room1References = new ReferenceCapture("room 1", Arrays.asList(
                new CaptureSet(-40, -55, -79, null),
                new CaptureSet(-42, -51, -81, null),
                new CaptureSet(-45, -66, null, -50),
                new CaptureSet(-42, -52, -82, null)
        ));

        ReferenceCapture room2References = new ReferenceCapture("room 2", Arrays.asList(
                new CaptureSet(-60, -35, null, -79),
                new CaptureSet(-62, -31, null, -81),
                new CaptureSet(-65, -46, null, -50),
                new CaptureSet(-62, -32, -82, null)
        ));

        // Do this once we have altered the reference points
        PreMatchingStatistics preMatchingStatistics = createPreMatchingStatistics(room1References, room2References);

        // Given one 'measured' capture:
        MacCapture macCapture = new MacCapture("macAddress", new CaptureSet(-53,-55,null, -77));

        for(ReferenceCapture referenceCapture : new ReferenceCapture[] { room1References, room2References} ) {
            System.out.println("Best error of " + macCapture.getMacAddress() + " to " + referenceCapture.getLocationName() + " is " + calculateDistance(preMatchingStatistics, referenceCapture, macCapture));
            // TODO: If the lowest error to a room is below a certain threshold, we've got our match!
        }

    }

    private PreMatchingStatistics createPreMatchingStatistics(ReferenceCapture... references) {
        int maxStrength = getStreamOfStrengths(references).max(Comparator.naturalOrder()).get();
        int minStrength = getStreamOfStrengths(references).min(Comparator.naturalOrder()).get();
        return new PreMatchingStatistics(maxStrength, minStrength);
    }

    private Stream<Integer> getStreamOfStrengths(ReferenceCapture... references) {
        return Arrays.stream(references)
                .map(referenceCapture -> referenceCapture.getReferenceCaptures())
                .flatMap(Collection::stream)
                .map(c -> c.getStrength())
                .flatMap(Arrays::stream)
                .filter(c -> c != null);
    }

    private class PreMatchingStatistics {
        private int bestDBm;
        private int worstDBm;

        private PreMatchingStatistics(int bestDBm, int worstDBm) {
            this.bestDBm = bestDBm;
            this.worstDBm = worstDBm;
        }

        public int getBestDBm() {
            return bestDBm;
        }

        public int getWorstDBm() {
            return worstDBm;
        }
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
    private int calculateDistance(PreMatchingStatistics preMatchingStatistics, ReferenceCapture referenceCapture, MacCapture macCapture) {

        int lowestError = Integer.MAX_VALUE;

        for(CaptureSet referenceCaptureSet : referenceCapture.getReferenceCaptures()) {
            int error = 0;

            for(int rpi = 0; rpi < referenceCaptureSet.getStrength().length; rpi++) {
                Integer s1 = macCapture.getCapture().getStrength()[rpi];
                Integer s2 = referenceCaptureSet.getStrength()[rpi];

                if(s1 == s2) {
                    //Powers are null or exactly the same, no error!
                } else if(s1 == null) {
                    // s1 has no measurement but s2 does, give small penalty:
                    error += Math.abs(preMatchingStatistics.getWorstDBm() - s2) * Math.abs(preMatchingStatistics.getWorstDBm() - s2);
                } else if(s2 == null) {
                    // s2 has no measurement but s1 does, give small penalty:
                    error += Math.abs(preMatchingStatistics.getWorstDBm() - s1) * Math.abs(preMatchingStatistics.getWorstDBm() - s1);
                } else {
                    // subtract the absolute minimal measurement and add the squared error:
                    s1 -= preMatchingStatistics.getBestDBm();
                    s2 -= preMatchingStatistics.getBestDBm();
                    error += Math.abs(s1 * s1 - s2 * s2);
                }
            }

            lowestError = Math.min(lowestError, error);
        }

        return lowestError;
    }

}
