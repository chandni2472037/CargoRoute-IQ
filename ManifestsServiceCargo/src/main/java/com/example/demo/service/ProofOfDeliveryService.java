package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.ProofOfDeliveryDTO;
import com.example.demo.dto.RequiredResponseDTO;
import com.example.demo.entities.enums.PodType;
import com.example.demo.entities.enums.ProofOfDeliveryStatus;

public interface ProofOfDeliveryService {

    ProofOfDeliveryDTO create(ProofOfDeliveryDTO proofOfDeliveryDTO);

    RequiredResponseDTO getById(Long podID);

    List<RequiredResponseDTO> getAll();

    RequiredResponseDTO getByBookingID(Long bookingID);

    List<RequiredResponseDTO> getByPodType(PodType podType);

    List<RequiredResponseDTO> getByProofOfDeliveryStatus(ProofOfDeliveryStatus status);

    ProofOfDeliveryDTO update(Long podID, ProofOfDeliveryDTO proofOfDeliveryDTO);

    void delete(Long podID);
}