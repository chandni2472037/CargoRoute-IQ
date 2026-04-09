package com.example.demo.dto;


public class ProofOfDeliveryResponseDTO {

    private ProofOfDeliveryDTO pod;
    private BookingDTO booking;
    
	public ProofOfDeliveryDTO getPodDto() {
		return pod;
	}
	public void setPodDto(ProofOfDeliveryDTO pod) {
		this.pod = pod;
	}
	public BookingDTO getBooking() {
		return booking;
	}
	public void setBooking(BookingDTO booking) {
		this.booking = booking;
	}  
}


   