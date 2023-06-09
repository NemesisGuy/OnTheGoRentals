package za.ac.cput.backend.controllers;
/**
 *  HelloWorldController.java
 *  This is a test controller class
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 */
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)

public class HelloWorldController {
  //  @GetMapping({ "/", "/home", "/index", "/api/home", "/api/hello", "/api/index" })
    public String hello() {
        return "{\"message\": \"Hello, world!\", \"description\": \"I'm a test controller!\"}";
    }
}
