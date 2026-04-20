package com.example.demo.serviceImpl;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.KPIDTO;
import com.example.demo.exception.KPINotFoundException;
import com.example.demo.entity.KPI;
import com.example.demo.repo.KPIRepo;
import com.example.demo.service.KPIService;

@Service
public class KPIServiceimpl implements KPIService {

    @Autowired
    private KPIRepo repo;

    private KPIDTO toDTO(KPI entity) {
        if (entity == null) return null;
        KPIDTO dto = new KPIDTO();
        dto.setKpiID(entity.getKPIID());
        dto.setName(entity.getName());
        dto.setDefinition(entity.getDefinition());
        dto.setTarget(entity.getTarget());
        dto.setCurrentValue(entity.getCurrentValue());
        dto.setReportingPeriod(entity.getReportingPeriod());
        return dto;
    }

    private KPI toEntity(KPIDTO dto) {
        if (dto == null) return null;
        KPI entity = new KPI();
        entity.setKPIID(dto.getKpiID());
        entity.setName(dto.getName());
        entity.setDefinition(dto.getDefinition());
        entity.setTarget(dto.getTarget());
        entity.setCurrentValue(dto.getCurrentValue());
        entity.setReportingPeriod(dto.getReportingPeriod());
        return entity;
    }

    @Override
    public KPIDTO save(KPIDTO kpi) {
        KPI saved = repo.save(toEntity(kpi));
        return toDTO(saved);
    }

    @Override
    public List<KPIDTO> getAll() {
        List<KPI> entities = repo.findAll();
        if (entities.isEmpty()) {
            throw new KPINotFoundException("No KPIs found");
        }
        return entities.stream().map(this::toDTO).toList();
    }

    @Override
    public KPIDTO getById(Long id) {
        KPI entity = repo.findById(id)
            .orElseThrow(() ->
                new KPINotFoundException("KPI not found with id: " + id)
            );
        return toDTO(entity);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new KPINotFoundException("KPI not found with id: " + id);
        }
        repo.deleteById(id);
    }
}