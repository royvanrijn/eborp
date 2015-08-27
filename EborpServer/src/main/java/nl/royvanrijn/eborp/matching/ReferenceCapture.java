package nl.royvanrijn.eborp.matching;

import java.util.ArrayList;
import java.util.List;

public class ReferenceCapture {

    private final String locationName;
    private final List<CaptureSet> referenceCaptures = new ArrayList<>();

    public ReferenceCapture(String locationName, List<CaptureSet> captureSet) {
        this.locationName = locationName;
        this.referenceCaptures.addAll(captureSet);
    }

    public String getLocationName() {
        return locationName;
    }

    public List<CaptureSet> getReferenceCaptures() {
        return referenceCaptures;
    }
}
