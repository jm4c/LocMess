package pt.tecnico.ulisboa.cmov.lmserver.utils;

import org.apache.tomcat.util.codec.binary.Base64;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static pt.tecnico.ulisboa.cmov.lmserver.utils.CryptoUtils.serialize;

public class HashUtils {

    public static byte[] hash(Object msg, byte[] salt) throws NoSuchAlgorithmException, IOException {

        MessageDigest md = MessageDigest.getInstance("SHA1");

        if (salt != null) {
            md.update(salt);
        }

        byte[] serializedMsg = serialize(msg);

        return md.digest(serializedMsg);
    }

    public static String hashInText(Object msg, byte[] salt) throws IOException, NoSuchAlgorithmException {
        return Base64.encodeBase64String(hash(msg,salt));

    }

}
