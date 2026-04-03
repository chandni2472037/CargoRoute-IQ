package com.example.demo.serviceimpl;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.BookingDTO;
import com.example.demo.dto.ProofOfDeliveryDTO;
import com.example.demo.dto.RequiredResponseDTO;
import com.example.demo.entities.ProofOfDelivery;
import com.example.demo.entities.enums.PodType;
import com.example.demo.entities.enums.ProofOfDeliveryStatus;
import com.example.demo.exception.InvalidDataException;
import com.example.demo.exception.ProofOfDeliveryNotFoundException;
import com.example.demo.repository.ProofOfDeliveryRepository;
import com.example.demo.service.ProofOfDeliveryService;

@Service
public class ProofOfDeliveryServiceImpl implements ProofOfDeliveryService {

    @Autowired
    private ProofOfDeliveryRepository proofOfDeliveryRepository;

    @Autowired
    private RestTemplate restTemplate;

    /* ================= Entity → DTO ================= */
    private ProofOfDeliveryDTO convertToDto(ProofOfDelivery entity) {

        ProofOfDeliveryDTO dto = new ProofOfDeliveryDTO();
        dto.setPodID(entity.getPodID());

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setBookingID(entity.getBookingID());
        dto.setBookingID(bookingDTO);

        dto.setDeliveredAt(entity.getDeliveredAt());
        dto.setReceivedBy(entity.getReceivedBy());
        dto.setPodURI(entity.getPodURI());
        dto.setPodType(entity.getPodType());
        dto.setStatus(entity.getStatus());

        return dto;
    }

    /* ================= DTO → Entity ================= */
    private ProofOfDelivery convertToEntity(ProofOfDeliveryDTO dto) {

        ProofOfDelivery entity = new ProofOfDelivery();

        entity.setBookingID(
                dto.getBookingID().getBookingID());
        entity.setDeliveredAt(dto.getDeliveredAt());
        entity.setReceivedBy(dto.getReceivedBy());
        entity.setPodURI(dto.getPodURI());
        entity.setPodType(dto.getPodType());
        entity.setStatus(dto.getStatus());

        return entity;
    }

    /* ================= FETCH BOOKING (REST) ================= */
    private RequiredResponseDTO buildResponse(ProofOfDelivery pod) {

        RequiredResponseDTO response = new RequiredResponseDTO();
        response.setPodDto(convertToDto(pod));

        BookingDTO booking =
                restTemplate.getForObject(
                        "http://BOOKING-SERVICE/bookings/" + pod.getBookingID(),
                        BookingDTO.class);

        response.setBooking(booking);
        return response;
    }

    /* ================= CREATE ================= */
    @Override
    public ProofOfDeliveryDTO create(ProofOfDeliveryDTO dto) {

        if (dto == null ||
            dto.getBookingID() == null ||
            dto.getBookingID().getBookingID() == null) {
            throw new InvalidDataException("Booking details are required");
        }

        ProofOfDelivery saved =
                proofOfDeliveryRepository.save(convertToEntity(dto));

        return convertToDto(saved);
    }

    /* ================= GET BY ID ================= */
    @Override
    public RequiredResponseDTO getById(Long podID) {

        ProofOfDelivery pod =
                proofOfDeliveryRepository.findById(podID)
                        .orElseThrow(() ->
                                new ProofOfDeliveryNotFoundException(
                                        "Proof Of Delivery not found with ID: " + podID));

        return buildResponse(pod);
    }

    /* ================= GET ALL ================= */
    @Override
    public List<RequiredResponseDTO> getAll() {

        List<ProofOfDelivery> pods =
                proofOfDeliveryRepository.findAll();

        if (pods.isEmpty()) {
            throw new ProofOfDeliveryNotFoundException(
                    "No Proof Of Delivery records found");
        }

        List<RequiredResponseDTO> responseList = new ArrayList<>();

        for (ProofOfDelivery pod : pods) {
            responseList.add(buildResponse(pod));
        }

        return responseList;
    }

    /* ================= GET BY BOOKING ID ================= */
    @Override
    public RequiredResponseDTO getByBookingID(Long bookingID) {

        ProofOfDelivery pod =
                proofOfDeliveryRepository.findByBookingID(bookingID);

        if (pod == null) {
            throw new ProofOfDeliveryNotFoundException(
                    "No Proof Of Delivery found for Booking ID: " + bookingID);
        }

        return buildResponse(pod);
    }

    /* ================= GET BY POD TYPE ================= */
    @Override
    public List<RequiredResponseDTO> getByPodType(PodType podType) {

        List<ProofOfDelivery> pods =
                proofOfDeliveryRepository.findByPodType(podType);

        List<RequiredResponseDTO> responseList = new ArrayList<>();

        for (ProofOfDelivery pod : pods) {
            responseList.add(buildResponse(pod));
        }

        return responseList;
    }

    /* ================= GET BY STATUS ================= */
    @Override
    public List<RequiredResponseDTO> getByProofOfDeliveryStatus(
            ProofOfDeliveryStatus status) {

        List<ProofOfDelivery> pods =
                proofOfDeliveryRepository.findByStatus(status);

        List<RequiredResponseDTO> responseList = new ArrayList<>();

        for (ProofOfDelivery pod : pods) {
            responseList.add(buildResponse(pod));
        }

        return responseList;
    }

    /* ================= UPDATE ================= */
    @Override
    public ProofOfDeliveryDTO update(Long podID, ProofOfDeliveryDTO dto) {

        ProofOfDelivery existing =
                proofOfDeliveryRepository.findById(podID)
                        .orElseThrow(() ->
                                new ProofOfDeliveryNotFoundException(
                                        "Proof Of Delivery not found"));

        if (dto.getDeliveredAt() != null)
            existing.setDeliveredAt(dto.getDeliveredAt());
        if (dto.getReceivedBy() != null)
            existing.setReceivedBy(dto.getReceivedBy());
        if (dto.getPodURI() != null)
            existing.setPodURI(dto.getPodURI());
        if (dto.getPodType() != null)
            existing.setPodType(dto.getPodType());
        if (dto.getStatus() != null)
            existing.setStatus(dto.getStatus());

        return convertToDto(
                proofOfDeliveryRepository.save(existing));
    }

    /* ================= DELETE ================= */
    @Override
    public void delete(Long podID) {

        ProofOfDelivery pod =
                proofOfDeliveryRepository.findById(podID)
                        .orElseThrow(() ->
                                new ProofOfDeliveryNotFoundException(
                                        "Proof Of Delivery not found with ID: " + podID));

        proofOfDeliveryRepository.delete(pod);
    }
}