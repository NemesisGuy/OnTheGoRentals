package za.ac.cput.backend.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.User;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @PostMapping("api/user/register")
    public String register(@RequestBody User loginRequest) {
        // Extract the registration details from the request object
        String username = loginRequest.getFirstName();
        String password = loginRequest.getPassword();
        String email = loginRequest.getEmail();
        System.out.println("Username: " + username);
        System.out.println("This user is now registered");
        // Implement the logic to handle user registration
        // Retrieve the user details from the request and process it
        // Return an appropriate response, such as a success message or error message
        return "User registered successfully";
    }

    @PostMapping("api/user/login")
    public String login(@RequestBody User loginRequest) {
        // Implement the logic to handle user login
        // Retrieve the user credentials from the request and authenticate the user
        // Return an appropriate response, such as a success message or error message
        return "User logged in successfully";
    }

}
