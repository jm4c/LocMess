package pt.tecnico.ulisboa.cmov.lmserver.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pt.tecnico.ulisboa.cmov.lmserver.Singleton;
import pt.tecnico.ulisboa.cmov.lmserver.types.Location;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@RestController
public class LocationController {
    @RequestMapping(value = "/locations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Location> createNewAccount(@RequestHeader(value = "session") String sessionID,
                                           @RequestHeader(value = "hash") String clientLocationHash,
                                           HttpServletResponse response) throws IOException {
        Singleton singleton = Singleton.getInstance();
        List<Location> locations = null;
        int id = Integer.valueOf(sessionID);

        if (singleton.tokenExists(id)) {
            System.out.println("LOG: " + singleton.getToken(id).getUsername() + " checking for new locations. Current hash: " + clientLocationHash);
            if (!(singleton.getLocationsHash().equals(clientLocationHash))) {
                locations = singleton.getLocations();
                System.out.println("LOG: " + singleton.getToken(id).getUsername() + " downloaded new locations. New hash: " + singleton.getLocationsHash());
            }
        } else {
            System.out.println("LOG: No valid session ID found.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

        return locations;
    }
}
