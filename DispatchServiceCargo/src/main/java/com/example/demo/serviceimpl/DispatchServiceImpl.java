package com.example.demo.serviceimpl;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.DispatchDTO;
import com.example.demo.dto.LoadDTO;
import com.example.demo.dto.RequiredResponseDTO;
import com.example.demo.entities.Dispatch;
import com.example.demo.entities.enums.DispatchStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.repository.DispatchRepository;
import com.example.demo.service.DispatchService;

@Service
public class DispatchServiceImpl implements DispatchService {

    @Autowired
    private DispatchRepository dispatchRepository;

    @Autowired
    private RestTemplate restTemplate;

    //Entity to DTO
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

    //DTO → Entity
    private Dispatch convertToEntity(DispatchDTO dto) {
        Dispatch dispatch = new Dispatch();
        dispatch.setLoadID(dto.getLoadID());
        dispatch.setAssignedDriverID(dto.getAssignedDriverID());
        dispatch.setAssignedBy(dto.getAssignedBy());
        dispatch.setAssignedAt(dto.getAssignedAt());
        dispatch.setStatus(dto.getStatus());
        return dispatch;
    }

    //RestTemplate Call
    private RequiredResponseDTO buildResponse(Dispatch dispatch) {

        RequiredResponseDTO response = new RequiredResponseDTO();
        response.setDispatch(convertToDto(dispatch));

        LoadDTO load =
                restTemplate.getForObject(
                        "http://LOAD-SERVICE/loads/" + dispatch.getLoadID(),
                        LoadDTO.class);

        response.setLoad(load);
        return response;
    }

    //INSERT 
    @Override
    public DispatchDTO insert(DispatchDTO dispatchDTO) {

        if (dispatchDTO == null || dispatchDTO.getLoadID() == null) {
            throw new ValidationException("Load ID is required to create dispatch");
        }

        Dispatch saved =
                dispatchRepository.save(convertToEntity(dispatchDTO));

        return convertToDto(saved);
    }

    //FETCH BY DISPATCH ID
    @Override
    public RequiredResponseDTO fetchByID(Long dispatchID) {

        Dispatch dispatch =
                dispatchRepository.findById(dispatchID)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Dispatch not found with ID: " + dispatchID));

        return buildResponse(dispatch);
    }

    //FETCH BY LOAD ID
    @Override
    public RequiredResponseDTO fetchByLoadID(Long loadID) {

        Dispatch dispatch =
                dispatchRepository.findByLoadID(loadID);

        if (dispatch == null) {
            throw new ResourceNotFoundException(
                    "Dispatch not found for Load ID: " + loadID);
        }

        return buildResponse(dispatch);
    }

    //FETCH BY DRIVER ID 
    @Override
    public RequiredResponseDTO fetchByAssignedDriverID(Long driverID) {

        Dispatch dispatch =
                dispatchRepository.findByAssignedDriverID(driverID);

        if (dispatch == null) {
            throw new ResourceNotFoundException(
                    "Dispatch not found for Driver ID: " + driverID);
        }

        return buildResponse(dispatch);
    }

    //FETCH BY ASSIGNED BY 
    @Override
    public List<RequiredResponseDTO> fetchByAssignedBy(String assignedBy) {

        List<Dispatch> dispatches =
                dispatchRepository.findByAssignedBy(assignedBy);

        List<RequiredResponseDTO> result = new ArrayList<>();
        for (Dispatch dispatch : dispatches) {
            result.add(buildResponse(dispatch));
        }
        return result;
    }

    //FETCH BY STATUS
    @Override
    public List<RequiredResponseDTO> fetchByStatus(DispatchStatus status) {

        List<Dispatch> dispatches =
                dispatchRepository.findByStatus(status);

        List<RequiredResponseDTO> result = new ArrayList<>();
        for (Dispatch dispatch : dispatches) {
            result.add(buildResponse(dispatch));
        }
        return result;
    }

    //FETCH ALL 
    @Override
    public List<RequiredResponseDTO> fetchAll() {

        List<Dispatch> dispatches = dispatchRepository.findAll();

        if (dispatches.isEmpty()) {
            throw new ResourceNotFoundException("No dispatch records found");
        }

        List<RequiredResponseDTO> result = new ArrayList<>();
        for (Dispatch dispatch : dispatches) {
            result.add(buildResponse(dispatch));
        }
        return result;
    }

    //UPDATE 
    @Override
    public DispatchDTO updateDispatch(Long dispatchID, DispatchDTO dto) {

        Dispatch existing =
                dispatchRepository.findById(dispatchID)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Dispatch not found with ID: " + dispatchID));

        if (dto.getAssignedDriverID() != null)
            existing.setAssignedDriverID(dto.getAssignedDriverID());
        if (dto.getAssignedBy() != null)
            existing.setAssignedBy(dto.getAssignedBy());
        if (dto.getAssignedAt() != null)
            existing.setAssignedAt(dto.getAssignedAt());
        if (dto.getStatus() != null)
            existing.setStatus(dto.getStatus());

        return convertToDto(
                dispatchRepository.save(existing));
    }

    //DELETE 
    @Override
    public void delete(Long dispatchID) {

        Dispatch dispatch =
                dispatchRepository.findById(dispatchID)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Dispatch not found with ID: " + dispatchID));

        dispatchRepository.delete(dispatch);
    }
}