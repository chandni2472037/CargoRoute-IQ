package com.example.demo.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.demo.DTO.NotificationDTO;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class NotificationClient {

    private static final Logger logger = LoggerFactory.getLogger(NotificationClient.class);

    private final RestTemplate restTemplate;

    public NotificationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean notifyUser(Long userId, Long entityId, String message, String category) {
        NotificationDTO dto = new NotificationDTO();
        dto.setUserID(userId);
        dto.setEntityID(entityId);
        dto.setMessage(message);
        dto.setCategory(category);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String auth = resolveAuthorizationHeader();
            if (auth != null) headers.set("Authorization", auth);
            restTemplate.postForObject(
                "http://NOTIFICATIONS-AND-TASK-LISTS/cargoRoute/internal/notifications/create",
                new HttpEntity<>(dto, headers),
                NotificationDTO.class
            );
            logger.info("Notification published for userId={}, entityId={}, category={}", userId, entityId, category);
            return true;
        } catch (HttpStatusCodeException ex) {
            logger.warn(
                "Notification publish failed with status={} for userId={}, entityId={}, responseBody={}",
                ex.getStatusCode(), userId, entityId, ex.getResponseBodyAsString(), ex
            );
            return false;
        } catch (ResourceAccessException ex) {
            logger.warn(
                "Notification service unreachable for userId={}, entityId={}: {}",
                userId, entityId, ex.getMessage(), ex
            );
            return false;
        } catch (Exception ex) {
            logger.warn(
                "Unexpected notification publish error for userId={}, entityId={}",
                userId, entityId, ex
            );
            return false;
        }
    }

    private String resolveAuthorizationHeader() {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        if (ra instanceof ServletRequestAttributes sra) {
            return sra.getRequest().getHeader("Authorization");
        }
        return null;
    }
}
