package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.BookingDTO;
import com.example.demo.dto.ProofOfDeliveryDTO;
import com.example.demo.dto.ProofOfDeliveryResponseDTO;
import com.example.demo.entities.ProofOfDelivery;
import com.example.demo.entities.enums.PodType;
import com.example.demo.entities.enums.ProofOfDeliveryStatus;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProofOfDeliveryRepository;
import com.example.demo.serviceimpl.ProofOfDeliveryServiceImpl;

@ExtendWith(MockitoExtension.class)
class ProofOfDeliveryServiceImplTest {

    @Mock
    private ProofOfDeliveryRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProofOfDeliveryServiceImpl service;

    private ProofOfDelivery pod;
    private ProofOfDeliveryDTO podDTO;
    private BookingDTO bookingDTO;

    @BeforeEach
    void setUp() {
        pod = new ProofOfDelivery();
        pod.setPodID(1L);
        pod.setBookingID(100L);
        pod.setReceivedBy("John Doe");
        pod.setPodType(PodType.Signature);
        pod.setStatus(ProofOfDeliveryStatus.PENDING);

        podDTO = new ProofOfDeliveryDTO();
        podDTO.setBookingID(100L);
        podDTO.setPodType(PodType.Signature);
        podDTO.setStatus(ProofOfDeliveryStatus.PENDING);

        bookingDTO = new BookingDTO();
        // Assume BookingDTO has basic fields
    }

    // ================= CREATE TESTS =================

    @Test
    void create_Success() {
        when(repository.save(any(ProofOfDelivery.class))).thenReturn(pod);

        ProofOfDeliveryDTO result = service.create(podDTO);

        assertNotNull(result);
        assertEquals(1L, result.getPodID());
        verify(repository).save(any(ProofOfDelivery.class));
    }

    @Test
    void create_ThrowsBadRequest_WhenBookingIdMissing() {
        podDTO.setBookingID(null);
        assertThrows(BadRequestException.class, () -> service.create(podDTO));
    }

    // ================= GET BY ID & FALLBACK TESTS =================

    @Test
    void getById_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(pod));
        when(restTemplate.getForObject(anyString(), eq(BookingDTO.class))).thenReturn(bookingDTO);

        ProofOfDeliveryResponseDTO result = service.getById(1L);

        assertNotNull(result);
        assertNotNull(result.getBooking());
        assertEquals(1L, result.getProofOfDelivery().getPodID());
    }

    @Test
    void getByIdFallback_ReturnsPartialResponse() {
        // Mocking the behavior inside the fallback
        when(repository.findById(1L)).thenReturn(Optional.of(pod));
        
        ProofOfDeliveryResponseDTO result = service.getByIdFallback(1L, new RuntimeException("Service Down"));

        assertNotNull(result);
        assertNull(result.getBooking()); // Fallback logic check
        assertEquals("John Doe", result.getProofOfDelivery().getReceivedBy());
    }

    // ================= ENUM-BASED SEARCH TESTS =================

    @Test
    void getByPodType_Success() {
        when(repository.findByPodType(PodType.Signature)).thenReturn(Arrays.asList(pod));
        when(restTemplate.getForObject(anyString(), eq(BookingDTO.class))).thenReturn(bookingDTO);

        List<ProofOfDeliveryResponseDTO> results = service.getByPodType(PodType.Signature);

        assertEquals(1, results.size());
        verify(repository).findByPodType(PodType.Signature);
    }

    @Test
    void getByPodType_NotFound_ThrowsException() {
        when(repository.findByPodType(PodType.Photo)).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> service.getByPodType(PodType.Photo));
    }

    @Test
    void getByStatusFallback_ManualTrigger() {
        when(repository.findByStatus(ProofOfDeliveryStatus.VERIFIED)).thenReturn(Arrays.asList(pod));

        List<ProofOfDeliveryResponseDTO> results = service.getByProofOfDeliveryStatusFallback(
                ProofOfDeliveryStatus.VERIFIED, new RuntimeException());

        assertFalse(results.isEmpty());
        assertNull(results.get(0).getBooking()); // Logic check for fallback
    }

    // ================= UPDATE & DELETE =================

    @Test
    void update_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(pod));
        when(repository.save(any(ProofOfDelivery.class))).thenReturn(pod);

        podDTO.setReceivedBy("Jane Doe");
        ProofOfDeliveryDTO result = service.update(1L, podDTO);

        assertEquals("Jane Doe", result.getReceivedBy());
        verify(repository).save(pod);
    }

    @Test
    void delete_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(pod));
        doNothing().when(repository).delete(pod);

        service.delete(1L);

        verify(repository).delete(pod);
    }
}