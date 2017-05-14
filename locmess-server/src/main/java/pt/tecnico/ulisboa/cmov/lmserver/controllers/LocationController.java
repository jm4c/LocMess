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
public class LocationController {
    @RequestMapping(value = "/locations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Location> getLocations(@RequestHeader(value = "session") String sessionID,
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

    @RequestMapping(value = "/location", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean addLocation(@RequestHeader(value = "session") String sessionID,
                               @RequestBody Location location,
                               HttpServletResponse response) throws IOException {
        synchronized (this) {
            Singleton singleton = Singleton.getInstance();
            int id = Integer.valueOf(sessionID);

            if (singleton.tokenExists(id)) {
                System.out.println("LOG: " + singleton.getToken(id).getUsername() + " trying to add new location. Location: " + location.getName());
                if (!(singleton.locationExists(location))) {
                    try {
                        singleton.addLocation(location);
                        System.out.println("LOG: Location " + location.getName() + " added successfully.");
                        return true;
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    System.out.println("LOG: Failed to add new location.");
                }
            } else {
                System.out.println("LOG: No valid session ID found.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }

            return false;
        }
    }

    @RequestMapping(value = "/location", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean removeLocation(@RequestHeader(value = "session") String sessionID,
                                  @RequestHeader(value = "location") String locationName,
                                  HttpServletResponse response) throws IOException {
        synchronized (this) {
            Singleton singleton = Singleton.getInstance();
            int id = Integer.valueOf(sessionID);

            if (singleton.tokenExists(id)) {
                System.out.println("LOG: " + singleton.getToken(id).getUsername() + " trying to remove a location. Location: " + locationName);
                try {
                    boolean result = singleton.removeLocation(locationName);
                    if (result)
                        System.out.println("LOG: Location " + locationName + " removed successfully.");
                    else
                        System.out.println("LOG: Failed to remove location.");
                    return result;
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
