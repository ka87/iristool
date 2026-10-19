package Common.SettingsManager;

public class SettingData {
    private String n, v;
    public SettingData(String n, String v) {
        this.n = n;
        this.v = v;
    }
    public String getName() {
        return n;
    }
    public String getValue() {
        return v;
    }
    public void setValue(String v) {
        this.v = v;
    }
}
