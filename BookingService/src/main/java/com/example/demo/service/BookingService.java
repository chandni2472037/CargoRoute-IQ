package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.BookingDTO;
import com.example.demo.entity.enums.BookingStatus;

public interface BookingService {

    // Save a new booking (or update if the entity contains an existing ID)
    public BookingDTO createBooking(BookingDTO b);

    // Retrieve all bookings from the database
    public List<BookingDTO> getAllBookings();

    // Retrieve a single booking by its ID
    public BookingDTO getBookingById(Long id);


    // Update only the status field of an existing booking
    public BookingDTO updateBookingStatus(Long id, BookingStatus status);

    // Retrieve bookings by status
    public List<BookingDTO> getByBookingStatus(BookingStatus status);

    // Retrieve bookings by shipper ID
    public List<BookingDTO> getByShipperId(Long shipperID);

   
}