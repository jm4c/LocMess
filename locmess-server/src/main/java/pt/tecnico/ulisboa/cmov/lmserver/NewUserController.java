package pt.tecnico.ulisboa.cmov.lmserver;

import org.springframework.web.bind.annotation.*;
import pt.tecnico.ulisboa.cmov.lmserver.utils.DatabaseManager;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;


import static pt.tecnico.ulisboa.cmov.lmserver.utils.CryptoUtils.getSalt;
import static pt.tecnico.ulisboa.cmov.lmserver.utils.HashUtils.hashInText;


@RestController
class NewUserController {

    @RequestMapping(value = "/newuser/{username}", method = RequestMethod.POST)
    public boolean createNewUser(@PathVariable("username") String username, @RequestHeader(value = "password") String password) {
        String salt = DatatypeConverter.printBase64Binary(getSalt());


        try {
            Connection databaseConnection = DatabaseManager.getConnection();
            String hashedPassword = hashInText(password, salt.getBytes());
            if (DatabaseManager.userExists(username))
                DatabaseManager.addNewUser(databaseConnection, username, hashedPassword, salt);
            else
                return false;
            databaseConnection.close();
        } catch (SQLException | NoSuchAlgorithmException | IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
