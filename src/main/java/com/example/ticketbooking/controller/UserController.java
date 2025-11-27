package com.example.ticketbooking.controller;

import com.example.ticketbooking.entity.User;
import com.example.ticketbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    @Autowired
    private UserRepository userRepo;

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepo.save(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        Optional<User> user = userRepo.findByEmail(loginRequest.getEmail());
        if (user.isPresent() && user.get().getPassword().equals(loginRequest.getPassword())) {
            return user.get().getId() + ""; // Return user ID as string
        }
        return "Invalid credentials";
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userRepo.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User user = userRepo.findById(id).orElse(null);
        if (user != null) {
            // Only update provided fields, keep others as is
            if (userDetails.getName() != null) user.setName(userDetails.getName());
            if (userDetails.getEmail() != null) user.setEmail(userDetails.getEmail()); // Ensure uniqueness
            if (userDetails.getPassword() != null) user.setPassword(userDetails.getPassword());
            if (userDetails.getAge() != null) user.setAge(userDetails.getAge());
            if (userDetails.getGender() != null) user.setGender(userDetails.getGender());
            if (userDetails.getPhoneNumber() != null) user.setPhoneNumber(userDetails.getPhoneNumber());
            return userRepo.save(user);
        }
        return null;
    }
}

class LoginRequest {
    private String email;
    private String password;

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}