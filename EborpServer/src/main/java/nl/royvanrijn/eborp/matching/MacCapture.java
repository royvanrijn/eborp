package nl.royvanrijn.eborp.matching;

import java.util.Map;

public class MacCapture {

    private final String macAddress;
    private final Map<String, Integer> rpiStrengths;

    public MacCapture(String macAddress, Map<String, Integer> rpiStrengths) {
        this.macAddress = macAddress;
        this.rpiStrengths = rpiStrengths;
    }

    public Map<String, Integer> getRpiStrengths() {
        return rpiStrengths;
    }

    public String getMacAddress() {
        return macAddress;
    }
}
