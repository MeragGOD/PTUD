package com.carrental.security;

import com.carrental.model.Booking;
import com.carrental.repository.BookingRepository;
import com.carrental.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("bookingSecurity")
@RequiredArgsConstructor
public class BookingSecurity {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public boolean isOwner(Authentication authentication, Long bookingId) {
        String email = authentication.getName();
        return bookingRepository.findById(bookingId)
                .map(booking -> booking.getUser().getEmail().equals(email))
                .orElse(false);
    }
}
