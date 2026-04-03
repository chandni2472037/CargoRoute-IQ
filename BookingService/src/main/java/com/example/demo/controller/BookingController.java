package com.example.demo.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.BookingDTO;
import com.example.demo.entity.enums.BookingStatus;
import com.example.demo.service.BookingService;

@RestController 
@RequestMapping("/bookings") 
public class BookingController {

    @Autowired
    private BookingService service; 

    @PostMapping
    public ResponseEntity<BookingDTO> addBooking(@RequestBody BookingDTO b) {
        BookingDTO savedBooking = service.createBooking(b); // Delegate saving to service layer
        return new ResponseEntity<>(savedBooking, HttpStatus.CREATED); // 201 Created response
    }

    @GetMapping
    public ResponseEntity<List<BookingDTO>> fetchAllBookings() {
        List<BookingDTO> bookings = service.getAllBookings(); // Fetch list from service
        return ResponseEntity.ok(bookings); // 200 OK with list
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getByBookingId(@PathVariable Long id) {
        BookingDTO booking = service.getBookingById(id); // Fetch by ID
        return (booking != null)
                ? ResponseEntity.ok(booking) // 200 OK if found
                : ResponseEntity.notFound().build(); // 404 Not Found if not
    }
    
    
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingDTO> modifyBookingStatus( @PathVariable Long id, @RequestParam BookingStatus status) { // Pass status as request param
        
        BookingDTO updated = service.updateBookingStatus(id, status); // Delegate to service
        if (updated != null) {
            return ResponseEntity.ok(updated); // 200 OK with updated booking
        }
        return ResponseEntity.notFound().build(); // 404 if booking not found
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookingDTO>> fetcgByBookingStatus(@PathVariable BookingStatus status) {
        List<BookingDTO> bookings = service.getByBookingStatus(status);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/shipper/{shipperId}")
    public ResponseEntity<List<BookingDTO>> getByShipperId(@PathVariable Long shipperId) {
        List<BookingDTO> bookings = service.getByShipperId(shipperId);
        return ResponseEntity.ok(bookings);
    }
 
}         