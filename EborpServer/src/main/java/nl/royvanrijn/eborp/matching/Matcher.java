package nl.royvanrijn.eborp.matching;

import com.google.common.collect.ImmutableMap;

import java.util.*;
import java.util.stream.Collectors;
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
                new ImmutableMap.Builder<String, Integer>().put("rpi1", -40).put("rpi2", -55).put("rpi3", -79).build(),
                new ImmutableMap.Builder<String, Integer>().put("rpi1", -42).put("rpi2", -51).put("rpi3", -81).build(),
                new ImmutableMap.Builder<String, Integer>().put("rpi1", -45).put("rpi2", -66).put("rpi4", -50).build(),
                new ImmutableMap.Builder<String, Integer>().put("rpi1", -42).put("rpi2", -52).put("rpi3", -82).build()
        ));

        ReferenceCapture room2References = new ReferenceCapture("room 2", Arrays.asList(
                new ImmutableMap.Builder<String, Integer>().put("rpi1", -60).put("rpi2", -32).put("rpi4", -79).build(),
                new ImmutableMap.Builder<String, Integer>().put("rpi1", -62).put("rpi2", -31).put("rpi4", -81).build(),
                new ImmutableMap.Builder<String, Integer>().put("rpi1", -65).put("rpi2", -46).put("rpi4", -50).build(),
                new ImmutableMap.Builder<String, Integer>().put("rpi1", -62).put("rpi2", -32).put("rpi3", -82).build()
        ));

        // Do this once we have altered the reference points
        PreMatchingStatistics preMatchingStatistics = createPreMatchingStatistics(room1References, room2References);

        // Given one 'measured' capture:

        MacCapture macCapture = new MacCapture("macAddress", new ImmutableMap.Builder<String, Integer>().put("rpi1", -53).put("rpi2", -55).put("rpi4", -77).build());

        for(ReferenceCapture referenceCapture : new ReferenceCapture[] { room1References, room2References} ) {
            System.out.println("Best error of " + macCapture.getMacAddress() + " to " + referenceCapture.getLocationName() + " is " + calculateDistance(preMatchingStatistics, referenceCapture, macCapture));
            // TODO: If the lowest error to a room is below a certain threshold, we've got our match!
        }

    }

    private PreMatchingStatistics createPreMatchingStatistics(ReferenceCapture... references) {
        int maxStrength = getStreamOfStrengths(references).max(Comparator.naturalOrder()).get();
        int minStrength = getStreamOfStrengths(references).min(Comparator.naturalOrder()).get();
        Set<String> allRpis = Arrays.stream(references)
                .map(referenceCapture -> referenceCapture.getReferenceRpiStrengths())
                .flatMap(Collection::stream)
                .map(Map::keySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        return new PreMatchingStatistics(maxStrength, minStrength, allRpis);
    }

    private Stream<Integer> getStreamOfStrengths(ReferenceCapture... references) {
        return Arrays.stream(references)
                .map(referenceCapture -> referenceCapture.getReferenceRpiStrengths())
                .flatMap(Collection::stream)
                .map(Map::values)
                .flatMap(Collection::stream);
    }

    private class PreMatchingStatistics {
        private int bestDBm;
        private int worstDBm;
        private Set<String> allRpis;

        private PreMatchingStatistics(int bestDBm, int worstDBm, Set<String> allRpis) {
            this.bestDBm = bestDBm;
            this.worstDBm = worstDBm;
            this.allRpis = allRpis;
        }

        public Set<String> getAllRpis() {
            return allRpis;
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

        Map<String, Integer> macRpiStrengths = macCapture.getRpiStrengths();
        for(Map<String, Integer> referenceRpiStrengths : referenceCapture.getReferenceRpiStrengths()) {
            int error = 0;
            for(String rpiSource : preMatchingStatistics.getAllRpis()) {
                Integer refStrength = referenceRpiStrengths.get(rpiSource);
                Integer macStrength = macRpiStrengths.get(rpiSource);
                error += calculateErrorBetweenDBm(preMatchingStatistics, refStrength, macStrength);
            }
            lowestError = Math.min(lowestError, error);
        }

        return lowestError;
    }

    private int calculateErrorBetweenDBm(PreMatchingStatistics preMatchingStatistics, Integer s1, Integer s2) {
        if(s1 == null && s2 == null) {
            //Powers are null or exactly the same, no error!
            return 0;
        } else if(s1 == null) {
            // s1 has no measurement but s2 does, give small penalty:
            return Math.abs(preMatchingStatistics.getWorstDBm() - s2) * Math.abs(preMatchingStatistics.getWorstDBm() - s2);
        } else if(s2 == null) {
            // s2 has no measurement but s1 does, give small penalty:
            return Math.abs(preMatchingStatistics.getWorstDBm() - s1) * Math.abs(preMatchingStatistics.getWorstDBm() - s1);
        } else {
            // subtract the absolute minimal measurement and add the squared error:
            s1 -= preMatchingStatistics.getBestDBm();
            s2 -= preMatchingStatistics.getBestDBm();
            return Math.abs(s1 * s1 - s2 * s2);
        }
    }

}
