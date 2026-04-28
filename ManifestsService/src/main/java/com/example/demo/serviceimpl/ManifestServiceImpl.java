package com.example.demo.serviceimpl;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import jakarta.servlet.http.HttpServletRequest;

import com.example.demo.clients.NotificationClient;
import com.example.demo.clients.TaskClient;
import com.example.demo.dto.LoadDTO;
import com.example.demo.dto.LoadResponseDTO;
import com.example.demo.dto.ManifestDTO;
import com.example.demo.dto.ManifestRequiredResponseDTO;
import com.example.demo.dto.VehicleDTO;
import com.example.demo.entities.Manifest;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ManifestRepository;
import com.example.demo.service.ManifestService;

@Service
public class ManifestServiceImpl implements ManifestService {

    private static final String MANIFEST_CB = "manifestService";

    @Autowired
    private ManifestRepository manifestRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private TaskClient taskClient;

    private static final String LOAD_SERVICE_URL =
            "http://ROUTING-SERVICE/cargoRoute/loads/getLoad/";

    private static final String FLEET_SERVICE_URL =
            "http://FLEET-SERVICE/cargoRoute/vehicles/getVehicle/";

    // ================= CREATE =================
    @Override
    public ManifestDTO create(ManifestDTO manifestDTO) {
        Manifest manifest = convertToEntity(manifestDTO);
        Manifest saved = manifestRepository.save(manifest);
        Long userId = resolveActorUserId(saved.getCreatedBy());
        notificationClient.notifyUser(
            userId,
            saved.getManifestID(),
            "Pickup task created for manifest " + saved.getManifestID() + ".",
            "Pickup"
        );
        // WHY: warehouse teams need explicit pickup tasks tied to manifest generation.
        taskClient.createTask(
            userId,
            saved.getManifestID(),
            "Prepare pickup handover for manifest " + saved.getManifestID() + ".",
            null
        );
        return convertToDTO(saved);
    }

    // ================= FETCH BY ID =================
    @Override
    @CircuitBreaker(name = MANIFEST_CB, fallbackMethod = "getByIdFallback")
    public ManifestRequiredResponseDTO getById(Long manifestID) {
        return buildResponse(findManifest(manifestID));
    }

    public ManifestRequiredResponseDTO getByIdFallback(Long manifestID, Throwable t) {
        ManifestRequiredResponseDTO response = new ManifestRequiredResponseDTO();
        response.setManifest(convertToDTO(findManifest(manifestID)));
        response.setLoad(null);
        response.setVehicle(null);
        return response;
    }

    // ================= FETCH ALL =================
    @Override
    @CircuitBreaker(name = MANIFEST_CB, fallbackMethod = "getAllFallback")
    public List<ManifestRequiredResponseDTO> getAll() {

        List<ManifestRequiredResponseDTO> responses = new ArrayList<>();
        for (Manifest manifest : manifestRepository.findAll()) {
            responses.add(buildResponse(manifest));
        }
        return responses;
    }

    public List<ManifestRequiredResponseDTO> getAllFallback(Throwable t) {

        List<ManifestRequiredResponseDTO> responses = new ArrayList<>();
        for (Manifest manifest : manifestRepository.findAll()) {
            ManifestRequiredResponseDTO response = new ManifestRequiredResponseDTO();
            response.setManifest(convertToDTO(manifest));
            response.setLoad(null);
            response.setVehicle(null);
            responses.add(response);
        }
        return responses;
    }

    // ================= FETCH BY LOAD ID =================
    @Override
    @CircuitBreaker(name = MANIFEST_CB, fallbackMethod = "getByLoadIDFallback")
    public ManifestRequiredResponseDTO getByLoadID(Long loadID) {

        Manifest manifest = manifestRepository.findByLoadID(loadID);
        if (manifest == null) {
            throw new ResourceNotFoundException("Manifest not found for loadID: " + loadID);
        }
        return buildResponse(manifest);
    }

    public ManifestRequiredResponseDTO getByLoadIDFallback(Long loadID, Throwable t) {

        Manifest manifest = manifestRepository.findByLoadID(loadID);
        if (manifest == null) {
            throw new ResourceNotFoundException("Manifest not found for loadID: " + loadID);
        }

        ManifestRequiredResponseDTO response = new ManifestRequiredResponseDTO();
        response.setManifest(convertToDTO(manifest));
        response.setLoad(null);
        response.setVehicle(null);
        return response;
    }

    // ================= FETCH BY WAREHOUSE ID =================
    @Override
    @CircuitBreaker(name = MANIFEST_CB, fallbackMethod = "getByWarehouseIDFallback")
    public List<ManifestRequiredResponseDTO> getByWarehouseID(Long warehouseID) {

        List<Manifest> manifests = manifestRepository.findByWarehouseID(warehouseID);
        if (manifests.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No manifests found for warehouseID: " + warehouseID);
        }

        List<ManifestRequiredResponseDTO> responses = new ArrayList<>();
        for (Manifest manifest : manifests) {
            responses.add(buildResponse(manifest));
        }
        return responses;
    }

