package za.ac.cput.controllers;
/**
 * IndexController.java
 * This is the controller for the Car class
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 */


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;
//TODO: remove this class this was just for testing purposes
@RestController
public class IndexController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping({"/", "/home", "/index", "/api/home", "/api/hello", "/api/index", "/api/greeting"})
    public Message greeting() {
        return new Message("Congratulations you are visitor number: ", counter.incrementAndGet());
    }

    public record Message(String content, long id) {
    }
}