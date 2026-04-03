package com.example.demo.serviceimpl;

import java.util.List;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.BookingDetailsDTO;
import com.example.demo.dto.ExceptionRecordDTO;
import com.example.demo.dto.RequiredResponseDTO;
import com.example.demo.entity.ExceptionRecord;
import com.example.demo.entity.enums.ExceptionStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ExceptionRepository;
import com.example.demo.service.ExceptionService;

@Service // Marks this class as a Spring-managed service component
public class ExceptionServiceImpl implements ExceptionService {

    @Autowired
    private ExceptionRepository repo; // Injects the ExceptionRepository for DB operations

    @Autowired
    private RestTemplate restTemplate;
    
    

    // Save a new exception record or update an existing one
    public ExceptionRecordDTO createException(ExceptionRecordDTO dto){
        if (dto.getBookingId() == null) {
            throw new com.example.demo.exception.BadRequestException("Booking ID is required");
        }
        ExceptionRecord exception = convertToEntity(dto);
        ExceptionRecord saved = repo.save(exception);
        return convertToDTO(saved);
    }

    // Retrieve all ExceptionRecord entries from the database
    public List<RequiredResponseDTO> getAllExceptions() {
        return repo.findAll().stream()
                .map(this::convertToRequiredResponse)
                .collect(Collectors.toList());
    }

    // Retrieve a single ExceptionRecord by its ID with booking details
    public RequiredResponseDTO getExceptionById(Long id) {
        ExceptionRecord exception = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exception with ID " + id + " not found"));
        return convertToRequiredResponse(exception);
    }

   
    
    // Update only the status of an ExceptionRecord
    public ExceptionRecordDTO updateExceptionStatus(Long id, ExceptionStatus status) {
        ExceptionRecord exception = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exception with ID " + id + " not found"));
        exception.setStatus(status);
        ExceptionRecord updated = repo.save(exception);
        return convertToDTO(updated);
    }

    // Retrieve exceptions by booking ID with booking details
    public List<RequiredResponseDTO> getExceptionByBookingId(Long bookingId) {
        List<ExceptionRecord> exceptions = repo.findByBookingId(bookingId);

        if (exceptions.isEmpty()) {
            throw new ResourceNotFoundException("No exceptions found for booking ID: " + bookingId);
        }

        return exceptions.stream()
                .map(this::convertToRequiredResponse)
                .collect(Collectors.toList());
    }
    

    // Retrieve exceptions by status
    public List<RequiredResponseDTO> getExceptionByStatus(ExceptionStatus status) {
        return repo.findByStatus(status).stream().map(this::convertToRequiredResponse).collect(Collectors.toList());
    }

    // Convert Entity to DTO with booking details fetched from BookingService
    private ExceptionRecordDTO convertToDTO(ExceptionRecord exception) {
    	ExceptionRecordDTO dto = new ExceptionRecordDTO();
        dto.setExceptionID(exception.getExceptionID());
        dto.setType(exception.getType());
        dto.setReportedBy(exception.getReportedBy());
        dto.setReportedAt(exception.getReportedAt());
        dto.setUpdatedAt(exception.getUpdatedAt());
        dto.setDescription(exception.getDescription());
        dto.setStatus(exception.getStatus());
        dto.setBookingId(exception.getBookingId());
        return dto;
        }

      

    // Convert DTO to Entity
    private ExceptionRecord convertToEntity(ExceptionRecordDTO dto) {
    	ExceptionRecord exception = new ExceptionRecord();
        exception.setExceptionID(dto.getExceptionID());
        exception.setType(dto.getType());
        exception.setReportedBy(dto.getReportedBy());
        exception.setReportedAt(dto.getReportedAt());
        exception.setDescription(dto.getDescription());
        exception.setStatus(dto.getStatus());
        exception.setBookingId(dto.getBookingId());
        return exception;
    
    }
    
    private RequiredResponseDTO convertToRequiredResponse(ExceptionRecord exception) {
RequiredResponseDTO response = new RequiredResponseDTO();
        
        // 1. Map Entity to local DTO
        ExceptionRecordDTO exceptionDto = new ExceptionRecordDTO();
        exceptionDto.setExceptionID(exception.getExceptionID());
        exceptionDto.setType(exception.getType());
        exceptionDto.setReportedBy(exception.getReportedBy());
        exceptionDto.setReportedAt(exception.getReportedAt());
        exceptionDto.setUpdatedAt(exception.getUpdatedAt());
        exceptionDto.setDescription(exception.getDescription());
        exceptionDto.setStatus(exception.getStatus());
        exceptionDto.setBookingId(exception.getBookingId());

        // 2. Fetch Booking Details from external service
        BookingDetailsDTO bookingDto = null;
        if (exception.getBookingId() != null) {
            try {
                bookingDto = restTemplate.getForObject(
                        "http://localhost:7070/bookings/{id}",
                        BookingDetailsDTO.class,
                        exception.getBookingId()
                );
            } catch (Exception e) {
                // Log the error but allow the exception record to be returned without booking info
                System.err.println("Booking Service unavailable for ID " + exception.getBookingId() + ": " + e.getMessage());
            }
        }

        // 3. Populate and return the wrapper
        response.setExceptiondto(exceptionDto);
        response.setBookingdto(bookingDto);
        
        return response;
    }
}