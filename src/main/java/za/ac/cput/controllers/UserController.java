/*
package za.ac.cput.controllers;

*/
/**
 * UserController.java
 * This is the controller for the user pages
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 *//*


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
*/
/*import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;*//*

import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.User;
import za.ac.cput.service.impl.UserServiceImpl;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    @Qualifier("userServiceImpl")
    private UserServiceImpl userService;

    @PostMapping("/register")
    public User register(@RequestBody User registrationRequest) {
        // Extract the registration details from the request object
        String username = registrationRequest.getUserName();
        String password = registrationRequest.getPassword();
        String email = registrationRequest.getEmail();
        System.out.println("Username: " + username);
        System.out.println("This user is now registered");
        User createdUser = userService.create(registrationRequest);
        // Implement the logic to handle user registration
        // Retrieve the user details from the request and process it
        // Return an appropriate response, such as a success message or error message
        return createdUser;
    }


    @PostMapping("/login")
    public String login(@RequestBody User loginRequest) {
        // Implement the logic to handle user login
        String username = loginRequest.getUserName();
        String password = loginRequest.getPassword();
        return "User logged in successfully";

    */
/*    // Authenticate the user
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Return an appropriate response
        if (authentication.isAuthenticated()) {
            // User is authenticated
            return "User logged in successfully";
        } else {
            // Authentication failed
            return "Authentication failed";
        }*//*

    }


    @GetMapping("/profile/{userId}")
    public User readUser(@PathVariable Integer userId) {
        System.out.println("/api/admin/users/read was triggered");
        System.out.println("UserService was created...attempting to read user...");
        User readUser = userService.read(userId);
        return readUser;
    }

}
*/
