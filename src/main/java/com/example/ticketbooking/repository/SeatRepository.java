package com.example.ticketbooking.repository;

import com.example.ticketbooking.entity.Seat;
import com.example.ticketbooking.entity.Seat.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByShowId(Long showId);
    List<Seat> findByShowIdAndStatus(Long showId, SeatStatus status); // New method
}