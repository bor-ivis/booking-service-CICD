package Pensionaten.controller;

import Pensionaten.repositories.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingApiController {

    private final BookingRepository bookingRepository;

    @GetMapping("/customer/{customerId}/exists")
    public boolean hasActiveBookings(@PathVariable Long customerId) {
        return bookingRepository.existsByCustomerId(customerId);
    }
}

