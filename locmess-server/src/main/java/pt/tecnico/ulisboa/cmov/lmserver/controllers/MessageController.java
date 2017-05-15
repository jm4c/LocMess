package pt.tecnico.ulisboa.cmov.lmserver.controllers;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pt.tecnico.ulisboa.cmov.lmserver.Singleton;
import pt.tecnico.ulisboa.cmov.lmserver.types.Message;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MessageController {
    @RequestMapping(value = "/message", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean getMessages(@RequestHeader(value = "session") String sessionID,
                                                 @RequestHeader(value = "hash") String availableKeysHash,
                                                 HttpServletResponse response) throws IOException {
        Singleton singleton = Singleton.getInstance();
        int id = Integer.valueOf(sessionID);

        if (singleton.tokenExists(id)) {

        } else {
            System.out.println("LOG: No valid session ID found.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

        return false;
    }

    @RequestMapping(value = "/message", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean addMessage(@RequestHeader(value = "session") String sessionID,
                                 @RequestBody Message message,
                                 HttpServletResponse response) throws IOException {
        synchronized (this) {
            Singleton singleton = Singleton.getInstance();
            int id = Integer.valueOf(sessionID);

            Boolean result = null;

            if (singleton.tokenExists(id)) {
                result = singleton.addMessage(message);
            } else {
                System.out.println("LOG: No valid session ID found.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
            return result;
        }
    }

    @RequestMapping(value = "/message", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean removeMessage(@RequestHeader(value = "session") String sessionID,
                                    @RequestHeader(value = "key") String profileKey,
                                    HttpServletResponse response) throws IOException {
        synchronized (this) {
            Singleton singleton = Singleton.getInstance();
            int id = Integer.valueOf(sessionID);

            if (singleton.tokenExists(id)) {

            } else {
                System.out.println("LOG: No valid session ID found.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }

            return false;
        }
    }
}
