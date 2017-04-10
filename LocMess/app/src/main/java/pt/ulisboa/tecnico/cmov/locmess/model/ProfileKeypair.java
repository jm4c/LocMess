package pt.ulisboa.tecnico.cmov.locmess.model;

/**
 * Created by joaod on 10-Apr-17.
 */

public class ProfileKeypair extends ListItem {
    private String key;
    private String value;

    public ProfileKeypair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
