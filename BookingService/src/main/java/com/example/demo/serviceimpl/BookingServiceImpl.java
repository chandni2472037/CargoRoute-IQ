package com.example.demo.serviceimpl;

import com.example.demo.dto.BookingDTO;
import com.example.demo.dto.ShipperDTO;
import com.example.demo.entity.Booking;
import com.example.demo.entity.Shipper;
import com.example.demo.entity.enums.BookingStatus;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.ShipperRepository;
import com.example.demo.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service // Marks this class as a Spring-managed service component
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository repo;

    @Autowired
    private ShipperRepository shipperRepo;

    // Save a new booking or update an existing booking
    public BookingDTO createBooking(BookingDTO b){
        Booking booking = convertToEntity(b);
        Booking saved = repo.save(booking);
        return convertToDTO(saved);
    }

    // Fetch all bookings from the database
    public List<BookingDTO> getAllBookings(){
        return repo.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Fetch a single booking by ID
    public BookingDTO getBookingById(Long id) {
        Booking booking = repo.findById(id).orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Booking with ID " + id + " not found"));
        return convertToDTO(booking);
    }

   

    // Update only the status of an existing booking
    public BookingDTO updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = repo.findById(id).orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Booking with ID " + id + " not found"));
        booking.setStatus(status);
        Booking updated = repo.save(booking);
        return convertToDTO(updated);
    }

    // Retrieve bookings by status
    public List<BookingDTO> getByBookingStatus(BookingStatus status) {
        return repo.findByStatus(status).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Retrieve bookings by shipper ID
    public List<BookingDTO> getByShipperId(Long shipperID) {
        return repo.findByShipper_ShipperID(shipperID).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

  

    private Booking convertToEntity(BookingDTO dto) {
        Booking booking = new Booking();
        booking.setBookingID(dto.getBookingID());
        if (dto.getShipper() != null) {
            booking.setShipper(convertShipperToEntity(dto.getShipper()));
        }
        booking.setOriginSiteID(dto.getOriginSiteID());
        booking.setDestinationSiteID(dto.getDestinationSiteID());
        booking.setPickupWindowStart(dto.getPickupWindowStart());
        booking.setPickupWindowEnd(dto.getPickupWindowEnd());
        booking.setDeliveryWindowStart(dto.getDeliveryWindowStart());
        booking.setDeliveryWindowEnd(dto.getDeliveryWindowEnd());
        booking.setWeightKg(dto.getWeightKg());
        booking.setVolumeM3(dto.getVolumeM3());
        booking.setPieces(dto.getPieces());
        booking.setCommodity(dto.getCommodity());
        booking.setSpecialHandlingFlags(dto.getSpecialHandlingFlags());
        booking.setStatus(dto.getStatus());
        // createdAt is set by @CreationTimestamp
        return booking;
    }

    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setBookingID(booking.getBookingID());
        dto.setShipper(convertShipperToDTO(booking.getShipper()));
        dto.setOriginSiteID(booking.getOriginSiteID());
        dto.setDestinationSiteID(booking.getDestinationSiteID());
        dto.setPickupWindowStart(booking.getPickupWindowStart());
        dto.setPickupWindowEnd(booking.getPickupWindowEnd());
        dto.setDeliveryWindowStart(booking.getDeliveryWindowStart());
        dto.setDeliveryWindowEnd(booking.getDeliveryWindowEnd());
        dto.setWeightKg(booking.getWeightKg());
        dto.setVolumeM3(booking.getVolumeM3());
        dto.setPieces(booking.getPieces());
        dto.setCommodity(booking.getCommodity());
        dto.setSpecialHandlingFlags(booking.getSpecialHandlingFlags());
        dto.setStatus(booking.getStatus());
        dto.setCreatedAt(booking.getCreatedAt());
        return dto;
    }

    private Shipper convertShipperToEntity(ShipperDTO dto) {
        if (dto == null) return null;
        if (dto.getShipperID() == null) {
            throw new com.example.demo.exception.BadRequestException("Shipper ID is required");
        }
        return shipperRepo.findById(dto.getShipperID())
                .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Shipper with ID " + dto.getShipperID() + " not found"));
    }

    private ShipperDTO convertShipperToDTO(Shipper shipper) {
        if (shipper == null) return null;
        ShipperDTO dto = new ShipperDTO();
        dto.setShipperID(shipper.getShipperID());
        dto.setName(shipper.getName());
        dto.setContactInfo(shipper.getContactInfo());
        dto.setAccountTerms(shipper.getAccountTerms());
        dto.setStatus(shipper.getStatus());
        return dto;
    }
}