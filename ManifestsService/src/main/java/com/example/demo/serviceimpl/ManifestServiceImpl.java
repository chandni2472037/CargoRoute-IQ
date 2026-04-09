package com.example.demo.serviceimpl;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.LoadDTO;
import com.example.demo.dto.LoadResponseDTO;
import com.example.demo.dto.ManifestDTO;
import com.example.demo.dto.ManifestRequiredResponseDTO;
import com.example.demo.dto.VehicleDTO;
import com.example.demo.entities.Manifest;
import com.example.demo.exception.ManifestNotFoundException;
import com.example.demo.repository.ManifestRepository;
import com.example.demo.service.ManifestService;

@Service
public class ManifestServiceImpl implements ManifestService {

    @Autowired
    private ManifestRepository manifestRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String LOAD_SERVICE_URL =
            "http://ROUTING-SERVICE/loads/";

    private static final String FLEET_SERVICE_URL =
            "http://FLEET-SERVICE/vehicles/";

    /* ================= CREATE ================= */

    @Override
    public ManifestDTO create(ManifestDTO manifestDTO) {
        Manifest manifest = convertToEntity(manifestDTO);
        return convertToDTO(manifestRepository.save(manifest));
    }

    /* ================= FETCH ================= */

    @Override
    public ManifestRequiredResponseDTO getById(Long manifestID) {
        return buildResponse(findManifest(manifestID));
    }

    @Override
    public List<ManifestRequiredResponseDTO> getAll() {
        List<Manifest> manifests = manifestRepository.findAll();
        List<ManifestRequiredResponseDTO> responses = new ArrayList<>();

        for (Manifest manifest : manifests) {
            responses.add(buildResponse(manifest));
        }
        return responses;
    }

    @Override
    public ManifestRequiredResponseDTO getByLoadID(Long loadID) {
        Manifest manifest = manifestRepository.findByLoadID(loadID);
        if (manifest == null) {
            throw new ManifestNotFoundException(
                    "Manifest not found for loadID: " + loadID);
        }
        return buildResponse(manifest);
    }

    @Override
    public List<ManifestRequiredResponseDTO> getByWarehouseID(Long warehouseID) {
        List<Manifest> manifests =
                manifestRepository.findByWarehouseID(warehouseID);

        List<ManifestRequiredResponseDTO> responses = new ArrayList<>();
        for (Manifest manifest : manifests) {
            responses.add(buildResponse(manifest));
        }
        return responses;
    }

    /* ================= UPDATE ================= */

    @Override
    public ManifestDTO update(Long manifestID, ManifestDTO manifestDTO) {
        Manifest manifest = findManifest(manifestID);

        manifest.setLoadID(manifestDTO.getLoadID());
        manifest.setWarehouseID(manifestDTO.getWarehouseID());
        manifest.setItemsJSON(manifestDTO.getItemsJSON());
        manifest.setManifestURI(manifestDTO.getManifestURI());

        return convertToDTO(manifestRepository.save(manifest));
    }

    /* ================= DELETE ================= */

    @Override
    public void delete(Long manifestID) {
        if (!manifestRepository.existsById(manifestID)) {
            throw new ManifestNotFoundException(
                    "Manifest not found with ID: " + manifestID);
        }
        manifestRepository.deleteById(manifestID);
    }

    /* ================= CORE COMPOSITION ================= */

    private ManifestRequiredResponseDTO buildResponse(Manifest manifest) {

        ManifestRequiredResponseDTO response =
                new ManifestRequiredResponseDTO();

        // ✅ Manifest
        response.setManifest(convertToDTO(manifest));

        try {
            // ✅ Load Service call
            LoadResponseDTO loadResponse =
                    restTemplate.getForObject(
                            LOAD_SERVICE_URL + manifest.getLoadID(),
                            LoadResponseDTO.class
                    );

            if (loadResponse != null && loadResponse.getLoadDto() != null) {

                LoadDTO load = loadResponse.getLoadDto();
                response.setLoad(load);

                // ✅ Fleet Service call (Vehicle + Driver)
                if (load.getVehicleID() != null) {

                    VehicleDTO vehicle =
                            restTemplate.getForObject(
                                    FLEET_SERVICE_URL + load.getVehicleID(),
                                    VehicleDTO.class
                            );

                    response.setVehicle(vehicle);
                }
            }

        } catch (Exception ex) {
            // graceful degradation
            response.setLoad(null);
            response.setVehicle(null);
        }

        return response;
    }

    /* ================= HELPERS ================= */

    private Manifest findManifest(Long id) {
        return manifestRepository.findById(id)
                .orElseThrow(() ->
                        new ManifestNotFoundException(
                                "Manifest not found with ID: " + id));
    }

    private ManifestDTO convertToDTO(Manifest manifest) {
        ManifestDTO dto = new ManifestDTO();
        dto.setManifestID(manifest.getManifestID());
        dto.setLoadID(manifest.getLoadID());
        dto.setWarehouseID(manifest.getWarehouseID());
        dto.setItemsJSON(manifest.getItemsJSON());
        dto.setCreatedBy(manifest.getCreatedBy());
        dto.setCreatedAt(manifest.getCreatedAt());
        dto.setManifestURI(manifest.getManifestURI());
        return dto;
    }

    private Manifest convertToEntity(ManifestDTO dto) {
        Manifest manifest = new Manifest();
        manifest.setLoadID(dto.getLoadID());
        manifest.setWarehouseID(dto.getWarehouseID());
        manifest.setItemsJSON(dto.getItemsJSON());
        manifest.setCreatedBy(dto.getCreatedBy());
        manifest.setCreatedAt(dto.getCreatedAt());
        manifest.setManifestURI(dto.getManifestURI());
        return manifest;
    }
}