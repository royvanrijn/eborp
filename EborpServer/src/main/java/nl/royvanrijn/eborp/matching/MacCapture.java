package nl.royvanrijn.eborp.matching;

public class MacCapture {

    private final String macAddress;
    private final CaptureSet capture;

    public MacCapture(String macAddress, CaptureSet capture) {
        this.macAddress = macAddress;
        this.capture = capture;
    }

    public CaptureSet getCapture() {
        return capture;
    }

    public String getMacAddress() {
        return macAddress;
    }
}
