package pt.tecnico.ulisboa.cmov.lmserver;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.tecnico.ulisboa.cmov.lmserver.types.Location;
import pt.tecnico.ulisboa.cmov.lmserver.types.Message;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }

    @RequestMapping("/test")
    public Message test() {
        return new Message (1, "title", "cs", "adas", new Location("lisboa","sa"), "ter", "erter");
    }
}
