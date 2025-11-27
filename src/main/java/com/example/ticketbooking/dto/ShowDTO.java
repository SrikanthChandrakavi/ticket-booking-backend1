package com.example.ticketbooking.dto;

import java.time.LocalDateTime;

public class ShowDTO {
    private Long id;
    private LocalDateTime showtime;
    private double price;
    private String theaterName;
    private int availableSeats;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getShowtime() { return showtime; }
    public void setShowtime(LocalDateTime showtime) { this.showtime = showtime; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getTheaterName() { return theaterName; }
    public void setTheaterName(String theaterName) { this.theaterName = theaterName; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
}
