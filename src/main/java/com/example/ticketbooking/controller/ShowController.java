package com.example.ticketbooking.controller;

import com.example.ticketbooking.dto.ShowDTO;
import com.example.ticketbooking.entity.Seat;
import com.example.ticketbooking.entity.Show;
import com.example.ticketbooking.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shows")
@CrossOrigin(origins = "http://localhost:5173")
public class ShowController {

    @Autowired
    private ShowRepository showRepo;

    // Get shows for a specific movie
    @GetMapping("/movie/{movieId}")
    public List<ShowDTO> getShowsByMovie(@PathVariable Long movieId) {
        List<Show> shows = showRepo.findByMovieId(movieId);

        return shows.stream().map(show -> {
            ShowDTO dto = new ShowDTO();
            dto.setId(show.getId());
            dto.setShowtime(show.getShowtime());
            dto.setPrice(show.getPrice());
            dto.setTheaterName(show.getTheater().getName());
            dto.setAvailableSeats((int) show.getSeats().stream()
                                       .filter(s -> s.getStatus() == Seat.SeatStatus.AVAILABLE)
                                       .count());
            return dto;
        }).toList();
    }

    // Get show by ID
    @GetMapping("/{id}")
    public ResponseEntity<Show> getShowById(@PathVariable Long id) {
        Optional<Show> show = showRepo.findById(id);
        return show.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get available seats for a show
    @GetMapping("/{id}/seats")
    public List<String> getAvailableSeats(@PathVariable Long id) {
        Optional<Show> showOpt = showRepo.findById(id);
        if (showOpt.isEmpty() || showOpt.get().getSeats() == null) return List.of();

        Show show = showOpt.get();
        return show.getSeats().stream()
                .filter(s -> s.getStatus() == Seat.SeatStatus.AVAILABLE)
                .map(Seat::getSeatNumber)
                .collect(Collectors.toList());
    }
}
