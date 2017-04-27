package pt.tecnico.ulisboa.cmov.lmserver;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static pt.tecnico.ulisboa.cmov.lmserver.utils.CryptoUtils.getSalt;
import static pt.tecnico.ulisboa.cmov.lmserver.utils.HashUtils.hashInText;


@RestController
class AccountController {
    @RequestMapping(value = "/account/new/{username}", method = RequestMethod.POST)
    public ResponseEntity createNewAccount(@PathVariable("username") String username, @RequestHeader(value = "password") String password) {
        Singleton singleton = Singleton.getInstance();
        try {
            if(singleton.usernameExists(username)){
                byte[] salt = getSalt();
                String hashedPassword = hashInText(password, salt);
                singleton.createAccount(username, hashedPassword, salt);
                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return  new ResponseEntity<>(HttpStatus.CONFLICT);
    }
}
