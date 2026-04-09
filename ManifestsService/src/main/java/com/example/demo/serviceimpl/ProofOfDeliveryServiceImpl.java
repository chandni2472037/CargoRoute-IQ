package com.example.demo.serviceimpl;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.BookingDTO;
import com.example.demo.dto.ProofOfDeliveryDTO;
import com.example.demo.dto.ProofOfDeliveryResponseDTO;
import com.example.demo.entities.ProofOfDelivery;
import com.example.demo.entities.enums.PodType;
import com.example.demo.entities.enums.ProofOfDeliveryStatus;
import com.example.demo.exception.ProofOfDeliveryNotFoundException;
import com.example.demo.repository.ProofOfDeliveryRepository;
import com.example.demo.service.ProofOfDeliveryService;

@Service
public class ProofOfDeliveryServiceImpl implements ProofOfDeliveryService {

    @Autowired
    private ProofOfDeliveryRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String BOOKING_SERVICE_URL =
            "http://BOOKING-SERVICE/bookings/";

    /* ================= CREATE ================= */

    @Override
    public ProofOfDeliveryDTO create(ProofOfDeliveryDTO dto) {

        if (dto.getBookingID() == null) {
            throw new IllegalArgumentException("bookingID is mandatory");
        }

        ProofOfDelivery pod = new ProofOfDelivery();
        pod.setBookingID(dto.getBookingID());
        pod.setDeliveredAt(dto.getDeliveredAt());
        pod.setReceivedBy(dto.getReceivedBy());
        pod.setPodURI(dto.getPodURI());
        pod.setPodType(dto.getPodType());
        pod.setStatus(dto.getStatus());

        ProofOfDelivery saved = repository.save(pod);
        return convertToDTO(saved);
    }

    /* ================= FETCH ================= */

    @Override
    public ProofOfDeliveryResponseDTO getById(Long podID) {
        return buildResponse(findPod(podID));
    }

    @Override
    public List<ProofOfDeliveryResponseDTO> getAll() {

        List<ProofOfDeliveryResponseDTO> responses = new ArrayList<>();
        for (ProofOfDelivery pod : repository.findAll()) {
            responses.add(buildResponse(pod));
        }
        return responses;
    }

    @Override
    public ProofOfDeliveryResponseDTO getByBookingID(Long bookingID) {

        ProofOfDelivery pod = repository.findByBookingID(bookingID);

        if (pod == null) {
            throw new ProofOfDeliveryNotFoundException(
                    "POD not found for bookingID: " + bookingID);
        }
        return buildResponse(pod);
    }

    @Override
    public List<ProofOfDeliveryResponseDTO> getByPodType(PodType podType) {

        List<ProofOfDeliveryResponseDTO> responses = new ArrayList<>();
        for (ProofOfDelivery pod : repository.findByPodType(podType)) {
            responses.add(buildResponse(pod));
        }
        return responses;
    }

    @Override
    public List<ProofOfDeliveryResponseDTO> getByProofOfDeliveryStatus(
            ProofOfDeliveryStatus status) {

        List<ProofOfDeliveryResponseDTO> responses = new ArrayList<>();
        for (ProofOfDelivery pod : repository.findByStatus(status)) {
            responses.add(buildResponse(pod));
        }
        return responses;
    }

    /* ================= UPDATE ================= */

    @Override
    public ProofOfDeliveryDTO update(Long podID, ProofOfDeliveryDTO dto) {

        ProofOfDelivery pod = findPod(podID);

        if (dto.getDeliveredAt() != null)
            pod.setDeliveredAt(dto.getDeliveredAt());

        if (dto.getReceivedBy() != null)
            pod.setReceivedBy(dto.getReceivedBy());

        if (dto.getPodURI() != null)
            pod.setPodURI(dto.getPodURI());

        if (dto.getPodType() != null)
            pod.setPodType(dto.getPodType());

        if (dto.getStatus() != null)
            pod.setStatus(dto.getStatus());

        return convertToDTO(repository.save(pod));
    }

    /* ================= DELETE ================= */

    @Override
    public void delete(Long podID) {
        repository.delete(findPod(podID));
    }

    /* ================= HELPERS ================= */

    private ProofOfDelivery findPod(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ProofOfDeliveryNotFoundException(
                                "POD not found with ID: " + id));
    }

    private ProofOfDeliveryDTO convertToDTO(ProofOfDelivery pod) {

        ProofOfDeliveryDTO dto = new ProofOfDeliveryDTO();
        dto.setPodID(pod.getPodID());
        dto.setBookingID(pod.getBookingID());
        dto.setDeliveredAt(pod.getDeliveredAt());
        dto.setReceivedBy(pod.getReceivedBy());
        dto.setPodURI(pod.getPodURI());
        dto.setPodType(pod.getPodType());
        dto.setStatus(pod.getStatus());
        return dto;
    }

    private ProofOfDeliveryResponseDTO buildResponse(ProofOfDelivery pod) {

        ProofOfDeliveryResponseDTO response = new ProofOfDeliveryResponseDTO();
        response.setPodDto(convertToDTO(pod));

        BookingDTO booking =
                restTemplate.getForObject(
                        BOOKING_SERVICE_URL + pod.getBookingID(),
                        BookingDTO.class);

        response.setBooking(booking);
        return response;
    }
}

