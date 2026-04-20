package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Booking;
import com.example.demo.entity.enums.BookingStatus;


import java.util.List;

public interface BookingRepository extends JpaRepository<Booking,Long>{

    // Find bookings by status
    List<Booking> findByStatus(BookingStatus status);

    // Find bookings by shipper ID
    List<Booking> findByShipper_ShipperID(Long shipperID);

  
}
