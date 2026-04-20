package com.example.demo.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ReportDTO;
import com.example.demo.entity.Report;
import com.example.demo.enums.ReportScope;
import com.example.demo.exception.ReportNotFoundException;
import com.example.demo.repo.ReportRepo;
import com.example.demo.service.ReportService;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepo repo;

    // ================= ENTITY → DTO =================
    private ReportDTO toDTO(Report entity) {
        if (entity == null) return null;

        ReportDTO dto = new ReportDTO();
        dto.setReportID(entity.getReportID());
        dto.setScope(entity.getScope().name());
        dto.setParametersJSON(entity.getParametersJSON());
        dto.setMetricsJSON(entity.getMetricsJSON());
        dto.setGeneratedBy(entity.getGeneratedBy());
        dto.setGeneratedAt(entity.getGeneratedAt() != null
                ? entity.getGeneratedAt().toString()
                : null);
        dto.setReportURI(entity.getReportURI());

        return dto;
    }

    // ================= DTO → ENTITY =================
    private Report toEntity(ReportDTO dto) {
        if (dto == null) return null;

        Report entity = new Report();
        entity.setReportID(dto.getReportID());
        entity.setScope(ReportScope.valueOf(dto.getScope()));
        entity.setParametersJSON(dto.getParametersJSON());
        entity.setMetricsJSON(dto.getMetricsJSON());
        entity.setGeneratedBy(dto.getGeneratedBy());
        entity.setGeneratedAt(LocalDateTime.now());
        entity.setReportURI(dto.getReportURI());

        return entity;
    }

    // ================= SAVE =================
    @Override
    public ReportDTO save(ReportDTO report) {
        Report saved = repo.save(toEntity(report));
        return toDTO(saved);
    }

    // ================= GET ALL =================
    @Override
    public List<ReportDTO> getAll() {
        List<Report> entities = repo.findAll();
        if (entities.isEmpty()) {
            throw new ReportNotFoundException("No reports found");
        }
        return entities.stream().map(this::toDTO).toList();
    }

    // ================= GET BY ID =================
    @Override
    public ReportDTO getById(Long id) {
        Report entity = repo.findById(id)
                .orElseThrow(() ->
                        new ReportNotFoundException("Report not found with id: " + id)
                );
        return toDTO(entity);
    }

    // ================= DELETE =================
    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ReportNotFoundException("Report not found with id: " + id);
        }
        repo.deleteById(id);
    }
}