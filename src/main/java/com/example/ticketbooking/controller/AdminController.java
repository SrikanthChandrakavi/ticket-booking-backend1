package com.example.ticketbooking.controller;

import com.example.ticketbooking.entity.Movie;
import com.example.ticketbooking.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {
    @Autowired private AdminService adminService;
    @Autowired private MovieController movieController;  // Reuse for listings

    @PostMapping("/movies")
    public Movie addMovie(@RequestBody Movie movie) {
        return adminService.addMovie(movie);
    }

    @GetMapping("/bookings")
    public List<Movie> getAllBookings() {  // Placeholder; extend to return all bookings
        return movieController.getAllMovies();  // Demo: returns movies for now
    }
}