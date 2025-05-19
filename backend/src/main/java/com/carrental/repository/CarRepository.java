package com.carrental.repository;

import com.carrental.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByAvailableTrue();
    List<Car> findByMakeContainingIgnoreCase(String make);
    List<Car> findByModelContainingIgnoreCase(String model);
    List<Car> findByPricePerDayBetween(BigDecimal min, BigDecimal max);

    @Query("SELECT c FROM Car c WHERE " +
            "(:make IS NULL OR LOWER(c.make) LIKE LOWER(CONCAT('%', :make, '%'))) AND " +
            "(:model IS NULL OR LOWER(c.model) LIKE LOWER(CONCAT('%', :model, '%'))) AND " +
            "(:minPrice IS NULL OR c.pricePerDay >= :minPrice) AND " +
            "(:maxPrice IS NULL OR c.pricePerDay <= :maxPrice) AND " +
            "(:available IS NULL OR c.available = :available)")
    List<Car> searchCars(
            @Param("make") String make,
            @Param("model") String model,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("available") Boolean available);
}
