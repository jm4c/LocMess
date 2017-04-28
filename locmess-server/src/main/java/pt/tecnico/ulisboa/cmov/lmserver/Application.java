package pt.tecnico.ulisboa.cmov.lmserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import pt.tecnico.ulisboa.cmov.lmserver.controllers.AccountController;

@ComponentScan(basePackageClasses = AccountController.class)
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
