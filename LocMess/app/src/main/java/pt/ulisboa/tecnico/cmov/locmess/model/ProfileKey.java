package pt.ulisboa.tecnico.cmov.locmess.model;

/**
 * Created by joaod on 10-Apr-17.
 */

public class ProfileKey extends ListItem {
    private String key;
    private String value;

    public ProfileKey(String key, String value) {
        this.key = key;
        this.value = value;
        setTitle(key + " = " + value);
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
