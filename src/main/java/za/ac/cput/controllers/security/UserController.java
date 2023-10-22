package za.ac.cput.controllers.security;
/**
 *
 * Author: Peter Buckingham (220165289)
 *
 */
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.service.IUserService;
import za.ac.cput.domain.dto.LoginDto;
import za.ac.cput.domain.dto.RegisterDto;
import za.ac.cput.domain.security.User;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {


    private final IUserService iUserService ;

    //RessourceEndPoint:http://localhost:8080/api/user/register
    @PostMapping("/register")
    public ResponseEntity<?> register (@RequestBody RegisterDto registerDto)
    {
        System.out.println("Register called, registerDto = " + registerDto);
        return  iUserService.register(registerDto);
    }


    //RessourceEndPoint:http://localhost:8080/api/user/authenticate
    @PostMapping("/authenticate")
    public String authenticate(@RequestBody LoginDto loginDto)
    {
        System.out.println("Authenticate called, loginDto = " + loginDto);
        return  iUserService.authenticate(loginDto);
    }

    // Endpoint to get user profile
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Integer userId) {
        //get user profile
        User userProfile = iUserService.read(userId);
        // Return an appropriate response
        if (userProfile != null) {
            return ResponseEntity.ok(userProfile);
        } else {
            return ResponseEntity.status(404).body("User profile not found");
        }
    }

    // Endpoint to update user profile
    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> updateUserProfile(@PathVariable Integer userId, @RequestBody User user) {
        // Replace the following line with your logic to update user profile
        User updatedUser = iUserService.update(userId, user);
        return ResponseEntity.ok(updatedUser);

    }






}
