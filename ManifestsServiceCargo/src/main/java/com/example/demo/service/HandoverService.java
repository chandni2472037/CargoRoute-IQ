package com.example.demo.service;

import java.util.List;
import com.example.demo.dto.HandoverDTO;

public interface HandoverService {

    HandoverDTO create(HandoverDTO handoverDTO);

    HandoverDTO getById(Long handoverID);

    List<HandoverDTO> getAll();

    HandoverDTO findByManifestID(Long manifestID);

    List<HandoverDTO> findByHandedBy(String handedBy);  

    HandoverDTO update(HandoverDTO handoverDTO);

    void delete(Long handoverID);
}