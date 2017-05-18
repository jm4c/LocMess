package pt.tecnico.ulisboa.cmov.lmserver.controllers;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pt.tecnico.ulisboa.cmov.lmserver.Singleton;
import pt.tecnico.ulisboa.cmov.lmserver.model.containers.MessagesContainer;
import pt.tecnico.ulisboa.cmov.lmserver.model.types.Message;
import pt.tecnico.ulisboa.cmov.lmserver.model.types.Profile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class MessageController {
    @RequestMapping(value = "/message", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public MessagesContainer getMessages(@RequestHeader(value = "session") String sessionID,
                                         @RequestHeader(value = "location") String location,
                                         @RequestBody Profile profile,
                                         HttpServletResponse response) throws IOException {
        Singleton singleton = Singleton.getInstance();
        int id = Integer.valueOf(sessionID);

        MessagesContainer messagesContainer = null;

        if (singleton.tokenExists(id)) {
            messagesContainer = singleton.getUserMessagesContainer(Integer.valueOf(sessionID), location, profile);
        } else {
            System.out.println("LOG: No valid session ID found.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

        return messagesContainer;
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
                if (result)
                    System.out.println("LOG: Message successfully added to server.");
                else
                    System.out.println("LOG: Failed to add message to server.");
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
