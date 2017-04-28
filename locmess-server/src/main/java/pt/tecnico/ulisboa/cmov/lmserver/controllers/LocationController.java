package pt.tecnico.ulisboa.cmov.lmserver.controllers;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.tecnico.ulisboa.cmov.lmserver.Singleton;
import pt.tecnico.ulisboa.cmov.lmserver.types.Location;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static pt.tecnico.ulisboa.cmov.lmserver.utils.CryptoUtils.getSalt;
import static pt.tecnico.ulisboa.cmov.lmserver.utils.HashUtils.hash;
import static pt.tecnico.ulisboa.cmov.lmserver.utils.HashUtils.hashInText;


@RestController
public class LocationController {
    @RequestMapping(value = "/location/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createNewAccount(@RequestHeader(value = "password") String hash) {
        Singleton singleton = Singleton.getInstance();

        if (!(singleton.getLocationsHash().equals(hash))) {
            List<Location> locations = singleton.getLocations();

            for (Location l: locations) {
//                JSONObject location = new JSONPObject();

            }

        }



        return new ResponseEntity<>(HttpStatus.OK);
    }
}
