package com.example.demo.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.LoadDTO;
import com.example.demo.dto.ManifestDTO;
import com.example.demo.dto.ManifestRequiredResponseDTO;
import com.example.demo.entities.Manifest;
import com.example.demo.exception.InvalidDataException;
import com.example.demo.exception.ManifestNotFoundException;
import com.example.demo.repository.ManifestRepository;
import com.example.demo.service.ManifestService;

@Service
public class ManifestServiceImpl implements ManifestService {

    @Autowired
    private ManifestRepository manifestRepository;

    @Autowired
    private RestTemplate restTemplate;

    /* ================= Entity → DTO ================= */
    private ManifestDTO convertToDto(Manifest manifest) {

        ManifestDTO dto = new ManifestDTO();

        dto.setManifestID(manifest.getManifestID());
        dto.setWarehouseID(manifest.getWarehouseID());
        dto.setItemsJSON(manifest.getItemsJSON());
        dto.setCreatedBy(manifest.getCreatedBy());
        dto.setCreatedAt(manifest.getCreatedAt());
        dto.setManifestURI(manifest.getManifestURI());

        LoadDTO loadDto = new LoadDTO();
        loadDto.setLoadID(manifest.getLoadID());
        dto.setLoadID(loadDto);

        return dto;
    }

    /* ================= DTO → Entity ================= */
    private Manifest convertToEntity(ManifestDTO dto) {

        Manifest manifest = new Manifest();

        manifest.setLoadID(dto.getLoadID().getLoadID());
        manifest.setWarehouseID(dto.getWarehouseID());
        manifest.setItemsJSON(dto.getItemsJSON());
        manifest.setCreatedBy(dto.getCreatedBy());
        manifest.setCreatedAt(dto.getCreatedAt());
        manifest.setManifestURI(dto.getManifestURI());

        return manifest;
    }

    /* ================= REST CALL ================= */
    private ManifestRequiredResponseDTO fetchLoadResponse(Manifest manifest) {

        ManifestRequiredResponseDTO response =
                new ManifestRequiredResponseDTO();

        response.setManifest(convertToDto(manifest));

        LoadDTO load =
                restTemplate.getForObject(
                        "http://LOAD-SERVICE/api/loads/" + manifest.getLoadID(),
                        LoadDTO.class);

        response.setLoad(load);
        return response;
    }

    /* ================= CREATE ================= */
    @Override
    public ManifestDTO create(ManifestDTO dto) {

        if (dto == null || dto.getLoadID() == null ||
            dto.getLoadID().getLoadID() == null) {
            throw new InvalidDataException("Load ID is mandatory");
        }

        Manifest saved =
                manifestRepository.save(convertToEntity(dto));

        return convertToDto(saved);
    }

    /* ================= GET BY ID ================= */
    @Override
    public ManifestRequiredResponseDTO getById(Long manifestID) {

        Manifest manifest =
                manifestRepository.findById(manifestID)
                        .orElseThrow(() ->
                                new ManifestNotFoundException(
                                        "Manifest not found with ID: " + manifestID));

        return fetchLoadResponse(manifest);
    }

    /* ================= GET ALL ================= */
    @Override
    public List<ManifestRequiredResponseDTO> getAll() {

        List<Manifest> manifests = manifestRepository.findAll();

        if (manifests.isEmpty()) {
            throw new ManifestNotFoundException("No Manifest records found");
        }

        List<ManifestRequiredResponseDTO> responseList =
                new ArrayList<>();

        for (Manifest manifest : manifests) {
            responseList.add(fetchLoadResponse(manifest));
        }

        return responseList;
    }

    /* ================= GET BY LOAD ID ================= */
    @Override
    public ManifestRequiredResponseDTO getByLoadID(Long loadID) {

        Manifest manifest =
                manifestRepository.findByLoadID(loadID);

        if (manifest == null) {
            throw new ManifestNotFoundException(
                    "No Manifest found for Load ID: " + loadID);
        }

        return fetchLoadResponse(manifest);
    }

    /* ================= GET BY WAREHOUSE ID ================= */
    @Override
    public List<ManifestRequiredResponseDTO> getByWarehouseID(Long warehouseID) {

        List<Manifest> manifests =
                manifestRepository.findByWarehouseID(warehouseID);

        List<ManifestRequiredResponseDTO> responseList =
                new ArrayList<>();

        for (Manifest manifest : manifests) {
            responseList.add(fetchLoadResponse(manifest));
        }

        return responseList;
    }

    /* ================= UPDATE ================= */
    @Override
    public ManifestDTO update(Long manifestID, ManifestDTO dto) {

        Manifest existing =
                manifestRepository.findById(manifestID)
                        .orElseThrow(() ->
                                new ManifestNotFoundException(
                                        "Manifest not found"));

        if (dto.getWarehouseID() != null)
            existing.setWarehouseID(dto.getWarehouseID());
        if (dto.getItemsJSON() != null)
            existing.setItemsJSON(dto.getItemsJSON());
        if (dto.getManifestURI() != null)
            existing.setManifestURI(dto.getManifestURI());

        return convertToDto(
                manifestRepository.save(existing));
    }

    /* ================= DELETE ================= */
    @Override
    public void delete(Long manifestID) {

        Manifest manifest =
                manifestRepository.findById(manifestID)
                        .orElseThrow(() ->
                                new ManifestNotFoundException(
                                        "Manifest not found with ID: " + manifestID));

        manifestRepository.delete(manifest);
    }
}