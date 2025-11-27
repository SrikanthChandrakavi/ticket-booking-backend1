package com.example.ticketbooking.service;

import com.example.ticketbooking.entity.Movie;
import com.example.ticketbooking.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired private MovieRepository movieRepo;

    public Movie addMovie(Movie movie) {
        return movieRepo.save(movie);
    }

    // Add more methods for promotions, etc., later
}