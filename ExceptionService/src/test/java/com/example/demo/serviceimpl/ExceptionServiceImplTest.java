package com.example.demo.serviceimpl;

import com.example.demo.dto.BookingDetailsDTO;
import com.example.demo.dto.ExceptionRecordDTO;
import com.example.demo.dto.RequiredResponseDTO;
import com.example.demo.entity.ExceptionRecord;
import com.example.demo.entity.enums.ExceptionStatus;
import com.example.demo.entity.enums.ExceptionType;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ExceptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExceptionServiceImplTest {

    @Mock
    private ExceptionRepository repo;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ExceptionServiceImpl exceptionService;

    private ExceptionRecord record;
    private ExceptionRecordDTO dto;
    private BookingDetailsDTO bookingDto;

    @BeforeEach
    void setUp() {
        record = new ExceptionRecord();
        record.setExceptionID(1L);
        record.setType(ExceptionType.DELAY);
        record.setReportedBy("John");
        record.setDescription("Cargo delayed at port");
        record.setStatus(ExceptionStatus.PENDING);
        record.setBookingId(1L);

        dto = new ExceptionRecordDTO();
        dto.setExceptionID(1L);
        dto.setType(ExceptionType.DELAY);
        dto.setReportedBy("John");
        dto.setDescription("Cargo delayed at port");
        dto.setStatus(ExceptionStatus.PENDING);
        dto.setBookingId(1L);

        bookingDto = new BookingDetailsDTO();
        bookingDto.setBookingID(1L);
        bookingDto.setCommodity("Electronics");
    }

    // ── createException ──────────────────────────────────────────────────────

    @Test
    void createException_ShouldSaveAndReturnDTO() {
        when(restTemplate.getForObject(anyString(), eq(BookingDetailsDTO.class), any(Long.class)))
                .thenReturn(bookingDto);
        when(repo.save(any(ExceptionRecord.class))).thenReturn(record);

        ExceptionRecordDTO result = exceptionService.createException(dto);

        assertNotNull(result);
        assertEquals(1L, result.getExceptionID());
        assertEquals(ExceptionType.DELAY, result.getType());
        assertEquals(ExceptionStatus.PENDING, result.getStatus());
        verify(repo, times(1)).save(any(ExceptionRecord.class));
    }

    @Test
    void createException_ShouldThrowBadRequest_WhenBookingIdIsNull() {
        dto.setBookingId(null);

        assertThrows(BadRequestException.class, () -> exceptionService.createException(dto));
        verify(repo, never()).save(any());
    }

    // ── getExceptionById ─────────────────────────────────────────────────────

    @Test
    void getExceptionById_ShouldReturnWithBookingDetails() {
        when(repo.findById(1L)).thenReturn(Optional.of(record));
        when(restTemplate.getForObject(anyString(), eq(BookingDetailsDTO.class), any(Long.class)))
                .thenReturn(bookingDto);

        RequiredResponseDTO result = exceptionService.getExceptionById(1L);

        assertNotNull(result);
        assertNotNull(result.getExceptiondto());
        assertEquals(1L, result.getExceptiondto().getExceptionID());
        assertNotNull(result.getBookingdto());
        assertEquals(1L, result.getBookingdto().getBookingID());
    }

    @Test
    void getExceptionById_ShouldThrowWhenNotFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> exceptionService.getExceptionById(99L));
    }

    // ── getAllExceptions ──────────────────────────────────────────────────────

    @Test
    void getAllExceptions_ShouldReturnList() {
        when(repo.findAll()).thenReturn(List.of(record));
        when(repo.findById(1L)).thenReturn(Optional.of(record));
        when(restTemplate.getForObject(anyString(), eq(BookingDetailsDTO.class), any(Long.class)))
                .thenReturn(bookingDto);

        List<RequiredResponseDTO> result = exceptionService.getAllExceptions();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getExceptiondto().getExceptionID());
    }

    @Test
    void getAllExceptions_ShouldReturnEmptyList() {
        when(repo.findAll()).thenReturn(List.of());

        List<RequiredResponseDTO> result = exceptionService.getAllExceptions();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ── updateExceptionStatus ─────────────────────────────────────────────────

    @Test
    void updateExceptionStatus_ShouldUpdateAndReturn() {
        ExceptionRecord updated = new ExceptionRecord();
        updated.setExceptionID(1L);
        updated.setStatus(ExceptionStatus.RESOLVED);
        updated.setBookingId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(record));
        when(repo.save(any(ExceptionRecord.class))).thenReturn(updated);

        ExceptionRecordDTO result = exceptionService.updateExceptionStatus(1L, ExceptionStatus.RESOLVED);

        assertNotNull(result);
        assertEquals(ExceptionStatus.RESOLVED, result.getStatus());
        verify(repo).save(any(ExceptionRecord.class));
    }

    @Test
    void updateExceptionStatus_ShouldThrowWhenNotFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> exceptionService.updateExceptionStatus(99L, ExceptionStatus.RESOLVED));
    }

    // ── getExceptionByBookingId ───────────────────────────────────────────────

    @Test
    void getExceptionByBookingId_ShouldReturnList() {
        when(repo.findByBookingId(1L)).thenReturn(List.of(record));
        when(repo.findById(1L)).thenReturn(Optional.of(record));
        when(restTemplate.getForObject(anyString(), eq(BookingDetailsDTO.class), any(Long.class)))
                .thenReturn(bookingDto);

        List<RequiredResponseDTO> result = exceptionService.getExceptionByBookingId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getExceptionByBookingId_ShouldThrowWhenNoExceptions() {
        when(repo.findByBookingId(99L)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> exceptionService.getExceptionByBookingId(99L));
    }

    // ── getExceptionByStatus ──────────────────────────────────────────────────

    @Test
    void getExceptionByStatus_ShouldReturnList() {
        when(repo.findByStatus(ExceptionStatus.PENDING)).thenReturn(List.of(record));
        when(repo.findById(1L)).thenReturn(Optional.of(record));
        when(restTemplate.getForObject(anyString(), eq(BookingDetailsDTO.class), any(Long.class)))
                .thenReturn(bookingDto);

        List<RequiredResponseDTO> result = exceptionService.getExceptionByStatus(ExceptionStatus.PENDING);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getExceptionByStatus_ShouldReturnEmptyList() {
        when(repo.findByStatus(ExceptionStatus.REJECTED)).thenReturn(List.of());

        List<RequiredResponseDTO> result = exceptionService.getExceptionByStatus(ExceptionStatus.REJECTED);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
