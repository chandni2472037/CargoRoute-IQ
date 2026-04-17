package com.example.demo.serviceimpl;
import java.util.ArrayList;
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
 
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
 
import com.example.demo.dto.BookingDTO;
import com.example.demo.dto.ProofOfDeliveryDTO;
import com.example.demo.dto.ProofOfDeliveryResponseDTO;
import com.example.demo.entities.ProofOfDelivery;
import com.example.demo.entities.enums.PodType;
import com.example.demo.entities.enums.ProofOfDeliveryStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProofOfDeliveryRepository;
import com.example.demo.service.ProofOfDeliveryService;
 
@Service
public class ProofOfDeliveryServiceImpl implements ProofOfDeliveryService {
 
    private static final String POD_CB = "podService";
 
    @Autowired
    private ProofOfDeliveryRepository repository;
 
    @Autowired
    private RestTemplate restTemplate;
 
    private static final String BOOKING_SERVICE_URL =
            "http://BOOKING-SERVICE/cargoRoute/booking/getBooking/";
 
    //Create proof of delivery
    @Override
    public ProofOfDeliveryDTO create(ProofOfDeliveryDTO dto) {
 
        if (dto.getBookingID() == null) {
            throw new IllegalArgumentException("bookingID is mandatory");
        }
 
        ProofOfDelivery saved =
                repository.save(convertToEntity(dto));
 
        return convertToDTO(saved);
    }
 
    //Fetch by proof of delivery ID
    @Override
    @CircuitBreaker(name = POD_CB, fallbackMethod = "getByIdFallback")
    public ProofOfDeliveryResponseDTO getById(Long podID) {
        return buildResponse(findPod(podID));
    }
 
    public ProofOfDeliveryResponseDTO getByIdFallback(Long podID, Throwable t) {
        System.out.println("Fallback executed: unable to fetch full POD response for ID = " + podID);
        ProofOfDeliveryResponseDTO response = new ProofOfDeliveryResponseDTO();
        response.setProofOfDelivery(convertToDTO(findPod(podID)));
        response.setBooking(null);
        return response;
    }
 
    //Fetch all proof of deliveries
    @Override
    @CircuitBreaker(name = POD_CB, fallbackMethod = "getAllFallback")
    public List<ProofOfDeliveryResponseDTO> getAll() {
        List<ProofOfDeliveryResponseDTO> responses = new ArrayList<>();
        for (ProofOfDelivery pod : repository.findAll()) {
            responses.add(buildResponse(pod));
        }
        return responses;
    }
 
    public List<ProofOfDeliveryResponseDTO> getAllFallback(Throwable t) {
        System.out.println("Fallback executed: unable to fetch full POD list, returning partial data");
        List<ProofOfDeliveryResponseDTO> responses = new ArrayList<>();
        for (ProofOfDelivery pod : repository.findAll()) {
            ProofOfDeliveryResponseDTO response = new ProofOfDeliveryResponseDTO();
            response.setProofOfDelivery(convertToDTO(pod));
            response.setBooking(null);
            responses.add(response);
        }
        return responses;
    }
 
    //Fetch by booking ID
    @Override
    @CircuitBreaker(name = POD_CB, fallbackMethod = "getByBookingIDFallback")
    public ProofOfDeliveryResponseDTO getByBookingID(Long bookingID) {
 
        ProofOfDelivery pod = repository.findByBookingID(bookingID);
 
        if (pod == null) {
            throw new ResourceNotFoundException(
                    "POD not found for bookingID: " + bookingID);
        }
        return buildResponse(pod);
    }
 
    public ProofOfDeliveryResponseDTO getByBookingIDFallback(Long bookingID, Throwable t) {
        System.out.println("Fallback executed: unable to fetch full POD response for bookingID = " + bookingID);
        ProofOfDelivery pod = repository.findByBookingID(bookingID);
        if (pod == null) return null;
        ProofOfDeliveryResponseDTO response = new ProofOfDeliveryResponseDTO();
        response.setProofOfDelivery(convertToDTO(pod));
        response.setBooking(null);
        return response;
    }
 
    //Fetch by proof of delivery type
    @Override
    @CircuitBreaker(name = POD_CB, fallbackMethod = "getByPodTypeFallback")
    public List<ProofOfDeliveryResponseDTO> getByPodType(PodType podType) {
        List<ProofOfDeliveryResponseDTO> responses = new ArrayList<>();
        for (ProofOfDelivery pod : repository.findByPodType(podType)) {
            responses.add(buildResponse(pod));
        }
        return responses;
    }
 
