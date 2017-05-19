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
        if (isWhitelist) {
            for (ProfileKeypair policyKeyPair : keyValues) {
                boolean foundPair = false;
                for (ProfileKeypair profileKeypair : profile.getProfileKeypairs()) {
                    if (policyKeyPair.getKey().equals(profileKeypair.getKey()))
                        if (policyKeyPair.getValue().equals(profileKeypair.getValue())) {
                            foundPair = true;
                        } else {
                            System.out.println("WHITELIST: " + policyKeyPair.getKey() + "=" + policyKeyPair.getValue() + " in policy does \n" +
                                    "not match  " + profileKeypair.getKey() + "=" + profileKeypair.getValue() + " in profile.");
                            return false;
                        }
                }
                if (!foundPair) {
                    System.out.println("WHITELIST: " + policyKeyPair.getKey() + "=" + policyKeyPair.getValue() + " not found in profile.");
                    return false;
                }
            }

        } else {
            for (ProfileKeypair policyKeyPair : keyValues) {
                for (ProfileKeypair profileKeypair : profile.getProfileKeypairs()) {
                    if (policyKeyPair.getKey().equals(profileKeypair.getKey())
                            && policyKeyPair.getValue().equals(profileKeypair.getValue())) {
                        System.out.println("BLACKLIST: " + policyKeyPair.getKey() + "=" + policyKeyPair.getValue() + " found in profile.");
                        return false;
                    }

                }
            }
        }
        return true;
    }
}

