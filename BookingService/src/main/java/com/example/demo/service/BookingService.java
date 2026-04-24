package com.example.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.BookingDTO;
import com.example.demo.entity.enums.BookingStatus;

public interface BookingService {

    /**
     * Persist a new booking. The backend always derives the owner from
     * {@code createdByUserId} (IAM numeric user ID) — any value in
     * {@code b.createdByUserId} sent from the frontend is ignored.
     *
     * @param b               booking payload from the request body
     * @param createdByUserId numeric IAM userId extracted from the JWT
     */
    public BookingDTO createBooking(BookingDTO b, Long createdByUserId);

    /**
     * Retrieve bookings with role-based visibility:
     * <ul>
     *   <li>Admin  → all bookings</li>
     *   <li>Others → only bookings created by {@code userId}</li>
     * </ul>
     */
    public List<BookingDTO> getAllBookings(Long userId, String role);

    // Retrieve a single booking by its ID
    public BookingDTO getBookingById(Long id);


    // Update only the status field of an existing booking
    @PreAuthorize("hasAnyRole('DISPATCHER','DRIVER','WAREHOUSEMANAGER')")
    public BookingDTO updateBookingStatus(Long id, BookingStatus status);

    // Retrieve bookings by status
    public List<BookingDTO> getByBookingStatus(BookingStatus status);

    // Retrieve bookings by shipper ID
    public List<BookingDTO> getByShipperId(Long shipperID);

    // Bulk import bookings from a CSV file
    public Map<String, Object> importBookings(MultipartFile file) throws Exception;
}