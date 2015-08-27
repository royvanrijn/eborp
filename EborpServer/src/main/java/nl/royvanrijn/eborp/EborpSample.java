package nl.royvanrijn.eborp;

import java.util.Map;

/**
 * @author Vincent Hartsteen
 */
public class EborpSample {
//{"epoch":1430961733158,"mac":"a0:39:f7:4b:ba:48","dbm":-33,"source":e9f5bff0-bf12-45b3-b532-0d4fccced24a}

    private final long epoch;
    private final String mac;
    private final int dbm;
    private final String source;

    public EborpSample(long epoch, String mac, int dbm, String source) {
        this.epoch = epoch;
        this.mac = mac;
        this.dbm = dbm;
        this.source = source;
    }

    public long getEpoch() {
        return epoch;
    }

    public String getMac() {
        return mac;
    }

    public int getDbm() {
        return dbm;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "EborpSample{" +
                "epoch=" + epoch +
                ", mac='" + mac + '\'' +
                ", dbm=" + dbm +
                ", source='" + source + '\'' +
                '}';
    }
}
