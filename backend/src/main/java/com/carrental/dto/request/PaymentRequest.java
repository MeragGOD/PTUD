package com.carrental.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    // Simulated payment details
    private String cardNumber;
    private String cardExpiry;
    private String cardCvv;
}
