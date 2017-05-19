package pt.tecnico.ulisboa.cmov.lmserver.controllers;

import org.springframework.web.bind.annotation.*;
import pt.tecnico.ulisboa.cmov.lmserver.Singleton;
import pt.tecnico.ulisboa.cmov.lmserver.model.containers.AvailableKeysContainer;
import pt.tecnico.ulisboa.cmov.lmserver.utils.CryptoUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;


@RestController
public class AccountController {
    @RequestMapping(value = "/account", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    boolean createNewAccount(@RequestHeader(value = "username") String username,
                             @RequestHeader(value = "password") String password) {
        Singleton singleton = Singleton.getInstance();
        try {
            if (!singleton.usernameExists(username)) {
                singleton.createAccount(username, password);
                return true;
            }
            System.out.println("LOG: Account '" + username + "' already exists");
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    int login(@RequestHeader(value = "username") String username,
              @RequestHeader(value = "password") String password) {
        Singleton singleton = Singleton.getInstance();
        try {
            if (singleton.usernameExists(username)) {
                return singleton.login(username, password);
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    //SECURITY
    @RequestMapping(value = "/publickey", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    boolean addPublicKey(@RequestHeader(value = "session") String sessionID,
                             @RequestBody byte[] serializedPublicKey,
                             HttpServletResponse response) throws IOException {
        PublicKey publicKey = null;
        try {
            publicKey = (PublicKey) CryptoUtils.deserialize(serializedPublicKey);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Singleton singleton = Singleton.getInstance();
        int id = Integer.valueOf(sessionID);
        if (singleton.tokenExists(id)) {
            return singleton.addPublicKey(id, publicKey);
        } else {
            System.out.println("LOG: No valid session ID found.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }


        return false;
    }


}


