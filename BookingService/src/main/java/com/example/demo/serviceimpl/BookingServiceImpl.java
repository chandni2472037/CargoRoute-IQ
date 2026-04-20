package com.example.demo.serviceimpl;

import com.example.demo.dto.BookingDTO;
import com.example.demo.dto.ShipperDTO;
import com.example.demo.entity.Booking;
import com.example.demo.entity.Shipper;
import com.example.demo.entity.enums.BookingStatus;
import com.example.demo.exception.BadRequestException;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.ShipperRepository;
import com.example.demo.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service // Marks this class as a Spring-managed service component
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository repo;

    @Autowired
    private ShipperRepository shipperRepo;

    // Save a new booking or update an existing booking
    public BookingDTO createBooking(BookingDTO b){
        if (b == null) {
            throw new BadRequestException("Booking request body must not be null");
        }
        if (b.getShipper() == null || b.getShipper().getShipperID() == null) {
            throw new BadRequestException("A valid shipper ID is required");
        }
        if (b.getOriginSiteID() == null) {
            throw new BadRequestException("Origin site ID is required");
        }
        if (b.getDestinationSiteID() == null) {
            throw new BadRequestException("Destination site ID is required");
        }
        if (b.getOriginSiteID().equals(b.getDestinationSiteID())) {
            throw new BadRequestException("Origin and destination site IDs must be different");
        }
        if (b.getCommodity() == null || b.getCommodity().trim().isEmpty()) {
            throw new BadRequestException("Commodity description is required");
        }
        if (b.getPickupWindowStart() == null) {
            throw new BadRequestException("Pickup window start time is required");
        }
        if (b.getPickupWindowEnd() == null) {
            throw new BadRequestException("Pickup window end time is required");
        }
        if (!b.getPickupWindowEnd().isAfter(b.getPickupWindowStart())) {
            throw new BadRequestException("Pickup window end must be after pickup window start");
        }
        if (b.getDeliveryWindowStart() != null && b.getDeliveryWindowEnd() != null
                && !b.getDeliveryWindowEnd().isAfter(b.getDeliveryWindowStart())) {
            throw new BadRequestException("Delivery window end must be after delivery window start");
        }
        if (b.getWeightKg() == null || b.getWeightKg() <= 0) {
            throw new BadRequestException("Weight must be a positive number");
        }
        if (b.getVolumeM3() == null || b.getVolumeM3() <= 0) {
            throw new BadRequestException("Volume must be a positive number");
        }
        if (b.getPieces() == null || b.getPieces() <= 0) {
            throw new BadRequestException("Pieces must be a positive integer");
        }
        // Default to SUBMITTED if no status is provided on creation
        if (b.getStatus() == null) {
            b.setStatus(BookingStatus.SUBMITTED);
        }
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

    // Bulk import bookings from CSV
    // Expected CSV columns (with header row):
    // shipperId,originSiteID,destinationSiteID,pickupWindowStart,pickupWindowEnd,
    // deliveryWindowStart,deliveryWindowEnd,weightKg,volumeM3,pieces,commodity,specialHandlingFlags
    // Dates must be ISO-8601 e.g. 2026-04-15T10:00:00
    public Map<String, Object> importBookings(MultipartFile file) throws Exception {
        List<BookingDTO> imported = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            int rowNum = 0;

            while ((line = reader.readLine()) != null) {
                rowNum++;
                if (rowNum == 1) continue; // skip header row
                if (line.trim().isEmpty()) continue;

                try {
                    String[] p = line.split(",", -1);
                    if (p.length < 11) {
                        errors.add("Row " + rowNum + ": expected at least 11 columns, got " + p.length);
                        continue;
                    }

                    BookingDTO dto = new BookingDTO();

                    ShipperDTO shipperRef = new ShipperDTO();
                    shipperRef.setShipperID(Long.parseLong(p[0].trim()));
                    dto.setShipper(shipperRef);

                    dto.setOriginSiteID(Long.parseLong(p[1].trim()));
                    dto.setDestinationSiteID(Long.parseLong(p[2].trim()));
                    dto.setPickupWindowStart(LocalDateTime.parse(p[3].trim()));
                    dto.setPickupWindowEnd(LocalDateTime.parse(p[4].trim()));
                    if (!p[5].trim().isEmpty()) dto.setDeliveryWindowStart(LocalDateTime.parse(p[5].trim()));
                    if (!p[6].trim().isEmpty()) dto.setDeliveryWindowEnd(LocalDateTime.parse(p[6].trim()));
                    dto.setWeightKg(Double.parseDouble(p[7].trim()));
                    dto.setVolumeM3(Double.parseDouble(p[8].trim()));
                    dto.setPieces(Integer.parseInt(p[9].trim()));
                    dto.setCommodity(p[10].trim());
                    if (p.length > 11 && !p[11].trim().isEmpty()) dto.setSpecialHandlingFlags(p[11].trim());
                    dto.setStatus(BookingStatus.SUBMITTED);

                    imported.add(createBooking(dto));

                } catch (Exception e) {
                    errors.add("Row " + rowNum + ": " + e.getMessage());
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("imported", imported.size());
        result.put("failed", errors.size());
        result.put("errors", errors);
        return result;
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