package pt.ulisboa.tecnico.cmov.locmess.model.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Profile implements Serializable {

    private List<ProfileKeypair> profileKeypairs;

    public Profile(List<ProfileKeypair> profileKeypairs) {
        this.profileKeypairs = profileKeypairs;
    }

    public Profile() {
        this.profileKeypairs = new ArrayList<>();
    }

    public List<ProfileKeypair> getProfileKeypairs() {
        return profileKeypairs;
    }

    public void setProfileKeypairs(List<ProfileKeypair> profileKeypairs) {
        this.profileKeypairs = profileKeypairs;
    }

    public void addKeypair(ProfileKeypair keypair) {
        profileKeypairs.add(keypair);
    }

    public ProfileKeypair getKeypair(String key) {
        for (ProfileKeypair keyPair : profileKeypairs) {
            if (keyPair.getKey().equals(key))
                return keyPair;
        }
        return null;
    }
}
