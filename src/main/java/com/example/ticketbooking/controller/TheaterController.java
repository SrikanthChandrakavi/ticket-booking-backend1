package com.example.ticketbooking.controller;

import com.example.ticketbooking.entity.Theater;
import com.example.ticketbooking.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theaters")
@CrossOrigin(origins = "http://localhost:5173")
public class TheaterController {
    @Autowired
    private TheaterRepository theaterRepo;

    @GetMapping
    public List<Theater> getAllTheaters() {
        return theaterRepo.findAll();
    }
}