package com.carrental.dto.response;

import com.carrental.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private Long id;
    private UserResponse user;
    private CarResponse car;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalPrice;
    private BookingStatus status;
    private LocalDate createdAt;
}
