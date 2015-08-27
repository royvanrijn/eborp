package nl.royvanrijn.eborp.matching;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReferenceCapture {

    private final String locationName;
    private final List<Map<String, Integer>> referenceRpiStrengths = new ArrayList<>();

    public ReferenceCapture(String locationName, List<Map<String, Integer>> referenceRpiStrengths) {
        this.locationName = locationName;
        this.referenceRpiStrengths.addAll(referenceRpiStrengths);
    }

    public String getLocationName() {
        return locationName;
    }

    public List<Map<String, Integer>> getReferenceRpiStrengths() {
        return referenceRpiStrengths;
    }
}
