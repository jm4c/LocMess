package pt.tecnico.ulisboa.cmov.lmserver.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pt.tecnico.ulisboa.cmov.lmserver.Singleton;
import pt.tecnico.ulisboa.cmov.lmserver.types.AvailableKeysContainer;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;


@RestController
public class ProfileKeyController {
    @RequestMapping(value = "/profilekey", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AvailableKeysContainer getProfileKeys(@RequestHeader(value = "session") String sessionID,
                                                 @RequestHeader(value = "hash") String availableKeysHash,
                                                 HttpServletResponse response) throws IOException {
        Singleton singleton = Singleton.getInstance();
        AvailableKeysContainer availableKeysContainer = null;
        int id = Integer.valueOf(sessionID);

        if (singleton.tokenExists(id)) {
            System.out.println("LOG: " + singleton.getToken(id).getUsername() + " checking for new profile keys. Current hash: " + availableKeysHash);
            if (!(singleton.getProfileKeysHash().equals(availableKeysHash))) {
                availableKeysContainer = singleton.getAvailableKeysContainer();
                System.out.println("LOG: " + singleton.getToken(id).getUsername() + " downloaded new profile keys. New hash: " + singleton.getProfileKeysHash());
            }
        } else {
            System.out.println("LOG: No valid session ID found.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

        return availableKeysContainer;
    }

    @RequestMapping(value = "/profilekey", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean addProfileKey(@RequestHeader(value = "session") String sessionID,
                                 @RequestBody String profileKey,
                                 HttpServletResponse response) throws IOException {
        synchronized (this) {
            Singleton singleton = Singleton.getInstance();
            int id = Integer.valueOf(sessionID);

            if (singleton.tokenExists(id)) {
                System.out.println("LOG: " + singleton.getToken(id).getUsername() + " trying to add new profile key. Profile key: " + profileKey);

                try {
                    singleton.addProfileKey(profileKey, id);
                    return true;
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                System.out.println("LOG: Failed to add new profile key.");

            } else {
                System.out.println("LOG: No valid session ID found.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }

            return false;
        }
    }

    @RequestMapping(value = "/profilekey", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean removeProfileKey(@RequestHeader(value = "session") String sessionID,
                                    @RequestHeader(value = "key") String profileKey,
                                    HttpServletResponse response) throws IOException {
        synchronized (this) {
            Singleton singleton = Singleton.getInstance();
            int id = Integer.valueOf(sessionID);

            if (singleton.tokenExists(id)) {
                System.out.println("LOG: " + singleton.getToken(id).getUsername() + " trying to remove a profile key. Profile key: " + profileKey);
                try {
                    boolean result = singleton.removeProfileKey(profileKey, id);
                    if (result)
                        System.out.println("LOG: Profile key " + profileKey + " removed successfully.");
                    else
                        System.out.println("LOG: Failed to remove profile key.");
                    return true;
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("LOG: No valid session ID found.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }

            return false;
        }
    }
}
