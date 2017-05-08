package pt.tecnico.ulisboa.cmov.lmserver.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pt.tecnico.ulisboa.cmov.lmserver.Singleton;
import pt.tecnico.ulisboa.cmov.lmserver.types.Location;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;


@RestController
public class ProfileKeyController {
    @RequestMapping(value = "/profilekey", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> getProfileKeys(@RequestHeader(value = "session") String sessionID,
                                           @RequestHeader(value = "hash") String availableKeysHash,
                                           HttpServletResponse response) throws IOException {
        Singleton singleton = Singleton.getInstance();
        List<String> profileKeys = null;
        int id = Integer.valueOf(sessionID);

        if (singleton.tokenExists(id)) {
            System.out.println("LOG: " + singleton.getToken(id).getUsername() + " checking for new profile keys. Current hash: " + availableKeysHash);
            if (!(singleton.getProfileKeysHash().equals(availableKeysHash))) {
                profileKeys = singleton.getProfileKeys();
                System.out.println("LOG: " + singleton.getToken(id).getUsername() + " downloaded new profile keys. New hash: " + singleton.getLocationsHash());
            }
        } else {
            System.out.println("LOG: No valid session ID found.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

        return profileKeys;
    }

    @RequestMapping(value = "/profilekey", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean addProfileKey(@RequestHeader(value = "session") String sessionID,
                                           @RequestBody String profileKey,
                                           HttpServletResponse response) throws IOException {
        Singleton singleton = Singleton.getInstance();
        int id = Integer.valueOf(sessionID);

        if (singleton.tokenExists(id)) {
            System.out.println("LOG: " + singleton.getToken(id).getUsername() + " trying to add new profile key. Profile key: " + profileKey);
            if (!(singleton.profileKeyExists(profileKey))) {
                try {
                    singleton.addProfileKey(profileKey);
                    System.out.println("LOG: Profile key '" + profileKey + "' added successfully.");
                    return true;
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                System.out.println("LOG: Failed to add new profile key.");
            }
        } else {
            System.out.println("LOG: No  valid session ID found.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

        return false;
    }
}
