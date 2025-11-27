package com.example.ticketbooking.controller;

import com.example.ticketbooking.entity.Booking;
import com.example.ticketbooking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:5173")
public class BookingController {
    @Autowired private BookingService bookingService;

    @PostMapping
    public Booking createBooking(@RequestBody BookingRequest request) {
        return bookingService.createBooking(request.getUserId(), request.getShowId(), request.getSeats(), request.getPricePerSeat());
    }

    @GetMapping("/show/{showId}")
    public List<Booking> getBookingsByShow(@PathVariable Long showId) {
        return bookingService.getBookingsByShow(showId);
    }
}

// DTO for request
class BookingRequest {
    private Long userId;
    private Long showId;
    private List<String> seats;
    private double pricePerSeat;

    // Getters/Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getShowId() { return showId; }
    public void setShowId(Long showId) { this.showId = showId; }
    public List<String> getSeats() { return seats; }
    public void setSeats(List<String> seats) { this.seats = seats; }
    public double getPricePerSeat() { return pricePerSeat; }
    public void setPricePerSeat(double pricePerSeat) { this.pricePerSeat = pricePerSeat; }
}