package za.ac.cput.controllers;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    public record Message(String content, long id) { }

    @GetMapping({ "/", "/home", "/index", "/api/home", "/api/hello", "/api/index","/api/greeting" })
    public Message greeting() {
        return new Message("Congratulations you are visitor number: " , counter.incrementAndGet());
    }
}