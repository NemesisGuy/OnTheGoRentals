package za.ac.cput.controllers.admin;

/**
 * AdminUserController.java
 * This is the controller for the user entity
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.User;
import za.ac.cput.service.impl.UserServiceImpl;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserServiceImpl userService;

    @RequestMapping("/list/all")
    public ArrayList<User> getAll() {
        ArrayList<User> users = new ArrayList<>(userService.getAll());
        return users;
    }

    @RequestMapping("/list/{argument}")
    public ArrayList<User> getAllByArgument(@PathVariable String argument) {
        ArrayList<User> users = new ArrayList<>(userService.getAll());
        users.removeIf(user -> !user.toString().toLowerCase().contains(argument.toLowerCase()));
        return users;
    }

    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        System.out.println("/api/admin/users/create was triggered");
        System.out.println("UserService was created...attempting to create user...");
        User createdUser = userService.create(user);
        return createdUser;
    }
    @GetMapping("/read/{userId}")
    public User readUser(@PathVariable Integer userId) {
        System.out.println("/api/admin/users/read was triggered");
        System.out.println("UserService was created...attempting to read user...");
        User readUser = userService.read(userId);
        return readUser;
    }
    @PutMapping("/update/{userId}")
    public User updateUser(@PathVariable int userId, @RequestBody User updatedUser) {
        System.out.println("/api/admin/users/update was triggered");
        System.out.println("UserService was created...attempting to update user...");
        User userToUpdate = userService.update(updatedUser);
        return userToUpdate;
    }
    @DeleteMapping("/delete/{userId}")
    public boolean deleteUser(@PathVariable Integer userId) {
        System.out.println("/api/admin/users/delete was triggered");
        System.out.println("UserService was created...attempting to delete user...");
        return userService.delete(userId);
    }
}
