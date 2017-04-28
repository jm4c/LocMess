package pt.tecnico.ulisboa.cmov.lmserver.controllers;

import org.springframework.web.bind.annotation.*;
import pt.tecnico.ulisboa.cmov.lmserver.Singleton;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static pt.tecnico.ulisboa.cmov.lmserver.utils.CryptoUtils.getSalt;
import static pt.tecnico.ulisboa.cmov.lmserver.utils.HashUtils.hashInText;


@RestController
public class AccountController {
    @RequestMapping(value = "/account", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody boolean createNewAccount(@RequestHeader(value = "username") String username,
                                                  @RequestHeader(value = "password") String password) {
        Singleton singleton = Singleton.getInstance();
        try {
            if(!singleton.usernameExists(username)){
                byte[] salt = getSalt();
                String hashedPassword = hashInText(password, salt);
                singleton.createAccount(username, hashedPassword, salt);
                return true;
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
