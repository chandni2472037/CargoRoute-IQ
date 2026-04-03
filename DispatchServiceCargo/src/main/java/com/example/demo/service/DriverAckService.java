package com.example.demo.service;

import com.example.demo.dto.DriverAckDTO;
import java.util.List;

public interface DriverAckService {

	//Create driver Acknowledgment
    DriverAckDTO insert(DriverAckDTO driverAckDTO);

    //Fetch by id
    DriverAckDTO fetchByID(Long ackID);

    //Fetch by dispatch id
    DriverAckDTO fetchByDispatchID(Long dispatchID);

    //Fetch by driver id
    DriverAckDTO fetchByDriverID(Long driverID);

    //Fetch total data
    List<DriverAckDTO> fetchAll();

    //Update driver Acknowledgement
    DriverAckDTO updateDriverAck(Long ackID, DriverAckDTO driverAckDTO);

    //Delete
    void delete(Long ackID);
}
