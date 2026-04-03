package com.example.demo.serviceimpl;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.HandoverDTO;
import com.example.demo.dto.ManifestDTO;
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

    /* ================= Entity → DTO ================= */
    private HandoverDTO convertToDto(Handover entity) {

        HandoverDTO dto = new HandoverDTO();
        dto.setHandoverID(entity.getHandoverID());

        ManifestDTO manifestDTO = new ManifestDTO();
        manifestDTO.setManifestID(
                entity.getManifest().getManifestID());

        dto.setManifest(manifestDTO);
        dto.setHandedBy(entity.getHandedBy());
        dto.setHandedAt(entity.getHandedAt());
        dto.setReceivedBy(entity.getReceivedBy());
        dto.setReceivedAt(entity.getReceivedAt());
        dto.setNotes(entity.getNotes());

        return dto;
    }

    /* ================= DTO → Entity ================= */
    private Handover convertToEntity(HandoverDTO dto) {

        Handover entity = new Handover();

        Manifest manifest = new Manifest();
        manifest.setManifestID(
                dto.getManifest().getManifestID());

        entity.setManifest(manifest);
        entity.setHandedBy(dto.getHandedBy());
        entity.setHandedAt(dto.getHandedAt());
        entity.setReceivedBy(dto.getReceivedBy());
        entity.setReceivedAt(dto.getReceivedAt());
        entity.setNotes(dto.getNotes());

        return entity;
    }

    /* ================= CREATE ================= */
    @Override
    public HandoverDTO create(HandoverDTO dto) {

        if (dto == null ||
            dto.getManifest() == null ||
            dto.getManifest().getManifestID() == null) {
            throw new InvalidDataException("Manifest details are required");
        }

        Handover saved =
                handoverRepository.save(convertToEntity(dto));

        return convertToDto(saved);
    }

    /* ================= GET BY ID ================= */
    @Override
    public HandoverDTO getById(Long handoverID) {

        Handover handover =
                handoverRepository.findById(handoverID)
                        .orElseThrow(() ->
                                new HandoverNotFoundException(
                                        "Handover not found with ID: " + handoverID));

        return convertToDto(handover);
    }

    /* ================= GET BY MANIFEST ID ================= */
    @Override
    public HandoverDTO findByManifestID(Long manifestID) {

        Handover handover =
                handoverRepository.findByManifest_ManifestID(manifestID);

        if (handover == null) {
            throw new HandoverNotFoundException(
                    "Handover not found for Manifest ID: " + manifestID);
        }

        return convertToDto(handover);
    }

    /* ================= GET BY HANDED BY ================= */
    @Override
    public List<HandoverDTO> findByHandedBy(String handedBy) {

        List<Handover> handovers =
                handoverRepository.findByHandedBy(handedBy);

        List<HandoverDTO> dtoList = new ArrayList<>();

        for (Handover handover : handovers) {
            dtoList.add(convertToDto(handover));
        }

        return dtoList;
    }

    /* ================= GET ALL ================= */
    @Override
    public List<HandoverDTO> getAll() {

        List<Handover> handovers = handoverRepository.findAll();

        if (handovers.isEmpty()) {
            throw new HandoverNotFoundException("No Handover records found");
        }

        List<HandoverDTO> dtoList = new ArrayList<>();

        for (Handover handover : handovers) {
            dtoList.add(convertToDto(handover));
        }

        return dtoList;
    }

    /* ================= UPDATE ================= */
    @Override
    public HandoverDTO update(HandoverDTO dto) {

        Handover existing =
                handoverRepository.findById(dto.getHandoverID())
                        .orElseThrow(() ->
                                new HandoverNotFoundException(
                                        "Handover not found with ID: " + dto.getHandoverID()));

        if (dto.getHandedBy() != null)
            existing.setHandedBy(dto.getHandedBy());

        if (dto.getHandedAt() != null)
            existing.setHandedAt(dto.getHandedAt());

        if (dto.getReceivedBy() != null)
            existing.setReceivedBy(dto.getReceivedBy());

        if (dto.getReceivedAt() != null)
            existing.setReceivedAt(dto.getReceivedAt());

        if (dto.getNotes() != null)
            existing.setNotes(dto.getNotes());

        return convertToDto(
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