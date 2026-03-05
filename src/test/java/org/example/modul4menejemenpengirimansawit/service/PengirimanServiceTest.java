package org.example.modul4menejemenpengirimansawit.service;

import org.example.modul4menejemenpengirimansawit.dto.external.*;
import org.example.modul4menejemenpengirimansawit.dto.request.*;
import org.example.modul4menejemenpengirimansawit.dto.response.PengirimanResponseDTO;
import org.example.modul4menejemenpengirimansawit.model.Pengiriman;
import org.example.modul4menejemenpengirimansawit.repository.PengirimanRepository;
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
import static org.mockito.ArgumentMatchers.argThat;
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
    private UUID mandorId;
    private UUID supirId;
    private UUID panenId1;
    private UUID panenId2;
    private List<UUID> listPanenId;
    private Pengiriman pengiriman;

    @BeforeEach
    void setUp() {
        pengirimanId = UUID.randomUUID();
        mandorId = UUID.randomUUID();
        supirId = UUID.randomUUID();
        panenId1 = UUID.randomUUID();
        panenId2 = UUID.randomUUID();
        listPanenId = List.of(panenId1, panenId2);

        pengiriman = Pengiriman.builder()
                .id(pengirimanId)
                .mandorId(mandorId)
                .supirId(supirId)
                .hasilPanen(listPanenId)
                .totalBeratKg(300.0)
                .status("Memuat")
                .tanggalPengiriman(LocalDateTime.now())
                .statusPersetujuanMandor("PENDING")
                .statusPersetujuanAdmin("PENDING")
                .beratDiakui(300.0)
                .build();

        // Mencegah UnnecessaryStubbingException, kita gunakan lenient() untuk konversi DTO
        lenient().when(eksternalService.getMandorById(mandorId))
                .thenReturn(UserDTO.builder().id(mandorId).nama("Mandor A").build());
        lenient().when(eksternalService.getSupirById(supirId))
                .thenReturn(UserDTO.builder().id(supirId).nama("Supir A").build());
        lenient().when(eksternalService.getPanenByIds(listPanenId))
                .thenReturn(List.of(
                        PanenDTO.builder().id(panenId1).kilogramSawit(150.0).build(),
                        PanenDTO.builder().id(panenId2).kilogramSawit(150.0).build()
                ));
    }

    @Test
    void testTugaskanSupir_Success() {
        CreatePengirimanRequestDTO request = new CreatePengirimanRequestDTO();
        request.setSupirId(supirId);
        request.setHasilPanenId(listPanenId);

        when(pengirimanRepository.save(any(Pengiriman.class))).thenReturn(pengiriman);

        PengirimanResponseDTO response = pengirimanService.tugaskanSupir(request, mandorId);

        assertNotNull(response);
        assertEquals(300.0, response.getTotalBeratKg());
        assertEquals("Supir A", response.getNamaSupir());
        verify(pengirimanRepository).save(any());
    }

    @Test
    void testTugaskanSupir_ExceedCapacity() {
        CreatePengirimanRequestDTO request = new CreatePengirimanRequestDTO();
        request.setHasilPanenId(listPanenId);

        List<PanenDTO> heavyPanen = List.of(PanenDTO.builder().kilogramSawit(500.0).build());
        when(eksternalService.getPanenByIds(listPanenId)).thenReturn(heavyPanen);

        assertThrows(IllegalArgumentException.class, () -> pengirimanService.tugaskanSupir(request, mandorId));
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
        request.setStatus("Istirahat"); // Status tidak valid

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

        // Note: di PengirimanService.java kamu, method ini masih menerima parameter Long supirId,
        // sehingga kita operasikan "null" agar tidak bentrok dengan UUID
        List<PengirimanResponseDTO> result = pengirimanService.getDaftarPengiriman(null, null, dateStr);
        assertEquals(1, result.size());

        List<PengirimanResponseDTO> emptyResult = pengirimanService.getDaftarPengiriman("Tiba di Tujuan", null, null);
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