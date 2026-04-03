package com.example.demo.serviceimpl;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.DriverAckDTO;
import com.example.demo.dto.DispatchDTO;
import com.example.demo.dto.DriverDTO;
import com.example.demo.entities.DriverAck;
import com.example.demo.entities.Dispatch;
import com.example.demo.entities.Driver;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.repository.DriverAckRepository;
import com.example.demo.service.DriverAckService;

@Service
public class DriverAckServiceImpl implements DriverAckService {

    @Autowired
    private DriverAckRepository driverAckRepository;

    //Entity to DTO 
    private DriverAckDTO convertToDto(DriverAck entity) {

        DriverAckDTO dto = new DriverAckDTO();
        dto.setAckID(entity.getAckID());

        DispatchDTO dispatchDTO = new DispatchDTO();
        dispatchDTO.setDispatchID(entity.getDispatch().getDispatchID());
        dto.setDispatch(dispatchDTO);

        DriverDTO driverDTO = new DriverDTO();
        driverDTO.setDriverID(entity.getDriver().getDriverID());
        dto.setDriver(driverDTO);

        dto.setAckAt(entity.getAckAt());
        dto.setNotes(entity.getNotes());

        return dto;
    }

    //DTO → Entity
    private DriverAck convertToEntity(DriverAckDTO dto) {

        DriverAck entity = new DriverAck();

        Dispatch dispatch = new Dispatch();
        dispatch.setDispatchID(dto.getDispatch().getDispatchID());

        Driver driver = new Driver();
        driver.setDriverID(dto.getDriver().getDriverID());

        entity.setDispatch(dispatch);
        entity.setDriver(driver);
        entity.setAckAt(dto.getAckAt());
        entity.setNotes(dto.getNotes());

        return entity;
    }

    //CREATE
    @Override
    public DriverAckDTO insert(DriverAckDTO driverAckDTO) {

        if (driverAckDTO == null ||
            driverAckDTO.getDispatch() == null ||
            driverAckDTO.getDriver() == null) {
            throw new ValidationException("Dispatch and Driver details are required");
        }

        DriverAck saved =
                driverAckRepository.save(convertToEntity(driverAckDTO));

        return convertToDto(saved);
    }

    //FETCH BY ACK ID 
    @Override
    public DriverAckDTO fetchByID(Long ackID) {

        DriverAck ack = driverAckRepository.findById(ackID)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Driver Acknowledgement not found with ID: " + ackID));

        return convertToDto(ack);
    }

    //FETCH BY DISPATCH ID
    @Override
    public DriverAckDTO fetchByDispatchID(Long dispatchID) {

        DriverAck ack =
                driverAckRepository.findByDispatch_DispatchID(dispatchID);

        if (ack == null) {
            throw new ResourceNotFoundException(
                    "Acknowledgement not found for Dispatch ID: " + dispatchID);
        }

        return convertToDto(ack);
    }

    //FETCH BY DRIVER ID 
    @Override
    public DriverAckDTO fetchByDriverID(Long driverID) {

        DriverAck ack =
                driverAckRepository.findByDriver_DriverID(driverID);

        if (ack == null) {
            throw new ResourceNotFoundException(
                    "Acknowledgement not found for Driver ID: " + driverID);
        }

        return convertToDto(ack);
    }

    //FETCH ALL
    @Override
    public List<DriverAckDTO> fetchAll() {

        List<DriverAck> ackList = driverAckRepository.findAll();
        List<DriverAckDTO> dtoList = new ArrayList<>();

        for (DriverAck ack : ackList) {
            dtoList.add(convertToDto(ack));
        }

        return dtoList;
    }

    //UPDATE 
    @Override
    public DriverAckDTO updateDriverAck(Long ackID, DriverAckDTO dto) {

        DriverAck existing = driverAckRepository.findById(ackID)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Driver Acknowledgement not found with ID: " + ackID));

        if (dto.getAckAt() != null)
            existing.setAckAt(dto.getAckAt());

        if (dto.getNotes() != null)
            existing.setNotes(dto.getNotes());

        return convertToDto(
                driverAckRepository.save(existing));
    }

    //DELETE 
    @Override
    public void delete(Long ackID) {

        DriverAck ack = driverAckRepository.findById(ackID)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Driver Acknowledgement not found with ID: " + ackID));

        driverAckRepository.delete(ack);
    }
}