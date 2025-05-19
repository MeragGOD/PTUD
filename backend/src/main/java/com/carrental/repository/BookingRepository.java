package com.carrental.repository;

import com.carrental.model.Booking;
import com.carrental.model.BookingStatus;
import com.carrental.model.Car;
import com.carrental.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    List<Booking> findByCar(Car car);
    List<Booking> findByStatus(BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.car.id = :carId AND b.status != 'CANCELLED' AND " +
            "((b.startDate BETWEEN :startDate AND :endDate) OR " +
            "(b.endDate BETWEEN :startDate AND :endDate) OR " +
            "(:startDate BETWEEN b.startDate AND b.endDate) OR " +
            "(:endDate BETWEEN b.startDate AND b.endDate))")
    List<Booking> findOverlappingBookings(
            @Param("carId") Long carId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
