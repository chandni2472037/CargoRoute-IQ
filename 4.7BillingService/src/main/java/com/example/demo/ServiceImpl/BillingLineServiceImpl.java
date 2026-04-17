package com.example.demo.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.BillingLineDTO;
import com.example.demo.dto.BillingLineResponseDTO;
import com.example.demo.dto.BookingDTO;
import com.example.demo.dto.RequiredResponseDTO;
import com.example.demo.entity.BillingLine;
import com.example.demo.exception.BillingLineNotFoundException;
import com.example.demo.repository.BillingLineRepository;
import com.example.demo.service.BillingLineService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import jakarta.transaction.Transactional;

@Service
public class BillingLineServiceImpl implements BillingLineService {

    private final BillingLineRepository repository;
    private final RestTemplate restTemplate;

    public BillingLineServiceImpl(
            BillingLineRepository repository,
            RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    // ================= CREATE =================
    @Override
    public BillingLineResponseDTO create(BillingLineDTO dto) {

        if (dto.getBookingID() == null) {
            throw new IllegalArgumentException("Booking ID is required");
        }
        if (dto.getLoadID() == null) {
            throw new IllegalArgumentException("Load ID is required");
        }
        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (dto.getTariffApplied() == null || dto.getTariffApplied().isBlank()) {
            throw new IllegalArgumentException("Tariff must be specified");
        }

        BillingLine saved = repository.save(toEntity(dto));
        return buildResponse(saved);
    }

    // ================= GET BY ID =================
    @Override
    public BillingLineResponseDTO getById(Long id) {

        BillingLine billingLine = repository.findById(id)
                .orElseThrow(() ->
                        new BillingLineNotFoundException(
                                "BillingLine with given ID not found"));

        return buildResponse(billingLine);
    }

    // ================= GET ALL =================
    @Override
    public List<BillingLineResponseDTO> getAll() {

        return repository.findAll()
                .stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }

    // ================= UPDATE =================
    @Transactional
    @Override
    public BillingLineResponseDTO update(Long id, BillingLineDTO dto) {

        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (dto.getTariffApplied() == null || dto.getTariffApplied().isBlank()) {
            throw new IllegalArgumentException("Tariff must be specified");
        }

        BillingLine billingLine = repository.findById(id)
                .orElseThrow(() ->
                        new BillingLineNotFoundException(
                                "BillingLine with given ID not found"));

        billingLine.setAmount(dto.getAmount());
        billingLine.setTariffApplied(dto.getTariffApplied());
        billingLine.setNotes(dto.getNotes());

        // ✅ Force DB update
        repository.saveAndFlush(billingLine);

        return buildResponse(billingLine);
    }

    // ================= DELETE =================
    @Override
    public void delete(Long id) {

        if (!repository.existsById(id)) {
            throw new BillingLineNotFoundException(
                    "BillingLine with given ID not found");
        }
        repository.deleteById(id);
    }

    // ================= RESPONSE BUILDER =================
    private BillingLineResponseDTO buildResponse(BillingLine billingLine) {

        BookingDTO booking = getBookingWithCB(
                billingLine.getBookingID());

        RequiredResponseDTO load = getLoadWithCB(
                billingLine.getLoadID());

        BillingLineResponseDTO response = new BillingLineResponseDTO();
        response.setBilling(toDTO(billingLine));
        response.setBooking(booking);
        response.setLoad(load);

        return response;
    }

    // ================= BOOKING SERVICE CALL =================
    // ✅ Matches your BookingController:
    // GET /cargoRoute/booking/getBooking/{id}
    @CircuitBreaker(
            name = "bookingService",
            fallbackMethod = "bookingFallback")
    public BookingDTO getBookingWithCB(Long bookingID) {

        return restTemplate.getForObject(
                "http://BOOKING-SERVICE/cargoRoute/booking/getBooking/" + bookingID,
                BookingDTO.class
        );
    }

    // ================= LOAD SERVICE CALL =================
    @CircuitBreaker(
            name = "loadService",
            fallbackMethod = "loadFallback")
    public RequiredResponseDTO getLoadWithCB(Long loadID) {

        return restTemplate.getForObject(
                "http://ROUTING-SERVICE/loads/" + loadID,
                RequiredResponseDTO.class
        );
    }

    // ================= FALLBACK METHODS =================
    public BookingDTO bookingFallback(Long bookingID, Throwable ex) {
        BookingDTO dto = new BookingDTO();
        dto.setBookingID(bookingID);
        return dto;
    }

    public RequiredResponseDTO loadFallback(Long loadID, Throwable ex) {
        return new RequiredResponseDTO();
    }

    // ================= ENTITY ↔ DTO =================
    private BillingLine toEntity(BillingLineDTO dto) {

        BillingLine entity = new BillingLine();
        entity.setBookingID(dto.getBookingID());
        entity.setLoadID(dto.getLoadID());
        entity.setAmount(dto.getAmount());
        entity.setTariffApplied(dto.getTariffApplied());
        entity.setNotes(dto.getNotes());
        return entity;
    }

    private BillingLineDTO toDTO(BillingLine entity) {

        BillingLineDTO dto = new BillingLineDTO();
        dto.setBillingLineID(entity.getBillingLineID());
        dto.setBookingID(entity.getBookingID());
        dto.setLoadID(entity.getLoadID());
        dto.setAmount(entity.getAmount());
        dto.setTariffApplied(entity.getTariffApplied());
        dto.setNotes(entity.getNotes());
        return dto;
    }
}