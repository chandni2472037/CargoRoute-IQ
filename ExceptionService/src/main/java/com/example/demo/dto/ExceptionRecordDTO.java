package com.example.demo.dto;

import java.time.LocalDateTime;


import com.example.demo.entity.enums.ExceptionStatus;
import com.example.demo.entity.enums.ExceptionType;

public class ExceptionRecordDTO {

    private Long exceptionID;
    private ExceptionType type;
    private String reportedBy;
    private LocalDateTime reportedAt;
    private LocalDateTime updatedAt;
    private String description;
    private ExceptionStatus status;
    private Long bookingId;
    
    
    public Long getExceptionID() {
        return exceptionID;
    }

    public void setExceptionID(Long exceptionID) {
        this.exceptionID = exceptionID;
    }

    public ExceptionType getType() {
        return type;
    }

    public void setType(ExceptionType type) {
        this.type = type;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(LocalDateTime reportedAt) {
        this.reportedAt = reportedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExceptionStatus getStatus() {
        return status;
    }

    public void setStatus(ExceptionStatus status) {
        this.status = status;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

  
}
