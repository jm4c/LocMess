package pt.tecnico.ulisboa.cmov.lmserver.model.types;

import java.io.Serializable;
import java.util.List;


public class Policy implements Serializable {

    private List<ProfileKeypair> keyValues;
    private boolean isWhitelist = true;

    public Policy(List<ProfileKeypair> keyValues, boolean isWhitelist) {
        this.keyValues = keyValues;
        this.isWhitelist = isWhitelist;
    }

    public Policy() {

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

    public boolean matches(Profile profile) {
        for (ProfileKeypair keypair : keyValues) {

            if (isWhitelist) { // true if profile contains all values in policy (one key pair not contained results in false)
                if (!profile.getProfileKeypairs().contains(keypair))
                    return false;

            } else { // true if profile does not contain any value in the policy (one key pair contained results in false)
                if (profile.getProfileKeypairs().contains(keypair))
                    return false;

            }
        }
        return true;
    }
}
