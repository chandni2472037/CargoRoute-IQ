package com.example.demo.serviceimpl;
import java.util.ArrayList;
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
 
 
import com.example.demo.dto.DispatchDTO;
import com.example.demo.dto.DispatchResponseDTO;
import com.example.demo.dto.LoadDTO;
import com.example.demo.dto.LoadResponseDTO;
import com.example.demo.dto.VehicleDTO;
import com.example.demo.entities.Dispatch;
import com.example.demo.entities.enums.DispatchStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.DispatchRepository;
import com.example.demo.service.DispatchService;
 
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
 
@Service
public class DispatchServiceImpl implements DispatchService {
 
    private static final String DISPATCH_CB = "dispatchService";
 
    @Autowired
    private DispatchRepository dispatchRepository;
 
    @Autowired
    private RestTemplate restTemplate;
 
    private static final String LOAD_SERVICE_URL =
            "http://ROUTING-SERVICE/cargoRoute/loads/getLoad/";
 
    private static final String FLEET_SERVICE_URL =
    		"http://FLEET-SERVICE/cargoRoute/vehicles/getVehicle/";
 
    /* ================= CREATE ================= */
 
    @Override
    public DispatchDTO insert(DispatchDTO dto) {
        Dispatch dispatch = convertToEntity(dto);
        return convertToDto(dispatchRepository.save(dispatch));
    }
 
    /* ================= FETCH ================= */
 
    @Override
    @CircuitBreaker(name = DISPATCH_CB, fallbackMethod = "fetchByIDFallback")
    public DispatchResponseDTO fetchByID(Long dispatchID) {
        Dispatch dispatch = findDispatch(dispatchID);
        return buildResponse(dispatch);
    }
 
    public DispatchResponseDTO fetchByIDFallback(Long dispatchID, Throwable t) {
        System.out.println("Fallback executed: unable to fetch full dispatch response for ID = " + dispatchID);
        Dispatch dispatch = findDispatch(dispatchID);
        DispatchResponseDTO response = new DispatchResponseDTO();
        response.setDispatch(convertToDto(dispatch));
        response.setLoad(null);
        response.setVehicle(null);
        return response;
    }
 
    @Override
    @CircuitBreaker(name = DISPATCH_CB, fallbackMethod = "fetchByAssignedByFallback")
    public List<DispatchResponseDTO> fetchByAssignedBy(String assignedBy) {
        List<DispatchResponseDTO> responses = new ArrayList<>();
        for (Dispatch dispatch :
                dispatchRepository.findByAssignedBy(assignedBy)) {
            responses.add(buildResponse(dispatch));
        }
        return responses;
    }
 
    public List<DispatchResponseDTO> fetchByAssignedByFallback(String assignedBy, Throwable t) {
        System.out.println("Fallback executed: unable to fetch full response for assignedBy = " + assignedBy);
        List<DispatchResponseDTO> responses = new ArrayList<>();
        for (Dispatch dispatch : dispatchRepository.findByAssignedBy(assignedBy)) {
            DispatchResponseDTO response = new DispatchResponseDTO();
            response.setDispatch(convertToDto(dispatch));
            responses.add(response);
        }
        return responses;
    }
 
    @Override
    @CircuitBreaker(name = DISPATCH_CB, fallbackMethod = "fetchByStatusFallback")
    public List<DispatchResponseDTO> fetchByStatus(DispatchStatus status) {
        List<DispatchResponseDTO> responses = new ArrayList<>();
        for (Dispatch dispatch :
                dispatchRepository.findByStatus(status)) {
            responses.add(buildResponse(dispatch));
        }
        return responses;
    }
 
    public List<DispatchResponseDTO> fetchByStatusFallback(DispatchStatus status, Throwable t) {
        System.out.println("Fallback executed: unable to fetch full response for status = " + status);
        List<DispatchResponseDTO> responses = new ArrayList<>();
        for (Dispatch dispatch : dispatchRepository.findByStatus(status)) {
            DispatchResponseDTO response = new DispatchResponseDTO();
            response.setDispatch(convertToDto(dispatch));
            responses.add(response);
        }
        return responses;
    }
 
