package com.example.demo.serviceimpl;
import java.util.ArrayList;
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
 
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
 
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
 
    private static final String LOAD_SERVICE_URL =
    		"http://ROUTING-SERVICE/cargoRoute/loads/getLoad/";
 
    private static final String FLEET_SERVICE_URL =
    		"http://FLEET-SERVICE/cargoRoute/vehicles/getVehicle/";
 
    //CREATE 
 
    @Override
    public ManifestDTO create(ManifestDTO manifestDTO) {
        Manifest manifest = convertToEntity(manifestDTO);
        return convertToDTO(manifestRepository.save(manifest));
    }
 
    //FETCH
 
    @Override
    @CircuitBreaker(name = MANIFEST_CB, fallbackMethod = "getByIdFallback")
    public ManifestRequiredResponseDTO getById(Long manifestID) {
        return buildResponse(findManifest(manifestID));
    }
 
    public ManifestRequiredResponseDTO getByIdFallback(Long manifestID, Throwable t) {
        System.out.println("Fallback executed: unable to fetch full manifest response for ID = " + manifestID);
        ManifestRequiredResponseDTO response = new ManifestRequiredResponseDTO();
        response.setManifest(convertToDTO(findManifest(manifestID)));
        response.setLoad(null);
        response.setVehicle(null);
        return response;
    }
 
    @Override
    @CircuitBreaker(name = MANIFEST_CB, fallbackMethod = "getAllFallback")
    public List<ManifestRequiredResponseDTO> getAll() {
        List<Manifest> manifests = manifestRepository.findAll();
        List<ManifestRequiredResponseDTO> responses = new ArrayList<>();
 
        for (Manifest manifest : manifests) {
            responses.add(buildResponse(manifest));
        }
        return responses;
    }
 
    public List<ManifestRequiredResponseDTO> getAllFallback(Throwable t) {
        System.out.println("Fallback executed: unable to fetch full manifest list, returning partial data");
        List<ManifestRequiredResponseDTO> responses = new ArrayList<>();
        for (Manifest manifest : manifestRepository.findAll()) {
            ManifestRequiredResponseDTO response = new ManifestRequiredResponseDTO();
            response.setManifest(convertToDTO(manifest));
            responses.add(response);
        }
        return responses;
    }
 
    @Override
    @CircuitBreaker(name = MANIFEST_CB, fallbackMethod = "getByLoadIDFallback")
    public ManifestRequiredResponseDTO getByLoadID(Long loadID) {
        Manifest manifest = manifestRepository.findByLoadID(loadID);
        if (manifest == null) {
            throw new ResourceNotFoundException(
                    "Manifest not found for loadID: " + loadID);
        }
        return buildResponse(manifest);
    }
 
    public ManifestRequiredResponseDTO getByLoadIDFallback(Long loadID, Throwable t) {
        System.out.println("Fallback executed: unable to fetch full manifest response for loadID = " + loadID);
        Manifest manifest = manifestRepository.findByLoadID(loadID);
        if (manifest == null) return null;
        ManifestRequiredResponseDTO response = new ManifestRequiredResponseDTO();
        response.setManifest(convertToDTO(manifest));
        response.setLoad(null);
        response.setVehicle(null);
        return response;
    }
 
    @Override
    @CircuitBreaker(name = MANIFEST_CB, fallbackMethod = "getByWarehouseIDFallback")
    public List<ManifestRequiredResponseDTO> getByWarehouseID(Long warehouseID) {
        List<Manifest> manifests =
                manifestRepository.findByWarehouseID(warehouseID);
 
        List<ManifestRequiredResponseDTO> responses = new ArrayList<>();
        for (Manifest manifest : manifests) {
            responses.add(buildResponse(manifest));
        }
        return responses;
    }
 
    public List<ManifestRequiredResponseDTO> getByWarehouseIDFallback(Long warehouseID, Throwable t) {
        System.out.println("Fallback executed: unable to fetch full manifest response for warehouseID = " + warehouseID);
        List<ManifestRequiredResponseDTO> responses = new ArrayList<>();
        for (Manifest manifest : manifestRepository.findByWarehouseID(warehouseID)) {
            ManifestRequiredResponseDTO response = new ManifestRequiredResponseDTO();
            response.setManifest(convertToDTO(manifest));
            responses.add(response);
        }
        return responses;
    }
 
    //UPDATE
 
    @Override
    public ManifestDTO update(Long manifestID, ManifestDTO manifestDTO) {
        Manifest manifest = findManifest(manifestID);

        manifest.setWarehouseID(manifestDTO.getWarehouseID());
        manifest.setItemsJSON(manifestDTO.getItemsJSON());
        manifest.setManifestURI(manifestDTO.getManifestURI());

        return convertToDTO(manifestRepository.save(manifest));
    }

 
    //DELETE
 
    @Override
    public void delete(Long manifestID) {
        if (!manifestRepository.existsById(manifestID)) {
            throw new ResourceNotFoundException(
                    "Manifest not found with ID: " + manifestID);
        }
        manifestRepository.deleteById(manifestID);
    }
 
    //CIRCUIT BREAKER CALLS
 
    @CircuitBreaker(name = "loadService", fallbackMethod = "loadFallback")
    private LoadResponseDTO callLoadService(Long loadId) {
        return restTemplate.getForObject(
                LOAD_SERVICE_URL + loadId,
                LoadResponseDTO.class
        );
    }
 
    private LoadResponseDTO loadFallback(Long loadId, Exception ex) {
        System.out.println(
            "Fallback executed: LOAD service unavailable for loadId = " + loadId
        );
        return null;
    }
 
    @CircuitBreaker(name = "fleetService", fallbackMethod = "fleetFallback")
    private VehicleDTO callFleetService(Long vehicleId) {
        return restTemplate.getForObject(
                FLEET_SERVICE_URL + vehicleId,
                VehicleDTO.class
        );
    }
 
    private VehicleDTO fleetFallback(Long vehicleId, Exception ex) {
        System.out.println(
            "Fallback executed: FLEET service unavailable for vehicleId = " + vehicleId
        );
        return null;
    }
 
    
 
    private ManifestRequiredResponseDTO buildResponse(Manifest manifest) {
 
        ManifestRequiredResponseDTO response =
                new ManifestRequiredResponseDTO();
 
        response.setManifest(convertToDTO(manifest));
 
        LoadResponseDTO loadResponse =
                callLoadService(manifest.getLoadID());
 
        if (loadResponse != null && loadResponse.getLoad() != null) {
 
            LoadDTO load = loadResponse.getLoad();
            response.setLoad(load);
 
            if (load.getVehicleID() != null) {
                VehicleDTO vehicle =
                        callFleetService(load.getVehicleID());
                response.setVehicle(vehicle);
            }
        }
 
        return response;
    }
 
    
 
    private Manifest findManifest(Long id) {
        return manifestRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
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