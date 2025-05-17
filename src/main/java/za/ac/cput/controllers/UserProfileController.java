package za.ac.cput.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.dto.UserDTO;
import za.ac.cput.domain.mapper.UserMapper;
import za.ac.cput.domain.security.User;
import za.ac.cput.security.JwtUtilities;
import za.ac.cput.service.impl.UserService;
import za.ac.cput.service.impl.UserServiceorig;

@RestController
@RequestMapping("/api/user/profile")
public class UserProfileController {

    private final JwtUtilities jwtUtilities;
    private final UserService userService;

    @Autowired
    public UserProfileController(JwtUtilities jwtUtilities, UserService userService) {
        this.jwtUtilities = jwtUtilities;
        this.userService = userService;
    }

    @GetMapping("/read/profile")
    public ResponseEntity<UserDTO> getUserProfile(HttpServletRequest request) {
        String token = jwtUtilities.getToken(request);

        if (token == null || !jwtUtilities.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userEmail = jwtUtilities.extractUserEmail(token);
        User user = userService.read(userEmail);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    @PutMapping("/update")
    public ResponseEntity<UserDTO> updateUserProfile(@RequestBody User updatedUser, HttpServletRequest request) {
        String token = jwtUtilities.getToken(request);

        if (token == null || !jwtUtilities.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userEmail = jwtUtilities.extractUserEmail(token);
        User existingUser = userService.read(userEmail);

        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword()); // Service handles encoding

        User savedUser = userService.update(existingUser);
        return ResponseEntity.ok(UserMapper.toDto(savedUser));
    }
}
