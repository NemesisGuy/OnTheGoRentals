package za.ac.cput.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.security.User;
import za.ac.cput.security.JwtUtilities;
import za.ac.cput.service.impl.UserService;

@RestController
@RequestMapping("/api/user/profile")
public class UserProfileController {

    @Autowired
    private JwtUtilities jwtUtilities;

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public User getUserProfile(HttpServletRequest request) {
        String token = jwtUtilities.getToken(request);

        if (token != null && jwtUtilities.validateToken(token)) {
            String userEmail = jwtUtilities.extractUsername(token);

            return userService.read(userEmail);
        } else {
            // Handle case where token is invalid or not present
            // You might want to return an error response or throw an exception
            return null; // Modify this to suit your needs
        }
    }
    @PutMapping("/update")
    public User updateUserProfile(@RequestBody User updatedUser, HttpServletRequest request) {
        String token = jwtUtilities.getToken(request);

        if (token != null && jwtUtilities.validateToken(token)) {
            String userEmail = jwtUtilities.extractUsername(token);

            User existingUser = userService.read(userEmail);

            if (existingUser != null) {

                existingUser.setFirstName(updatedUser.getFirstName());
                existingUser.setLastName(updatedUser.getLastName());
                existingUser.setEmail(updatedUser.getEmail());
//                existingUser.setPassword(updatedUser.getPassword());

                return userService.update(existingUser);
            }
        }

        return null;
    }

}
