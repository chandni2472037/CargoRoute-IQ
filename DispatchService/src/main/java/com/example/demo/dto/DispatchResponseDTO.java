package com.example.demo.dto;


public class DispatchResponseDTO {

    private DispatchDTO dispatch;
    private LoadDTO loadDto;
    private VehicleDTO vehicle; 
    
	public DispatchDTO getDispatch() {
		return dispatch;
	}
	public void setDispatch(DispatchDTO dispatch) {
		this.dispatch = dispatch;
	}
	public LoadDTO getLoadDto() {
		return loadDto;
	}
	public void setLoadDto(LoadDTO loadDto) {
		this.loadDto = loadDto;
	}
	public VehicleDTO getVehicle() {
		return vehicle;
	}
	public void setVehicle(VehicleDTO vehicle) {
		this.vehicle = vehicle;
	}
    
    

}
    