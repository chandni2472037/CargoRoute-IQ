package com.example.demo.dto; 
import java.time.LocalDateTime; 
 
public class DriverAckDTO { 
 
    private Long ackID; 	// Unique identifier for the acknowledgement record
    
    private DispatchDTO dispatch;

    private DriverDTO driver; 
 
    private LocalDateTime ackAt; 
 
    private String notes; 
 
    //Getters and setters
	public Long getAckID() {
		return ackID;
	}

	public void setAckID(Long ackID) {
		this.ackID = ackID;
	}

	public DispatchDTO getDispatch() {
		return dispatch;
	}

	public void setDispatch(DispatchDTO dispatch) {
		this.dispatch = dispatch;
	}

	public DriverDTO getDriver() {
		return driver;
	}

	public void setDriver(DriverDTO driver) {
		this.driver = driver;
	}

	public LocalDateTime getAckAt() {
		return ackAt;
	}

	public void setAckAt(LocalDateTime ackAt) {
		this.ackAt = ackAt;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
    
} 