package com.example.demo.serviceimpl;

import com.example.demo.dto.BookingDetailsDTO;
import com.example.demo.dto.ExceptionRecordDTO;
import com.example.demo.dto.InternalUserDTO;
import com.example.demo.dto.RequiredResponseDTO;
import com.example.demo.entity.ExceptionRecord;
import com.example.demo.entity.enums.ExceptionStatus;
import com.example.demo.entity.enums.ExceptionType;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ExceptionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private InternalUserDTO internalUserDTO;

    private static final String TEST_EMAIL   = "test@user.com";
    private static final Long   TEST_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        record = new ExceptionRecord();
        record.setExceptionID(1L);
        record.setType(ExceptionType.DELAY);
        record.setReportedBy(TEST_USER_ID);
        record.setDescription("Cargo delayed at port");
        record.setStatus(ExceptionStatus.PENDING);
        record.setBookingId(1L);

        // reportedBy is READ_ONLY — not supplied by the client in the DTO
        dto = new ExceptionRecordDTO();
        dto.setExceptionID(1L);
        dto.setType(ExceptionType.DELAY);
        dto.setDescription("Cargo delayed at port");
        dto.setStatus(ExceptionStatus.PENDING);
        dto.setBookingId(1L);

        bookingDto = new BookingDetailsDTO();
        bookingDto.setBookingID(1L);
        bookingDto.setCommodity("Electronics");

        internalUserDTO = new InternalUserDTO();
        internalUserDTO.setUserID(TEST_USER_ID);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ── Auth helpers ─────────────────────────────────────────────────────────

    /**
     * Simulates a logged-in Shipper.
     * Credentials hold a token string (used by createException → resolveUserId).
     */
    private void setAuthAsShipper() {
        var auth = new UsernamePasswordAuthenticationToken(
                TEST_EMAIL, "dummy-token",
                List.of(new SimpleGrantedAuthority("ROLE_SHIPPER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * Simulates a logged-in Admin.
     * ExceptionServiceImpl checks role.equals("Admin") after stripping "ROLE_",
     * so the authority must be "ROLE_Admin".
     */
    private void setAuthAsAdmin() {
        var auth = new UsernamePasswordAuthenticationToken(
                TEST_EMAIL, "dummy-token",
                List.of(new SimpleGrantedAuthority("ROLE_Admin")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // ── createException ──────────────────────────────────────────────────────

    @Test
    void createException_ShouldSaveAndReturnDTO() {
        setAuthAsShipper();
        // IAM call: resolveUserId(email) → GET /internal/users/byEmail?email=… (template var is a String)
        when(restTemplate.getForObject(contains("byEmail"), eq(InternalUserDTO.class), any(String.class)))
                .thenReturn(internalUserDTO);
        // BookingService existence check → GET /getBookingById/{id} (template var is a Long)
        when(restTemplate.getForObject(contains("getBookingById"), eq(BookingDetailsDTO.class), any(Long.class)))
                .thenReturn(bookingDto);
        when(repo.save(any(ExceptionRecord.class))).thenReturn(record);

        ExceptionRecordDTO result = exceptionService.createException(dto);

        assertNotNull(result);
        assertEquals(1L, result.getExceptionID());
        assertEquals(ExceptionType.DELAY, result.getType());
        assertEquals(ExceptionStatus.PENDING, result.getStatus());
        // reportedBy must come from auth context, not from DTO
        assertEquals(TEST_USER_ID, result.getReportedBy());
        verify(repo, times(1)).save(any(ExceptionRecord.class));
    }

    @Test
    void createException_ShouldThrowBadRequest_WhenBookingIdIsNull() {
        // Validated before auth context is read — no SecurityContext setup needed
        dto.setBookingId(null);

        assertThrows(BadRequestException.class, () -> exceptionService.createException(dto));
        verify(repo, never()).save(any());
    }

    @Test
    void createException_ShouldThrowBadRequest_WhenTypeIsNull() {
        // Validated before auth context is read — no SecurityContext setup needed
        dto.setType(null);

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
    void getAllExceptions_AsAdmin_ShouldReturnAllRecords() {
        setAuthAsAdmin();
        when(repo.findAll()).thenReturn(List.of(record));
        when(repo.findById(1L)).thenReturn(Optional.of(record));
        when(restTemplate.getForObject(anyString(), eq(BookingDetailsDTO.class), any(Long.class)))
                .thenReturn(bookingDto);

        List<RequiredResponseDTO> result = exceptionService.getAllExceptions();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getExceptiondto().getExceptionID());
        verify(repo).findAll();
        verify(repo, never()).findByReportedBy(any());
    }

    @Test
    void getAllExceptions_AsAdmin_ShouldReturnEmptyList() {
        setAuthAsAdmin();
        when(repo.findAll()).thenReturn(List.of());

        List<RequiredResponseDTO> result = exceptionService.getAllExceptions();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllExceptions_AsShipper_ShouldReturnOwnExceptions() {
        setAuthAsShipper();
        // Non-admin path resolves userId from IAM (email is the template var — String)
        when(restTemplate.getForObject(contains("byEmail"), eq(InternalUserDTO.class), any(String.class)))
                .thenReturn(internalUserDTO);
        when(repo.findByReportedBy(TEST_USER_ID)).thenReturn(List.of(record));
        when(repo.findById(1L)).thenReturn(Optional.of(record));
        // BookingService call uses Long as template var
        when(restTemplate.getForObject(contains("getBookingById"), eq(BookingDetailsDTO.class), any(Long.class)))
                .thenReturn(bookingDto);

        List<RequiredResponseDTO> result = exceptionService.getAllExceptions();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repo).findByReportedBy(TEST_USER_ID);
        verify(repo, never()).findAll();
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
