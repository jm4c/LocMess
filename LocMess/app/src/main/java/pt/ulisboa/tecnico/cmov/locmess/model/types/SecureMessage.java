package pt.ulisboa.tecnico.cmov.locmess.model.types;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Arrays;

import static pt.ulisboa.tecnico.cmov.locmess.utils.CryptoUtils.sign;
import static pt.ulisboa.tecnico.cmov.locmess.utils.CryptoUtils.verify;
import static pt.ulisboa.tecnico.cmov.locmess.utils.HashUtils.hash;

public class SecureMessage implements Serializable {
    private Message message;
    private byte[] signedHash;
    private byte[] signature;

    public SecureMessage(Message message, PrivateKey privateKey) {
        try {
            this.message = message;
            this.signedHash = hash(message, null);
            this.signature = sign(signedHash, privateKey);
        } catch (NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public SecureMessage() {
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public byte[] getSignedHash() {
        return signedHash;
    }

    public void setSignedHash(byte[] signedHash) {
        this.signedHash = signedHash;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    @JsonIgnore
    public boolean checkIntegrity() {
        try {
            return Arrays.equals(hash(message, null), signedHash);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    @JsonIgnore
    public boolean verifySignature(PublicKey publicKey) {
        try {
            return verify(signedHash, publicKey, signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return false;
        }
    }
}
