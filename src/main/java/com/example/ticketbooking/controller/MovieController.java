package com.example.ticketbooking.controller;

import com.example.ticketbooking.entity.Movie;
import com.example.ticketbooking.entity.Seat;
import com.example.ticketbooking.entity.Seat.SeatStatus;
import com.example.ticketbooking.entity.Show;
import com.example.ticketbooking.entity.Theater;
import com.example.ticketbooking.repository.MovieRepository;
import com.example.ticketbooking.repository.SeatRepository;
import com.example.ticketbooking.repository.ShowRepository;
import com.example.ticketbooking.repository.TheaterRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "http://localhost:5173")
public class MovieController {

    @Autowired
    private MovieRepository movieRepo;
    @Autowired
    private ShowRepository showRepo;
    @Autowired
    private SeatRepository seatRepo;
    @Autowired
    private TheaterRepository theaterRepo;

    private final String uploadDir = "src/main/resources/static/posters/";

    // --------------------- ADD MOVIE ---------------------
    @PostMapping
    public ResponseEntity<String> addMovie(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("duration") String duration,
            @RequestParam("language") String language,
            @RequestParam("poster") MultipartFile poster,
            @RequestParam(value = "showDates", required = false) String[] showDates,
            @RequestParam(value = "showTimes", required = false) String[] showTimes,
            @RequestParam(value = "theaterIds", required = false) Long[] theaterIds,
            @RequestParam(value = "prices", required = false) Double[] prices) {

        try {
            // Create upload directory if missing
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            if (poster.isEmpty() || !poster.getContentType().startsWith("image/")) {
                return ResponseEntity.badRequest().body("Please upload a valid image file.");
            }

            String fileName = System.currentTimeMillis() + "_" + poster.getOriginalFilename().replaceAll("[^a-zA-Z0-9.-]", "_");
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(poster.getInputStream(), filePath);

            Movie movie = new Movie();
            movie.setTitle(title);
            movie.setDescription(description);
            movie.setDuration(duration);
            movie.setLanguage(language);
            movie.setPosterPath("/posters/" + fileName);
            movieRepo.save(movie);

            // Add shows if provided
            if (showDates != null && showTimes != null && theaterIds != null && prices != null &&
                showDates.length == showTimes.length &&
                showTimes.length == theaterIds.length &&
                theaterIds.length == prices.length) {

                for (int i = 0; i < showDates.length; i++) {
                    final int idx = i; // effectively final for lambda usage
                    Optional<Theater> optionalTheater = theaterRepo.findById(theaterIds[idx]);
                    if (optionalTheater.isEmpty()) {
                        return ResponseEntity.badRequest().body("Theater with ID " + theaterIds[idx] + " does not exist.");
                    }

                    Show show = new Show();
                    show.setMovie(movie);
                    show.setTheater(optionalTheater.get());
                    show.setShowtime(LocalDateTime.parse(showDates[idx] + "T" + showTimes[idx] + ":00"));
                    show.setPrice(prices[idx]);
                    showRepo.save(show);

                    // Initialize seats
                    initializeSeats(show);
                }
            }

            return ResponseEntity.ok("Added movie and shows successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error uploading file: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    // --------------------- UPDATE MOVIE ---------------------
    @PutMapping("/{id}")
    public ResponseEntity<String> updateMovie(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("duration") String duration,
            @RequestParam("language") String language,
            @RequestParam(value = "poster", required = false) MultipartFile poster,
            @RequestParam(value = "showIds", required = false) Long[] showIds,
            @RequestParam(value = "showDates", required = false) String[] showDates,
            @RequestParam(value = "showTimes", required = false) String[] showTimes,
            @RequestParam(value = "theaterIds", required = false) Long[] theaterIds,
            @RequestParam(value = "prices", required = false) Double[] prices) {

        try {
            Optional<Movie> optionalMovie = movieRepo.findById(id);
            if (optionalMovie.isEmpty()) return ResponseEntity.notFound().build();

            Movie movie = optionalMovie.get();
            movie.setTitle(title);
            movie.setDescription(description);
            movie.setDuration(duration);
            movie.setLanguage(language);

            // Update poster if provided
            if (poster != null && !poster.isEmpty()) {
                if (movie.getPosterPath() != null) {
                    Path oldFilePath = Paths.get(uploadDir + movie.getPosterPath().substring(9));
                    Files.deleteIfExists(oldFilePath);
                }
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

                String fileName = System.currentTimeMillis() + "_" + poster.getOriginalFilename().replaceAll("[^a-zA-Z0-9.-]", "_");
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(poster.getInputStream(), filePath);
                movie.setPosterPath("/posters/" + fileName);
            }

            movieRepo.save(movie);

            // Update shows
            if (showIds != null && showDates != null && showTimes != null && theaterIds != null && prices != null &&
                showIds.length == showDates.length &&
                showDates.length == showTimes.length &&
                showTimes.length == theaterIds.length &&
                theaterIds.length == prices.length) {

                for (int i = 0; i < showIds.length; i++) {
                    final int idx = i; // effectively final
                    Optional<Show> optionalShow = showRepo.findById(showIds[idx]);
                    Show show = optionalShow.orElseGet(Show::new);
                    show.setMovie(movie);

                    Optional<Theater> optionalTheater = theaterRepo.findById(theaterIds[idx]);
                    if (optionalTheater.isEmpty()) {
                        return ResponseEntity.badRequest().body("Theater with ID " + theaterIds[idx] + " does not exist.");
                    }
                    show.setTheater(optionalTheater.get());

                    show.setShowtime(LocalDateTime.parse(showDates[idx] + "T" + showTimes[idx] + ":00"));
                    show.setPrice(prices[idx]);
                    showRepo.save(show);
                }
            }

            return ResponseEntity.ok("Updated movie and shows successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error updating movie: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    // --------------------- GET MOVIES ---------------------
    @GetMapping
    public List<Movie> getAllMovies() {
        return movieRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Optional<Movie> movie = movieRepo.findById(id);
        return movie.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --------------------- DELETE MOVIE ---------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMovie(@PathVariable Long id) {
        try {
            Optional<Movie> optionalMovie = movieRepo.findById(id);
            if (optionalMovie.isEmpty()) return ResponseEntity.notFound().build();

            Movie movie = optionalMovie.get();
            if (movie.getPosterPath() != null) {
                Path filePath = Paths.get(uploadDir + movie.getPosterPath().substring(9));
                Files.deleteIfExists(filePath);
            }

            movieRepo.deleteById(id);
            return ResponseEntity.ok("Deleted successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error deleting movie: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    // --------------------- BOOK SEATS ---------------------
    @PostMapping("/{showId}/book")
    public ResponseEntity<String> bookSeats(@PathVariable Long showId, @RequestBody List<String> seatNumbers) {
        try {
            Optional<Show> optionalShow = showRepo.findById(showId);
            if (optionalShow.isEmpty()) return ResponseEntity.notFound().build();

            Show show = optionalShow.get();
            for (String seatNumber : seatNumbers) {
                final String seatNumFinal = seatNumber; // effectively final
                Optional<Seat> existingSeat = seatRepo.findByShowId(showId).stream()
                        .filter(seat -> seat.getSeatNumber().equals(seatNumFinal))
                        .findFirst();

                Seat seat = existingSeat.orElseGet(() -> {
                    Seat newSeat = new Seat();
                    newSeat.setShow(show);
                    newSeat.setSeatNumber(seatNumFinal);
                    return newSeat;
                });

                seat.setStatus(SeatStatus.BOOKED);
                seatRepo.save(seat);
            }

            return ResponseEntity.ok("Seats booked successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error booking seats: " + e.getMessage());
        }
    }

    // --------------------- INITIALIZE SEATS ---------------------
    private void initializeSeats(Show show) {
        for (int row = 0; row < 5; row++) {
            char rowLetter = (char) ('A' + row);
            for (int col = 1; col <= 10; col++) {
                final String seatNumber = rowLetter + String.valueOf(col); // effectively final
                if (seatRepo.findByShowId(show.getId()).stream().noneMatch(seat -> seat.getSeatNumber().equals(seatNumber))) {
                    Seat seat = new Seat();
                    seat.setShow(show);
                    seat.setSeatNumber(seatNumber);
                    seat.setStatus(SeatStatus.AVAILABLE);
                    seatRepo.save(seat);
                }
            }
        }
    }
}
