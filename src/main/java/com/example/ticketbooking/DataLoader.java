package com.example.ticketbooking;

import com.example.ticketbooking.entity.Theater;
import com.example.ticketbooking.repository.TheaterRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final TheaterRepository theaterRepo;

    public DataLoader(TheaterRepository theaterRepo) {
        this.theaterRepo = theaterRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        if (theaterRepo.count() == 0) {
            Theater t1 = new Theater();
            t1.setName("INOX Laila Mall");
            t1.setLocation("Laila Mall");

            Theater t2 = new Theater();
            t2.setName("G3 Raj YuvRaj");
            t2.setLocation("Raj Nagar");

            Theater t3 = new Theater();
            t3.setName("Sailaja Theatre");
            t3.setLocation("Main Street");

            theaterRepo.save(t1);
            theaterRepo.save(t2);
            theaterRepo.save(t3);

            System.out.println("Predefined theaters inserted.");
        }
    }
}
