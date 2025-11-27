package com.example.ticketbooking.service;

import com.example.ticketbooking.entity.*;
import com.example.ticketbooking.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired private BookingRepository bookingRepo;
    @Autowired private SeatRepository seatRepo;
    @Autowired private ShowRepository showRepo;
    @Autowired private UserRepository userRepo;

    @Transactional
    public Booking createBooking(Long userId, Long showId, List<String> selectedSeats, double pricePerSeat) {
        logger.info("Creating booking for userId: {}, showId: {}, seats: {}", userId, showId, selectedSeats);

        // Validate user and show existence
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Show show = showRepo.findById(showId).orElseThrow(() -> new RuntimeException("Show not found"));

        // Check seats available
        List<Seat> availableSeats = seatRepo.findByShowIdAndStatus(showId, Seat.SeatStatus.AVAILABLE);
        List<String> availNumbers = availableSeats.stream().map(Seat::getSeatNumber).collect(Collectors.toList());
        for (String seat : selectedSeats) {
            if (!availNumbers.contains(seat)) {
                throw new RuntimeException("Seat " + seat + " not available");
            }
        }

        // Book seats
        selectedSeats.forEach(seatNum -> {
            Seat seat = seatRepo.findByShowIdAndStatus(showId, Seat.SeatStatus.AVAILABLE).stream()
                .filter(s -> s.getSeatNumber().equals(seatNum)).findFirst().orElse(null);
            if (seat != null) {
                seat.setStatus(Seat.SeatStatus.BOOKED);
                seatRepo.save(seat); // Save individually to ensure transaction consistency
                logger.info("Seat {} booked for show {}", seatNum, showId);
            }
        });

        // Create booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setSeats(selectedSeats);
        booking.setTotalAmount(BigDecimal.valueOf(selectedSeats.size() * pricePerSeat));
        booking.setStatus(Booking.BookingStatus.CONFIRMED); // Simulate payment success
        Booking savedBooking = bookingRepo.save(booking);
        logger.info("Booking created with ID: {}", savedBooking.getId());

        return savedBooking;
    }

    public List<Booking> getBookingsByShow(Long showId) {
        return bookingRepo.findByShowId(showId);
    }
}