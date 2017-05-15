package pt.tecnico.ulisboa.cmov.lmserver.types;

import java.io.Serializable;
import java.util.List;


public class Policy implements Serializable{

    private List<ProfileKeypair> keyValues;
    private boolean isWhitelist = true;

    public Policy(List<ProfileKeypair> keyValues, boolean isWhitelist) {
        this.keyValues = keyValues;
        this.isWhitelist = isWhitelist;
    }

    public Policy(){

    }

    public List<ProfileKeypair> getKeyValues() {
        return keyValues;
    }

    public void setKeyValues(List<ProfileKeypair> keyValues) {
        this.keyValues = keyValues;
    }

    public boolean isWhitelist() {
        return isWhitelist;
    }

    public void setWhitelist(boolean whitelist) {
        isWhitelist = whitelist;
    }
}
