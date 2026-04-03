package com.example.demo.service;
import com.example.demo.dto.DispatchDTO;
import com.example.demo.dto.RequiredResponseDTO;
import com.example.demo.entities.enums.DispatchStatus;

import java.util.List;

public interface DispatchService {

	//Create a new dispatch
    public DispatchDTO insert(DispatchDTO dispatchDTO);

    //Get dispatch by dispatch ID
    public RequiredResponseDTO fetchByID(Long dispatchID);

    //Get dispatches by load ID
    public RequiredResponseDTO fetchByLoadID(Long loadID);

    //Get dispatches by assigned driver ID
    public RequiredResponseDTO fetchByAssignedDriverID(Long driverID);

    //Get dispatches by assigned by (dispatcher name)
    public List<RequiredResponseDTO> fetchByAssignedBy(String assignedBy);

    //Get dispatches by status
    public List<RequiredResponseDTO> fetchByStatus(DispatchStatus status);

    //Get all dispatches
    public List<RequiredResponseDTO> fetchAll();

    //Update dispatch
    public DispatchDTO updateDispatch(Long dispatchID, DispatchDTO dispatchDTO);

    //Delete dispatch by ID
    public void delete(Long dispatchID);
}