    @Override
    @CircuitBreaker(name = DISPATCH_CB, fallbackMethod = "fetchAllFallback")
    public List<DispatchResponseDTO> fetchAll() {
        List<DispatchResponseDTO> responses = new ArrayList<>();
        for (Dispatch dispatch :dispatchRepository.findAll()) {
            responses.add(buildResponse(dispatch));
        }
        return responses;
    }
 
    public List<DispatchResponseDTO> fetchAllFallback(Throwable t) {
        System.out.println("Fallback executed: unable to fetch full dispatch list, returning partial data");
        List<DispatchResponseDTO> responses = new ArrayList<>();
        for (Dispatch dispatch : dispatchRepository.findAll()) {
            DispatchResponseDTO response = new DispatchResponseDTO();
            response.setDispatch(convertToDto(dispatch));
            responses.add(response);
        }
        return responses;
    }
 
    /* ================= UPDATE ================= */
 
    @Override
    public DispatchDTO updateDispatch(Long dispatchID, DispatchDTO dto) {
 
        Dispatch dispatch = findDispatch(dispatchID);
 
        if (dto.getLoadID() != null)
            dispatch.setLoadID(dto.getLoadID());
        if (dto.getAssignedDriverID() != null)
            dispatch.setAssignedDriverID(dto.getAssignedDriverID());
        if (dto.getAssignedBy() != null)
            dispatch.setAssignedBy(dto.getAssignedBy());
        if (dto.getAssignedAt() != null)
            dispatch.setAssignedAt(dto.getAssignedAt());
        if (dto.getStatus() != null)
            dispatch.setStatus(dto.getStatus());
 
        return convertToDto(dispatchRepository.save(dispatch));
    }
 
    /* ================= DELETE ================= */
 
    @Override
    public void delete(Long dispatchID) {
        dispatchRepository.delete(findDispatch(dispatchID));
    }
 
    /* ================= CIRCUIT BREAKER CALLS ================= */
 
    @CircuitBreaker(name = "loadService", fallbackMethod = "loadFallback")
    public LoadResponseDTO callLoadService(Long loadId) {
        return restTemplate.getForObject(
                LOAD_SERVICE_URL + loadId,
                LoadResponseDTO.class
        );
    }
 
    public LoadResponseDTO loadFallback(Long loadId, Exception ex) {
        System.out.println(
            "Fallback executed: LOAD service unavailable for loadId = " + loadId
        );
        return null;
    }
 
    @CircuitBreaker(name = "fleetService", fallbackMethod = "fleetFallback")
    public VehicleDTO callFleetService(Long vehicleId) {
        return restTemplate.getForObject(
                FLEET_SERVICE_URL + vehicleId,
                VehicleDTO.class
        );
    }
 
    public VehicleDTO fleetFallback(Long vehicleId, Exception ex) {
        System.out.println(
            "Fallback executed: FLEET service unavailable for vehicleId = " + vehicleId
        );
        return null;
    }
 
    /* ================= RESPONSE BUILDER ================= */
 
    private DispatchResponseDTO buildResponse(Dispatch dispatch) {
 
        DispatchResponseDTO response = new DispatchResponseDTO();
        response.setDispatch(convertToDto(dispatch));
 
        LoadResponseDTO loadResponse =
                callLoadService(dispatch.getLoadID());
 
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
 
    /* ================= HELPERS ================= */
 
    private Dispatch findDispatch(Long id) {
        return dispatchRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Dispatch not found with ID: " + id));
    }
 
    private DispatchDTO convertToDto(Dispatch dispatch) {
        DispatchDTO dto = new DispatchDTO();
        dto.setDispatchID(dispatch.getDispatchID());
        dto.setLoadID(dispatch.getLoadID());
        dto.setAssignedDriverID(dispatch.getAssignedDriverID());
        dto.setAssignedBy(dispatch.getAssignedBy());
        dto.setAssignedAt(dispatch.getAssignedAt());
        dto.setStatus(dispatch.getStatus());
        return dto;
    }
 
    private Dispatch convertToEntity(DispatchDTO dto) {
        Dispatch dispatch = new Dispatch();
        dispatch.setDispatchID(dto.getDispatchID());
        dispatch.setLoadID(dto.getLoadID());
        dispatch.setAssignedDriverID(dto.getAssignedDriverID());
        dispatch.setAssignedBy(dto.getAssignedBy());
        dispatch.setAssignedAt(dto.getAssignedAt());
        dispatch.setStatus(dto.getStatus());
        return dispatch;
    }
}