package za.ac.cput.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

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