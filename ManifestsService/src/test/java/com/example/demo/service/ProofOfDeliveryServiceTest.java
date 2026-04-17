package com.example.demo.service;


import com.example.demo.dto.BookingDTO;
import com.example.demo.dto.ProofOfDeliveryDTO;
import com.example.demo.dto.ProofOfDeliveryResponseDTO;
import com.example.demo.entities.ProofOfDelivery;
import com.example.demo.entities.enums.PodType;
import com.example.demo.entities.enums.ProofOfDeliveryStatus;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProofOfDeliveryRepository;
import com.example.demo.serviceimpl.ProofOfDeliveryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProofOfDeliveryServiceImplTest {

    @Mock
    private ProofOfDeliveryRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProofOfDeliveryServiceImpl podService;

    private ProofOfDelivery pod;
    private ProofOfDeliveryDTO podDTO;

    @BeforeEach
    void setUp() {

        pod = new ProofOfDelivery();
        pod.setPodID(1L);
        pod.setBookingID(10L);
        pod.setDeliveredAt(LocalDateTime.now());
        pod.setReceivedBy("Customer");
        pod.setPodURI("s3://pod/photo.jpg");
        pod.setPodType(PodType.Photo);
        pod.setStatus(ProofOfDeliveryStatus.UPLOADED);

        podDTO = new ProofOfDeliveryDTO();
        podDTO.setBookingID(10L);
        podDTO.setDeliveredAt(LocalDateTime.now());
        podDTO.setReceivedBy("Customer");
        podDTO.setPodURI("s3://pod/photo.jpg");
        podDTO.setPodType(PodType.Photo);
        podDTO.setStatus(ProofOfDeliveryStatus.UPLOADED);
    }

    // ── create ───────────────────────────────────────────────────────

    @Test
    void create_ShouldSaveAndReturnDTO() {

        when(repository.save(any(ProofOfDelivery.class)))
                .thenReturn(pod);

        ProofOfDeliveryDTO result =
                podService.create(podDTO);

        assertNotNull(result);
        assertEquals(10L, result.getBookingID());
        assertEquals(PodType.Photo, result.getPodType());
        verify(repository, times(1))
                .save(any(ProofOfDelivery.class));
    }

    @Test
    void create_ShouldThrowException_WhenBookingIDMissing() {

        ProofOfDeliveryDTO invalid = new ProofOfDeliveryDTO();

        assertThrows(IllegalArgumentException.class,
                () -> podService.create(invalid));
    }

    // ── getById ──────────────────────────────────────────────────────

    @Test
    void getById_ShouldReturnPod_WhenFound() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(pod));
        when(restTemplate.getForObject(any(String.class),
                eq(BookingDTO.class)))
                .thenReturn(new BookingDTO());

        ProofOfDeliveryResponseDTO result =
                podService.getById(1L);

        assertNotNull(result);
        assertEquals(1L,
                result.getProofOfDelivery().getPodID());
        assertNotNull(result.getBooking());
    }

    @Test
    void getById_ShouldThrowException_WhenNotFound() {

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> podService.getById(99L));
    }

    // ── getAll ───────────────────────────────────────────────────────

    @Test
    void getAll_ShouldReturnAllPods() {

        when(repository.findAll())
                .thenReturn(List.of(pod));
        when(restTemplate.getForObject(any(String.class),
                eq(BookingDTO.class)))
                .thenReturn(new BookingDTO());

        List<ProofOfDeliveryResponseDTO> result =
                podService.getAll();

        assertEquals(1, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void getAll_ShouldReturnEmptyList_WhenNoPods() {

        when(repository.findAll())
                .thenReturn(List.of());

        List<ProofOfDeliveryResponseDTO> result =
                podService.getAll();

        assertTrue(result.isEmpty());
    }

    // ── getByBookingID ──────────────────────────────────────────────

    @Test
    void getByBookingID_ShouldReturnPod() {

        when(repository.findByBookingID(10L))
                .thenReturn(pod);
        when(restTemplate.getForObject(any(String.class),
                eq(BookingDTO.class)))
                .thenReturn(new BookingDTO());

        ProofOfDeliveryResponseDTO result =
                podService.getByBookingID(10L);

        assertEquals(10L,
                result.getProofOfDelivery().getBookingID());
    }

    @Test
    void getByBookingID_ShouldThrowException_WhenNotFound() {

        when(repository.findByBookingID(10L))
                .thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> podService.getByBookingID(10L));
    }

    // ── getByPodType ────────────────────────────────────────────────

    @Test
    void getByPodType_ShouldReturnList() {

        when(repository.findByPodType(PodType.Photo))
                .thenReturn(List.of(pod));
        when(restTemplate.getForObject(any(String.class),
                eq(BookingDTO.class)))
                .thenReturn(new BookingDTO());

        List<ProofOfDeliveryResponseDTO> result =
                podService.getByPodType(PodType.Photo);

        assertEquals(1, result.size());
    }

    // ── getByStatus ─────────────────────────────────────────────────

    @Test
    void getByProofOfDeliveryStatus_ShouldReturnList() {

        when(repository.findByStatus(ProofOfDeliveryStatus.UPLOADED))
                .thenReturn(List.of(pod));
        when(restTemplate.getForObject(any(String.class),
                eq(BookingDTO.class)))
                .thenReturn(new BookingDTO());

        List<ProofOfDeliveryResponseDTO> result =
                podService.getByProofOfDeliveryStatus(
                        ProofOfDeliveryStatus.UPLOADED);

        assertEquals(1, result.size());
    }

    // ── update ───────────────────────────────────────────────────────

    @Test
    void update_ShouldModifyAndReturnDTO() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(pod));
        when(repository.save(any(ProofOfDelivery.class)))
                .thenReturn(pod);

        ProofOfDeliveryDTO updateDTO = new ProofOfDeliveryDTO();
        updateDTO.setStatus(ProofOfDeliveryStatus.VERIFIED);

        ProofOfDeliveryDTO result =
                podService.update(1L, updateDTO);

        assertEquals(ProofOfDeliveryStatus.VERIFIED,
                result.getStatus());
    }

    @Test
    void update_ShouldThrowException_WhenNotFound() {

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> podService.update(1L, podDTO));
    }

    // ── delete ───────────────────────────────────────────────────────

    @Test
    void delete_ShouldDeletePod() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(pod));

        assertDoesNotThrow(() -> podService.delete(1L));

        verify(repository, times(1))
                .delete(pod);
    }

    @Test
    void delete_ShouldThrowException_WhenNotFound() {

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> podService.delete(99L));
    }
}