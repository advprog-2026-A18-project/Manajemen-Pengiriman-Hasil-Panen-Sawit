package org.example.modul4menejemenpengirimansawit.model;

import org.example.modul4menejemenpengirimansawit.dto.external.*;
import org.example.modul4menejemenpengirimansawit.dto.request.*;
import org.example.modul4menejemenpengirimansawit.dto.response.PengirimanResponseDTO;
import org.example.modul4menejemenpengirimansawit.model.Pengiriman;
import org.example.modul4menejemenpengirimansawit.repository.PengirimanRepository;
import org.example.modul4menejemenpengirimansawit.service.PengirimanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PengirimanServiceTest {

    @Mock
    private PengirimanRepository pengirimanRepository;

    @Mock
    private EksternalIntegrationService eksternalService;

    @InjectMocks
    private PengirimanService pengirimanService;

    private UUID pengirimanId;
    private Pengiriman pengiriman;
    private List<Long> listPanenId;

    @BeforeEach
    void setUp() {
        pengirimanId = UUID.randomUUID();
        listPanenId = List.of(1L, 2L);

        pengiriman = Pengiriman.builder()
                .id(pengirimanId)
                .mandorId(10L)
                .supirId(20L)
                .hasilPanen(listPanenId)
                .totalBeratKg(300.0)
                .status("Memuat")
                .tanggalPengiriman(LocalDateTime.now())
                .statusPersetujuanMandor("PENDING")
                .statusPersetujuanAdmin("PENDING")
                .build();
    }

    @Test
    void testTugaskanSupir_Success() {
        CreatePengirimanRequestDTO request = new CreatePengirimanRequestDTO();
        request.setSupirId(20L);
        request.setHasilPanenId(listPanenId);

        List<PanenDTO> panenDetails = List.of(
                PanenDTO.builder().id(1L).kilogramSawit(150.0).build(),
                PanenDTO.builder().id(2L).kilogramSawit(150.0).build()
        );

        when(eksternalService.getPanenByIds(listPanenId)).thenReturn(panenDetails);
        when(pengirimanRepository.save(any(Pengiriman.class))).thenReturn(pengiriman);

        // Mock untuk konversi DTO (ternary operator coverage)
        when(eksternalService.getMandorById(10L)).thenReturn(UserDTO.builder().nama("Mandor A").build());
        when(eksternalService.getSupirById(20L)).thenReturn(null); // Test coverage: data supir null

        PengirimanResponseDTO response = pengirimanService.tugaskanSupir(request, 10L);

        assertNotNull(response);
        assertEquals(300.0, response.getTotalBeratKg());
        assertEquals("Data Supir Tidak Ditemukan", response.getNamaSupir());
        verify(pengirimanRepository).save(any());
    }

    @Test
    void testTugaskanSupir_ExceedCapacity() {
        CreatePengirimanRequestDTO request = new CreatePengirimanRequestDTO();
        request.setHasilPanenId(listPanenId);

        List<PanenDTO> heavyPanen = List.of(PanenDTO.builder().kilogramSawit(500.0).build());
        when(eksternalService.getPanenByIds(listPanenId)).thenReturn(heavyPanen);

        assertThrows(IllegalArgumentException.class, () -> pengirimanService.tugaskanSupir(request, 10L));
    }

    @Test
    void testUpdateStatus_Success() {
        UpdateStatusRequestDTO request = new UpdateStatusRequestDTO();
        request.setStatus("Mengirim");

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(pengirimanRepository.save(any())).thenReturn(pengiriman);

        PengirimanResponseDTO response = pengirimanService.updateStatusPengiriman(pengirimanId, request);
        assertEquals("Mengirim", response.getStatusPengiriman());
    }

    @Test
    void testUpdateStatus_InvalidStatus() {
        UpdateStatusRequestDTO request = new UpdateStatusRequestDTO();
        request.setStatus("Istirahat"); // Status ilegal

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        assertThrows(IllegalArgumentException.class, () -> pengirimanService.updateStatusPengiriman(pengirimanId, request));
    }

    @Test
    void testReviewByMandor_Approve() {
        ReviewMandorRequestDTO request = new ReviewMandorRequestDTO();
        request.setApproved(true);

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(pengirimanRepository.save(any())).thenReturn(pengiriman);

        PengirimanResponseDTO response = pengirimanService.reviewByMandor(pengirimanId, request);
        assertEquals("DISETUJUI", response.getStatusPersetujuanMandor());
    }

    @Test
    void testReviewByMandor_RejectNoReason() {
        ReviewMandorRequestDTO request = new ReviewMandorRequestDTO();
        request.setApproved(false);
        request.setAlasanPenolakan(""); // Kosong

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        assertThrows(IllegalArgumentException.class, () -> pengirimanService.reviewByMandor(pengirimanId, request));
    }

    @Test
    void testReviewByAdmin_PartialReject_Success() {
        ReviewAdminRequestDTO request = new ReviewAdminRequestDTO();
        request.setStatusAproval("Partial_Reject");
        request.setBeratdiAkuiKg(250.0);
        request.setAlasanPenolakan("Ada sawit busuk");

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(pengirimanRepository.save(any())).thenReturn(pengiriman);

        pengirimanService.reviewByAdmin(pengirimanId, request);
        verify(pengirimanRepository).save(argThat(p -> p.getBeratDiakui() == 250.0));
    }

    @Test
    void testReviewByAdmin_Approve() {
        ReviewAdminRequestDTO request = new ReviewAdminRequestDTO();
        request.setStatusAproval("Approve");

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(pengirimanRepository.save(any())).thenReturn(pengiriman);

        pengirimanService.reviewByAdmin(pengirimanId, request);
        verify(pengirimanRepository).save(argThat(p -> p.getBeratDiakui() == 300.0));
    }

    @Test
    void testGetDaftarPengiriman_WithFilters() {
        String dateStr = LocalDateTime.now().toLocalDate().toString();
        when(pengirimanRepository.findAll()).thenReturn(List.of(pengiriman));

        // Test filter by supirId and date
        List<PengirimanResponseDTO> result = pengirimanService.getDaftarPengiriman(null, 20L, dateStr);
        assertEquals(1, result.size());

        // Test filter mismatch
        List<PengirimanResponseDTO> emptyResult = pengirimanService.getDaftarPengiriman("Tiba di Tujuan", 99L, null);
        assertEquals(0, emptyResult.size());
    }

    @Test
    void testFindOrThrow_NotFound() {
        when(pengirimanRepository.findById(any())).thenReturn(Optional.empty());
        UpdateStatusRequestDTO request = new UpdateStatusRequestDTO();
        request.setStatus("Mengirim");

        assertThrows(IllegalArgumentException.class, () -> pengirimanService.updateStatusPengiriman(UUID.randomUUID(), request));
    }
}