package pt.tecnico.ulisboa.cmov.lmserver.controllers;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pt.tecnico.ulisboa.cmov.lmserver.Singleton;
import pt.tecnico.ulisboa.cmov.lmserver.model.containers.MessagesContainer;
import pt.tecnico.ulisboa.cmov.lmserver.model.containers.SecureMessagesContainer;
import pt.tecnico.ulisboa.cmov.lmserver.model.types.Message;
import pt.tecnico.ulisboa.cmov.lmserver.model.types.Profile;
import pt.tecnico.ulisboa.cmov.lmserver.model.types.SecureMessage;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class SecureMessageController {
    @RequestMapping(value = "/securemessage", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SecureMessagesContainer getSecureMessages(@RequestHeader(value = "session") String sessionID,
                                               @RequestHeader(value = "location") String location,
                                               @RequestBody Profile profile,
                                               HttpServletResponse response) throws IOException {
        Singleton singleton = Singleton.getInstance();
        int id = Integer.valueOf(sessionID);

        SecureMessagesContainer secureMessagesContainer = null;

        if (singleton.tokenExists(id)) {
            secureMessagesContainer = singleton.getUserSecureMessagesContainer(Integer.valueOf(sessionID), location, profile);
        } else {
            System.out.println("LOG: No valid session ID found.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

        return secureMessagesContainer;
    }

    @RequestMapping(value = "/securemessage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean addMessage(@RequestHeader(value = "session") String sessionID,
                              @RequestBody SecureMessage secureMessage,
                              HttpServletResponse response) throws IOException {
        synchronized (this) {
            Singleton singleton = Singleton.getInstance();
            int id = Integer.valueOf(sessionID);

            Boolean result = null;

            if (singleton.tokenExists(id)) {
                result = singleton.addSecureMessage(secureMessage);
                if (result)
                    System.out.println("LOG: Secure message successfully added to server.");
                else
                    System.out.println("LOG: Failed to add secure message to server.");
            } else {
                System.out.println("LOG: No valid session ID found.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
            return result;
        }
    }

    @RequestMapping(value = "/securemessage", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean removeMessage(@RequestHeader(value = "session") String sessionID,
                                 @RequestHeader(value = "message") String secureMessageTitle,
                                 HttpServletResponse response) throws IOException {
        synchronized (this) {
            Singleton singleton = Singleton.getInstance();
            int id = Integer.valueOf(sessionID);
            if (singleton.tokenExists(id)) {
                return singleton.removeSecureMessage(id, secureMessageTitle);
            } else {
                System.out.println("LOG: No valid session ID found.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }

            return false;
        }
    }
}