    public List<ManifestRequiredResponseDTO> getByWarehouseIDFallback(
            Long warehouseID, Throwable t) {

        List<Manifest> manifests = manifestRepository.findByWarehouseID(warehouseID);
        if (manifests.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No manifests found for warehouseID: " + warehouseID);
        }

        List<ManifestRequiredResponseDTO> responses = new ArrayList<>();
        for (Manifest manifest : manifests) {
            ManifestRequiredResponseDTO response = new ManifestRequiredResponseDTO();
            response.setManifest(convertToDTO(manifest));
            response.setLoad(null);
            response.setVehicle(null);
            responses.add(response);
        }
        return responses;
    }

    // ================= UPDATE =================
    @Override
    public ManifestDTO update(Long manifestID, ManifestDTO manifestDTO) {

        Manifest manifest = findManifest(manifestID);

        manifest.setWarehouseID(manifestDTO.getWarehouseID());
        manifest.setItemsJSON(manifestDTO.getItemsJSON());
        manifest.setManifestURI(manifestDTO.getManifestURI());

        Manifest updated = manifestRepository.save(manifest);
        Long userId = resolveActorUserId(updated.getCreatedBy());
        notificationClient.notifyUser(
            userId,
            updated.getManifestID(),
            "Delivery task updated for manifest " + updated.getManifestID() + ".",
            "Delivery"
        );
        // WHY: manifest updates impact delivery execution and must create a follow-up task.
        taskClient.createTask(
            userId,
            updated.getManifestID(),
            "Reconfirm delivery readiness for manifest " + updated.getManifestID() + ".",
            null
        );
        return convertToDTO(updated);
    }

    // ================= DELETE =================
    @Override
    public void delete(Long manifestID) {
        if (!manifestRepository.existsById(manifestID)) {
            throw new ResourceNotFoundException(
                    "Manifest not found with ID: " + manifestID);
        }
        manifestRepository.deleteById(manifestID);
    }

    // ================= REMOTE SERVICE CALLS =================
    @CircuitBreaker(name = "loadService", fallbackMethod = "loadFallback")
    private LoadResponseDTO callLoadService(Long loadId) {
        return restTemplate.getForObject(
                LOAD_SERVICE_URL + loadId,
                LoadResponseDTO.class
        );
    }

    private LoadResponseDTO loadFallback(Long loadId, Throwable ex) {
        return null;
    }

    @CircuitBreaker(name = "fleetService", fallbackMethod = "fleetFallback")
    private VehicleDTO callFleetService(Long vehicleId) {
        return restTemplate.getForObject(
                FLEET_SERVICE_URL + vehicleId,
                VehicleDTO.class
        );
    }

    private VehicleDTO fleetFallback(Long vehicleId, Throwable ex) {
        return null;
    }

    // ================= RESPONSE BUILDER (DEFENSIVE) =================
    private ManifestRequiredResponseDTO buildResponse(Manifest manifest) {

        ManifestRequiredResponseDTO response = new ManifestRequiredResponseDTO();
        response.setManifest(convertToDTO(manifest));

        try {
            LoadResponseDTO loadResponse =
                    callLoadService(manifest.getLoadID());

            if (loadResponse == null || loadResponse.getLoad() == null) {
                response.setLoad(null);
                response.setVehicle(null);
                return response;
            }

            LoadDTO load = loadResponse.getLoad();
            response.setLoad(load);

            if (load.getVehicleID() != null) {
                VehicleDTO vehicle =
                        callFleetService(load.getVehicleID());
                response.setVehicle(vehicle);
            } else {
                response.setVehicle(null);
            }

        } catch (Exception ex) {
            response.setLoad(null);
            response.setVehicle(null);
        }

        return response;
    }

    // ================= UTILITIES =================
    private Manifest findManifest(Long id) {
        return manifestRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Manifest not found with ID: " + id)
                );
    }

    private Long parseUserId(String createdBy) {
        if (createdBy == null || createdBy.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(createdBy.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Long resolveActorUserId(String createdBy) {
        Long parsed = parseUserId(createdBy);
        if (parsed != null) {
            return parsed;
        }
        // WHY: createdBy can be username text, fallback to authenticated user to keep non-pickup flows deliverable.
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            HttpServletRequest request = servletAttributes.getRequest();
            Object userId = request.getAttribute("userId");
            if (userId instanceof Long value) {
                return value;
            }
            if (userId instanceof Integer value) {
                return value.longValue();
            }
            if (userId instanceof String value) {
                try {
                    return Long.parseLong(value);
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }
        return null;
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
        //manifest.setCreatedAt(dto.getCreatedAt());
        manifest.setManifestURI(dto.getManifestURI());
        return manifest;
    }
}
