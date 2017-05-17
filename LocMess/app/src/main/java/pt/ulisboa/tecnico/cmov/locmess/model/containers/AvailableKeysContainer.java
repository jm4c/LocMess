package pt.ulisboa.tecnico.cmov.locmess.model.containers;

import java.util.ArrayList;
import java.util.List;

public class AvailableKeysContainer {
    private String keysHash;
    private List<String> keys;

    public AvailableKeysContainer() {
        keys = new ArrayList<>();
        keysHash = "";
    }

    public String getKeysHash() {
        return keysHash;
    }

    public void setKeysHash(String keysHash) {
        this.keysHash = keysHash;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public boolean addKey(String profileKey) {
        return keys.add(profileKey);
    }

    public boolean removeKey(String profileKey) {
        return keys.remove(profileKey);
    }
}
