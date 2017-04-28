package pt.ulisboa.tecnico.cmov.locmess.model;

import java.util.HashMap;

/**
 * Created by joaod on 28-Apr-17.
 */

public class Policy {

    private HashMap<String,String> keyValues;
    private boolean isWhitelist;

    public Policy(HashMap<String, String> keyValues, boolean isWhitelist) {
        this.keyValues = keyValues;
        this.isWhitelist = isWhitelist;
    }

    public HashMap<String, String> getKeyValues() {
        return keyValues;
    }

    public void setKeyValues(HashMap<String, String> keyValues) {
        this.keyValues = keyValues;
    }

    public boolean isWhitelist() {
        return isWhitelist;
    }

    public void setWhitelist(boolean whitelist) {
        isWhitelist = whitelist;
    }
}
