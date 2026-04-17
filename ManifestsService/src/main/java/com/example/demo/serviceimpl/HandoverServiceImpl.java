package com.example.demo.serviceimpl;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.HandoverDTO;
import com.example.demo.dto.HandoverResponseDTO;
import com.example.demo.dto.ManifestRequiredResponseDTO;
import com.example.demo.entities.Handover;
import com.example.demo.entities.Manifest;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.HandoverRepository;
import com.example.demo.service.HandoverService;

@Service
public class HandoverServiceImpl implements HandoverService {

    @Autowired
    private HandoverRepository handoverRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String MANIFEST_SERVICE_URL =
            "http://MANIFEST-SERVICE/cargoRoute/manifests/getByManifestId/";

    // -------------------- Entity to DTO --------------------
    private HandoverDTO convertToDTO(Handover entity) {

        HandoverDTO dto = new HandoverDTO();
        dto.setHandoverID(entity.getHandoverID());
        dto.setManifestID(entity.getManifest().getManifestID());
        dto.setHandedBy(entity.getHandedBy());
        dto.setHandedAt(entity.getHandedAt());
        dto.setReceivedBy(entity.getReceivedBy());
        dto.setReceivedAt(entity.getReceivedAt());
        dto.setNotes(entity.getNotes());

        return dto;
    }

    // -------------------- DTO to Entity --------------------
    private Handover convertToEntity(HandoverDTO dto) {

        if (dto == null || dto.getManifestID() == null) {
            throw new BadRequestException("Manifest ID is required");
        }

        Manifest manifest = new Manifest();
        manifest.setManifestID(dto.getManifestID());

        Handover entity = new Handover();
        entity.setManifest(manifest);
        entity.setHandedBy(dto.getHandedBy());
        entity.setHandedAt(dto.getHandedAt());
        entity.setReceivedBy(dto.getReceivedBy());
        entity.setReceivedAt(dto.getReceivedAt());
        entity.setNotes(dto.getNotes());

        return entity;
    }

    // -------------------- SAME SERVICE call (no CircuitBreaker) --------------------
    private ManifestRequiredResponseDTO callManifestService(Long manifestId) {
        return restTemplate.getForObject(
                MANIFEST_SERVICE_URL + manifestId,
                ManifestRequiredResponseDTO.class
        );
    }

    // -------------------- Response Builder --------------------
    private HandoverResponseDTO buildResponse(Handover handover) {

        HandoverResponseDTO response = new HandoverResponseDTO();
        response.setHandover(convertToDTO(handover));
        response.setManifestDetails(
                callManifestService(handover.getManifest().getManifestID())
        );

        return response;
    }

    // -------------------- CREATE --------------------
    @Override
    public HandoverDTO create(HandoverDTO handoverDTO) {
        return convertToDTO(
                handoverRepository.save(convertToEntity(handoverDTO))
        );
    }

    // -------------------- GET BY ID --------------------
    @Override
    public HandoverResponseDTO getById(Long handoverID) {

        Handover handover = handoverRepository.findById(handoverID)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Handover not found with ID: " + handoverID));

        return buildResponse(handover);
    }

    // -------------------- GET ALL --------------------
    @Override
    public List<HandoverResponseDTO> getAll() {

        List<Handover> handovers = handoverRepository.findAll();
        List<HandoverResponseDTO> responses = new ArrayList<>();

        for (Handover handover : handovers) {
            responses.add(buildResponse(handover));
        }
        return responses;
    }

    // -------------------- GET BY MANIFEST ID --------------------
    @Override
    public List<HandoverResponseDTO> getByManifestID(Long manifestID) {

        List<Handover> handovers =
                handoverRepository.findByManifest_ManifestID(manifestID);

        if (handovers.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Handover not found for Manifest ID: " + manifestID);
        }

        List<HandoverResponseDTO> responses = new ArrayList<>();
        for (Handover handover : handovers) {
            responses.add(buildResponse(handover));
        }

        return responses;
    }

    // -------------------- GET BY HANDED BY --------------------
    @Override
    public List<HandoverResponseDTO> getByHandedBy(String handedBy) {

        List<Handover> handovers =
                handoverRepository.findByHandedBy(handedBy);

        if (handovers.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Handover not found for handedBy: " + handedBy);
        }

        List<HandoverResponseDTO> responses = new ArrayList<>();
        for (Handover handover : handovers) {
            responses.add(buildResponse(handover));
        }

        return responses;
    }

    // -------------------- UPDATE --------------------
    @Override
    public HandoverDTO update(Long handoverID, HandoverDTO handoverDTO) {

        Handover existing = handoverRepository.findById(handoverID)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Handover not found with ID: " + handoverID));

        if (handoverDTO.getHandedBy() != null)
            existing.setHandedBy(handoverDTO.getHandedBy());

        if (handoverDTO.getHandedAt() != null)
            existing.setHandedAt(handoverDTO.getHandedAt());

        if (handoverDTO.getReceivedBy() != null)
            existing.setReceivedBy(handoverDTO.getReceivedBy());

        if (handoverDTO.getReceivedAt() != null)
            existing.setReceivedAt(handoverDTO.getReceivedAt());

        if (handoverDTO.getNotes() != null)
            existing.setNotes(handoverDTO.getNotes());

        return convertToDTO(handoverRepository.save(existing));
    }

    // -------------------- DELETE --------------------
    @Override
    public void delete(Long handoverID) {

        Handover handover = handoverRepository.findById(handoverID)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Handover not found with ID: " + handoverID));

        handoverRepository.delete(handover);
    }
}