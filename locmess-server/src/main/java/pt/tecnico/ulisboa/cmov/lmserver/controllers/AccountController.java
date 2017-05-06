package pt.tecnico.ulisboa.cmov.lmserver.controllers;

import org.springframework.web.bind.annotation.*;
import pt.tecnico.ulisboa.cmov.lmserver.Singleton;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;


@RestController
public class AccountController {
    @RequestMapping(value = "/account", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody boolean createNewAccount(@RequestHeader(value = "username") String username,
                                                  @RequestHeader(value = "password") String password) {
        Singleton singleton = Singleton.getInstance();
        try {
            if(!singleton.usernameExists(username)){
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
    public @ResponseBody int login(@RequestHeader(value = "username") String username,
                                                  @RequestHeader(value = "password") String password) {
        Singleton singleton = Singleton.getInstance();
        try {
            if(singleton.usernameExists(username)){
                return singleton.login(username, password);
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
    }
}


