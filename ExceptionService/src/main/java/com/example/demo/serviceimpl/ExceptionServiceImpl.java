package com.example.demo.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.BookingDetailsDTO;
import com.example.demo.dto.ExceptionRecordDTO;
import com.example.demo.dto.RequiredResponseDTO;
import com.example.demo.entity.ExceptionRecord;
import com.example.demo.entity.enums.ExceptionStatus;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ExceptionRepository;
import com.example.demo.service.ExceptionService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class ExceptionServiceImpl implements ExceptionService {

    private static final String EXCEPTION_SERVICE_CB = "bookingService";

    @Autowired
    private ExceptionRepository repo;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public ExceptionRecordDTO createException(ExceptionRecordDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Exception request body must not be null");
        }
        if (dto.getBookingId() == null) {
            throw new BadRequestException("Booking ID is required");
        }
        if (dto.getType() == null) {
            throw new BadRequestException("Exception type is required. Valid values are: DELAY, DAMAGE, MISSING");
        }
      
        if (dto.getStatus() == null) {
            dto.setStatus(com.example.demo.entity.enums.ExceptionStatus.PENDING);
        }

        // Verify the booking exists in the Booking Service before saving
        try {
            BookingDetailsDTO booking = restTemplate.getForObject(
                    "http://BOOKING-SERVICE/cargoRoute/booking/getBookingById/{id}",
                    BookingDetailsDTO.class,
                    dto.getBookingId()
            );
            if (booking == null) {
                throw new BadRequestException("Booking with ID " + dto.getBookingId() + " does not exist.");
            }
        } catch (HttpClientErrorException.NotFound ex) {
            throw new BadRequestException("Booking with ID " + dto.getBookingId() + " does not exist.");
        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BadRequestException("Could not verify Booking ID " + dto.getBookingId() + ". Please check the ID and try again.");
        }

        ExceptionRecord exception = dtoToEntity(dto);
        ExceptionRecord saved = repo.save(exception);
        return entityToDto(saved);
    }

    @Override
    @CircuitBreaker(name = EXCEPTION_SERVICE_CB, fallbackMethod = "getAllExceptionsFallback")
    public List<RequiredResponseDTO> getAllExceptions() {
        return repo.findAll()
                .stream()
                .map(e -> getExceptionById(e.getExceptionID()))
                .collect(Collectors.toList());
    }

    // Fallback method
    public List<RequiredResponseDTO> getAllExceptionsFallback(Throwable t) {
        return repo.findAll().stream()
                .map(e -> {
                    RequiredResponseDTO fallback = new RequiredResponseDTO();
                    fallback.setExceptiondto(entityToDto(e));
                    fallback.setBookingdto(null);
                    return fallback;
                })
                .collect(Collectors.toList());
    }

    @Override
    @CircuitBreaker(name = EXCEPTION_SERVICE_CB, fallbackMethod = "getExceptionByIdFallback")
    public RequiredResponseDTO getExceptionById(Long id) {
        ExceptionRecord exception = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exception with ID " + id + " not found"));

        RequiredResponseDTO response = new RequiredResponseDTO();
        response.setExceptiondto(entityToDto(exception));

        if (exception.getBookingId() != null) {
            try {
                BookingDetailsDTO bookingDto = restTemplate.getForObject(
                        "http://BOOKING-SERVICE/cargoRoute/booking/getBookingById/{id}",
                        BookingDetailsDTO.class,
                        exception.getBookingId()
                );
                response.setBookingdto(bookingDto);
            } catch (Exception e) {
                // Booking may no longer exist or service is unavailable — return exception without booking details
                response.setBookingdto(null);
            }
        }

        return response;
    }

    // Fallback method
    public RequiredResponseDTO getExceptionByIdFallback(Long id, Throwable t) {
        ExceptionRecord exception = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exception with ID " + id + " not found"));
        RequiredResponseDTO fallback = new RequiredResponseDTO();
        fallback.setExceptiondto(entityToDto(exception));
        fallback.setBookingdto(null);
        return fallback;
    }

    @Override
    public ExceptionRecordDTO updateExceptionStatus(Long id, ExceptionStatus status) {
        ExceptionRecord exception = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exception with ID " + id + " not found"));
        exception.setStatus(status);
        return entityToDto(repo.save(exception));
    }

    @Override
    public List<RequiredResponseDTO> getExceptionByBookingId(Long bookingId) {
        List<ExceptionRecord> exceptions = repo.findByBookingId(bookingId);
        if (exceptions.isEmpty()) {
            throw new ResourceNotFoundException("No exceptions found for booking ID: " + bookingId);
        }
        return exceptions.stream()
                .map(e -> getExceptionById(e.getExceptionID()))
                .collect(Collectors.toList());
    }

    @Override
    public List<RequiredResponseDTO> getExceptionByStatus(ExceptionStatus status) {
        return repo.findByStatus(status).stream()
                .map(e -> getExceptionById(e.getExceptionID()))
                .collect(Collectors.toList());
    }

    // Mapping methods
    private ExceptionRecordDTO entityToDto(ExceptionRecord exception) {
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

    private ExceptionRecord dtoToEntity(ExceptionRecordDTO dto) {
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
}