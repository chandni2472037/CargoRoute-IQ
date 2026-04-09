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
import com.example.demo.exception.HandoverNotFoundException;
import com.example.demo.exception.InvalidDataException;
import com.example.demo.repository.HandoverRepository;
import com.example.demo.service.HandoverService;

@Service
public class HandoverServiceImpl implements HandoverService {

    @Autowired
    private HandoverRepository handoverRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String MANIFEST_SERVICE_URL =
            "http://MANIFEST-SERVICE/CargoRoute/manifests/getByManifestId/";

    /* ================= ENTITY → DTO ================= */
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

    /* ================= DTO → ENTITY ================= */
    private Handover convertToEntity(HandoverDTO dto) {

        if (dto == null || dto.getManifestID() == null) {
            throw new InvalidDataException("Manifest ID is required");
        }

        // Detached manifest reference (NO repository call)
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

    /* ================= BUILD RESPONSE ================= */
    private HandoverResponseDTO buildResponse(Handover handover) {

        HandoverDTO handoverDTO = convertToDTO(handover);

        ManifestRequiredResponseDTO manifestDetails =
                restTemplate.getForObject(
                        MANIFEST_SERVICE_URL + handoverDTO.getManifestID(),
                        ManifestRequiredResponseDTO.class
                );

        HandoverResponseDTO response = new HandoverResponseDTO();
        response.setHandover(handoverDTO);
        response.setManifestDetails(manifestDetails);

        return response;
    }

    /* ================= CREATE ================= */
    @Override
    public HandoverDTO create(HandoverDTO handoverDTO) {
        return convertToDTO(
                handoverRepository.save(convertToEntity(handoverDTO))
        );
    }

    /* ================= GET BY ID ================= */
    @Override
    public HandoverResponseDTO getById(Long handoverID) {

        Handover handover = handoverRepository.findById(handoverID)
                .orElseThrow(() ->
                        new HandoverNotFoundException(
                                "Handover not found with ID: " + handoverID));

        return buildResponse(handover);
    }

    /* ================= GET ALL ================= */
    @Override
    public List<HandoverResponseDTO> getAll() {

        List<Handover> handovers = handoverRepository.findAll();
        List<HandoverResponseDTO> responses = new ArrayList<>();

        for (Handover handover : handovers) {
            responses.add(buildResponse(handover));
        }

        return responses; // ✅ empty list if none
    }

    /* ================= GET BY MANIFEST ID ================= */
    @Override
    public HandoverResponseDTO getByManifestID(Long manifestID) {

        Handover handover =
                handoverRepository.findByManifest_ManifestID(manifestID);

        if (handover == null) {
            throw new HandoverNotFoundException(
                    "Handover not found for Manifest ID: " + manifestID);
        }

        return buildResponse(handover);
    }

    /* ================= GET BY HANDED BY ================= */
    @Override
    public List<HandoverResponseDTO> getByHandedBy(String handedBy) {

        List<Handover> handovers =
                handoverRepository.findByHandedBy(handedBy);

        List<HandoverResponseDTO> responses = new ArrayList<>();

        for (Handover handover : handovers) {
            responses.add(buildResponse(handover));
        }

        return responses;
    }

    /* ================= UPDATE ================= */
    @Override
    public HandoverDTO update(Long handoverID, HandoverDTO handoverDTO) {

        Handover existing = handoverRepository.findById(handoverID)
                .orElseThrow(() ->
                        new HandoverNotFoundException(
                                "Handover not found with ID: " + handoverID));

        // Update only provided fields
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

        return convertToDTO(
                handoverRepository.save(existing));
    }

    /* ================= DELETE ================= */
    @Override
    public void delete(Long handoverID) {

        Handover handover =
                handoverRepository.findById(handoverID)
                        .orElseThrow(() ->
                                new HandoverNotFoundException(
                                        "Handover not found with ID: " + handoverID));

        handoverRepository.delete(handover);
    }
}