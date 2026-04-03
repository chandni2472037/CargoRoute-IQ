package com.example.demo.dto;
import java.time.LocalDateTime;
import com.example.demo.entities.enums.VehicleStatus;
import com.example.demo.entities.enums.VehicleType;

public class VehicleDTO {

    private Long vehicleID;

    private String regNumber;

    private VehicleType type; 

    private Double maxWeightKg;
    
    private Double maxVolumeM3;

    private DriverDTO driver; 

    private VehicleStatus status; 

    private LocalDateTime lastMaintenanceAt;

    //Getters and setters
	public Long getVehicleID() {
		return vehicleID;
	}

	public void setVehicleID(Long vehicleID) {
		this.vehicleID = vehicleID;
	}

	public String getRegNumber() {
		return regNumber;
	}

	public void setRegNumber(String regNumber) {
		this.regNumber = regNumber;
	}

	public VehicleType getType() {
		return type;
	}

	public void setType(VehicleType type) {
		this.type = type;
	}

	public Double getMaxWeightKg() {
		return maxWeightKg;
	}

	public void setMaxWeightKg(Double maxWeightKg) {
		this.maxWeightKg = maxWeightKg;
	}

	public Double getMaxVolumeM3() {
		return maxVolumeM3;
	}

	public void setMaxVolumeM3(Double maxVolumeM3) {
		this.maxVolumeM3 = maxVolumeM3;
	}

	public DriverDTO getDriver() {
		return driver;
	}

	public void setDriver(DriverDTO driver) {
		this.driver = driver;
	}

	public VehicleStatus getStatus() {
		return status;
	}

	public void setStatus(VehicleStatus status) {
		this.status = status;
	}

	public LocalDateTime getLastMaintenanceAt() {
		return lastMaintenanceAt;
	}

	public void setLastMaintenanceAt(LocalDateTime lastMaintenanceAt) {
		this.lastMaintenanceAt = lastMaintenanceAt;
	}
    
    
}