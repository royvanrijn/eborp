package nl.royvanrijn.eborp.matching;

public class CaptureSet {

    private final Integer[] strength;

    public CaptureSet(Integer... strength) {
        this.strength = strength;
    }

    public Integer[] getStrength() {
        return strength;
    }
}
