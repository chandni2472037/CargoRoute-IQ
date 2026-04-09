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

@Service
public class DispatchServiceImpl implements DispatchService {

    @Autowired
    private DispatchRepository dispatchRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String LOAD_SERVICE_URL =
            "http://ROUTING-SERVICE/loads/";

    private static final String FLEET_SERVICE_URL =
            "http://FLEET-SERVICE/vehicles/";

    /* ================= CREATE ================= */
    
    @Override
    public DispatchDTO insert(DispatchDTO dto) {
        Dispatch dispatch = convertToEntity(dto);
        return convertToDto(dispatchRepository.save(dispatch));
    }

    /* ================= FETCH ================= */

    @Override
    public DispatchResponseDTO fetchByID(Long dispatchID) {
        Dispatch dispatch = findDispatch(dispatchID);
        return buildResponse(dispatch);
    }

    
    @Override
    public List<DispatchResponseDTO> fetchByAssignedBy(String assignedBy) {
        List<DispatchResponseDTO> responses = new ArrayList<>();
        for (Dispatch dispatch :
                dispatchRepository.findByAssignedBy(assignedBy)) {
            responses.add(buildResponse(dispatch));
        }
        return responses;
    }

    @Override
    public List<DispatchResponseDTO> fetchByStatus(DispatchStatus status) {
        List<DispatchResponseDTO> responses = new ArrayList<>();
        for (Dispatch dispatch :
                dispatchRepository.findByStatus(status)) {
            responses.add(buildResponse(dispatch));
        }
        return responses;
    }

    @Override
    public List<DispatchResponseDTO> fetchAll() {
        List<DispatchResponseDTO> responses = new ArrayList<>();
        for (Dispatch dispatch :
                dispatchRepository.findAll()) {
            responses.add(buildResponse(dispatch));
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

    

    private DispatchResponseDTO buildResponse(Dispatch dispatch) {

        DispatchResponseDTO response = new DispatchResponseDTO();
        response.setDispatch(convertToDto(dispatch));

        try {
            //Call LOAD service
            LoadResponseDTO loadResponse =
                    restTemplate.getForObject(
                            LOAD_SERVICE_URL + dispatch.getLoadID(),
                            LoadResponseDTO.class
                    );

            if (loadResponse != null && loadResponse.getLoadDto() != null) {

                LoadDTO load = loadResponse.getLoadDto();
                response.setLoadDto(load);

                // Fleet service call
                if (load.getVehicleID() != null) {
                    VehicleDTO vehicle =
                            restTemplate.getForObject(
                                    FLEET_SERVICE_URL + load.getVehicleID(),
                                    VehicleDTO.class
                            );
                    response.setVehicle(vehicle);
                }
            }

        } catch (Exception e) {
            // graceful degradation
            response.setLoadDto(null);
            response.setVehicle(null);
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