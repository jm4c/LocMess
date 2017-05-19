package pt.tecnico.ulisboa.cmov.lmserver.model.types;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.IOException;
import java.io.Serializable;
import java.security.*;
import java.util.Arrays;

import static pt.tecnico.ulisboa.cmov.lmserver.utils.CryptoUtils.sign;
import static pt.tecnico.ulisboa.cmov.lmserver.utils.CryptoUtils.verify;
import static pt.tecnico.ulisboa.cmov.lmserver.utils.HashUtils.hash;

/**
 * Created by joaod on 19-May-17.
 */

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

}