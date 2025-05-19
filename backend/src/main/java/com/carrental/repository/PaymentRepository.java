package com.carrental.repository;

import com.carrental.model.Booking;
import com.carrental.model.Payment;
import com.carrental.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBooking(Booking booking);
    List<Payment> findByPaymentStatus(PaymentStatus status);
}
