package za.ac.cput.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.impl.User;
import za.ac.cput.service.impl.ICarServiceImpl;
import za.ac.cput.service.impl.IUserServiceImpl;


import java.util.ArrayList;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private IUserServiceImpl userService;

    @PostMapping("/register")
    public String register(@RequestBody User registrationRequest) {
        // Extract the registration details from the request object
        String username = registrationRequest.getUserName();
        String password = registrationRequest.getPassword();
        String email = registrationRequest.getEmail();
        System.out.println("Username: " + username);
        System.out.println("This user is now registered");
        // Implement the logic to handle user registration
        // Retrieve the user details from the request and process it
        // Return an appropriate response, such as a success message or error message
        return "User registered successfully";
    }

    @PostMapping("/login")
    public String login(@RequestBody User loginRequest) {
        // Implement the logic to handle user login
        // Retrieve the user credentials from the request and authenticate the user
        // Return an appropriate response, such as a success message or error message
        return "User logged in successfully";
    }
    @RequestMapping("/getall")
    public ArrayList<User> getAll() {
        ArrayList<User> users = new ArrayList<>(userService.getAll());

        return users;
    }


}
