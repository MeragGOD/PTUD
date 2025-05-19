package com.carrental.dto.response;

import com.carrental.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long bookingId;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private String transactionId;
    private LocalDateTime paymentDate;
    private String paymentMethod;
}
