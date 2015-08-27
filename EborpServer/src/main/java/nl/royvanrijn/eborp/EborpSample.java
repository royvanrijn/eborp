package nl.royvanrijn.eborp;

/**
 * @author Vincent Hartsteen
 */
public class EborpSample {
//{"epoch":1430961733158,"mac":"a0:39:f7:4b:ba:48","dbm":-33,"source":e9f5bff0-bf12-45b3-b532-0d4fccced24a}

    private long epoch;
    private String mac;
    private int dbm;
    private String source;

    public long getEpoch() {
        return epoch;
    }

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getDbm() {
        return dbm;
    }

    public void setDbm(int dbm) {
        this.dbm = dbm;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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
