package com.carrental.security;

import com.carrental.model.Payment;
import com.carrental.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("paymentSecurity")
@RequiredArgsConstructor
public class PaymentSecurity {

    private final PaymentRepository paymentRepository;

    public boolean isOwner(Authentication authentication, Long paymentId) {
        String email = authentication.getName();
        return paymentRepository.findById(paymentId)
                .map(payment -> payment.getBooking().getUser().getEmail().equals(email))
                .orElse(false);
    }
}
