package com.example.demo.servicesImplementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.DTO.*;
import com.example.demo.entities.Notification;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repositories.NotificationRepository;
import com.example.demo.services.NotificationService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final String USER_SERVICE = "Identity-Access-Management";

    private final NotificationRepository repo;
    private final RestTemplate restTemplate;

    public NotificationServiceImpl(NotificationRepository repo,
                                   RestTemplate restTemplate) {
        this.repo = repo;
        this.restTemplate = restTemplate;
    }

//    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "fallbackUser")
    @Override
    public NotificationDTO create(NotificationDTO dto) {

        // 1. Validate user existence via USER-SERVICE
    	Boolean exists = restTemplate.getForObject(
    		    "http://Identity-Access-Management/internal/users/" + dto.getUserID(),
    		    Boolean.class
    		);

    		if (exists == null || !exists) {
    		    throw new ResourceNotFoundException(
    		        "User not found with id: " + dto.getUserID()
    		    );
    		}

        // 2. Proceed only if user exists
        Notification n = new Notification();
        n.setUserID(dto.getUserID());
        n.setEntityID(dto.getEntityID());
        n.setMessage(dto.getMessage());
        n.setCategory(dto.getCategory());
        n.setStatus("UNREAD");

        return mapToDTO(repo.save(n));
    }

    @Override
    public NotificationDTO getById(Long id) {
        return mapToDTO(repo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Notification not found: " + id)));
    }

    @Override
    public List<NotificationDTO> getAll() {

        List<NotificationDTO> list = new ArrayList<>();
        for (Notification n : repo.findAll()) {
            list.add(mapToDTO(n));
        }
        return list;
    }

    //  API COMPOSITION (LIKE RequiredResponseDto)
    @Override
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "fallbackUser")
    public NotificationResponseDTO getNotificationWithUser(Long id) {

        NotificationDTO notification = getById(id);

        InternalUserDTO user = restTemplate.getForObject(
        	    "http://Identity-Access-Management/internal/users/" + notification.getUserID(),
        	    InternalUserDTO.class
        	);

        NotificationResponseDTO response = new NotificationResponseDTO();
        response.setNotification(notification);
        response.setUser(user);
        return response;
    }

    // ✅ FALLBACK METHOD
    public NotificationResponseDTO fallbackUser(Long id, Exception ex) {

        NotificationResponseDTO response = new NotificationResponseDTO();
        response.setNotification(getById(id));
        response.setUser(null); // graceful degradation
        return response;
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Notification not found: " + id);
        }
        repo.deleteById(id);
    }

    private NotificationDTO mapToDTO(Notification n) {

        NotificationDTO dto = new NotificationDTO();
        dto.setNotificationID(n.getNotificationID());
        dto.setUserID(n.getUserID());
        dto.setEntityID(n.getEntityID());
        dto.setMessage(n.getMessage());
        dto.setCategory(n.getCategory());
        dto.setStatus(n.getStatus());
        dto.setCreatedAt(n.getCreatedAt());

        return dto;
    }
}