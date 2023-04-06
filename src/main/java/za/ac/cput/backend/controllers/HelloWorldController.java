package za.ac.cput.backend.controllers;
/**
 *  HelloWorldController.java
 *  This is a test controller class
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 */

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @GetMapping("/hello")
    public String hello() {
        return "<h1>Hello, world!</h1> </br> <h3>Im a test controller!!</h3>";
    }
}