    public List<ProofOfDeliveryResponseDTO> getByPodTypeFallback(PodType podType, Throwable t) {
        System.out.println("Fallback executed: unable to fetch full POD response for podType = " + podType);
        List<ProofOfDeliveryResponseDTO> responses = new ArrayList<>();
        for (ProofOfDelivery pod : repository.findByPodType(podType)) {
            ProofOfDeliveryResponseDTO response = new ProofOfDeliveryResponseDTO();
            response.setProofOfDelivery(convertToDTO(pod));
            response.setBooking(null);
            responses.add(response);
        }
        return responses;
    }
 
    //Fetch by proof of delivery status
    @Override
    @CircuitBreaker(name = POD_CB, fallbackMethod = "getByProofOfDeliveryStatusFallback")
    public List<ProofOfDeliveryResponseDTO> getByProofOfDeliveryStatus(
            ProofOfDeliveryStatus status) {
 
        List<ProofOfDeliveryResponseDTO> responses = new ArrayList<>();
        for (ProofOfDelivery pod : repository.findByStatus(status)) {
            responses.add(buildResponse(pod));
        }
        return responses;
    }
 
    public List<ProofOfDeliveryResponseDTO> getByProofOfDeliveryStatusFallback(ProofOfDeliveryStatus status, Throwable t) {
        System.out.println("Fallback executed: unable to fetch full POD response for status = " + status);
        List<ProofOfDeliveryResponseDTO> responses = new ArrayList<>();
        for (ProofOfDelivery pod : repository.findByStatus(status)) {
            ProofOfDeliveryResponseDTO response = new ProofOfDeliveryResponseDTO();
            response.setProofOfDelivery(convertToDTO(pod));
            response.setBooking(null);
            responses.add(response);
        }
        return responses;
    }
 
    //Update proof of delivery
 
    @Override
    public ProofOfDeliveryDTO update(Long podID, ProofOfDeliveryDTO dto) {
 
        ProofOfDelivery pod = findPod(podID);
 
        if (dto.getDeliveredAt() != null)
            pod.setDeliveredAt(dto.getDeliveredAt());
 
        if (dto.getReceivedBy() != null)
            pod.setReceivedBy(dto.getReceivedBy());
 
        if (dto.getPodURI() != null)
            pod.setPodURI(dto.getPodURI());
 
        if (dto.getPodType() != null)
            pod.setPodType(dto.getPodType());
 
        if (dto.getStatus() != null)
            pod.setStatus(dto.getStatus());
 
        return convertToDTO(repository.save(pod));
    }
 
    //Delete proof of delivery
    @Override
    public void delete(Long podID) {
        repository.delete(findPod(podID));
    }
 
    //Circuit breaker
    @CircuitBreaker(name = "bookingService", fallbackMethod = "bookingFallback")
    private BookingDTO callBookingService(Long bookingID) {
        return restTemplate.getForObject(
                BOOKING_SERVICE_URL + bookingID,
                BookingDTO.class);
    }
 
    private BookingDTO bookingFallback(Long bookingID, Exception ex) {
        System.out.println(
                "Fallback executed: BOOKING service unavailable for bookingID = "
                        + bookingID);
        return null;
    }
 
    //Response builder
    private ProofOfDeliveryResponseDTO buildResponse(ProofOfDelivery pod) {
 
        ProofOfDeliveryResponseDTO response =
                new ProofOfDeliveryResponseDTO();
 
        response.setProofOfDelivery(convertToDTO(pod));
 
        BookingDTO booking =
                callBookingService(pod.getBookingID());
 
        response.setBooking(booking);
        return response;
    }
 
 
    private ProofOfDelivery findPod(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "POD not found with ID: " + id));
    }
 
    //Entity to DTO
 
    private ProofOfDeliveryDTO convertToDTO(ProofOfDelivery pod) {
 
        ProofOfDeliveryDTO dto = new ProofOfDeliveryDTO();
        dto.setPodID(pod.getPodID());
        dto.setBookingID(pod.getBookingID());
        dto.setDeliveredAt(pod.getDeliveredAt());
        dto.setReceivedBy(pod.getReceivedBy());
        dto.setPodURI(pod.getPodURI());
        dto.setPodType(pod.getPodType());
        dto.setStatus(pod.getStatus());
        return dto;
    }
 
    //DTO to Entity
 
    private ProofOfDelivery convertToEntity(ProofOfDeliveryDTO dto) {
 
        ProofOfDelivery pod = new ProofOfDelivery();
        pod.setBookingID(dto.getBookingID());
        pod.setDeliveredAt(dto.getDeliveredAt());
        pod.setReceivedBy(dto.getReceivedBy());
        pod.setPodURI(dto.getPodURI());
        pod.setPodType(dto.getPodType());
        pod.setStatus(dto.getStatus());
        return pod;
    }
}