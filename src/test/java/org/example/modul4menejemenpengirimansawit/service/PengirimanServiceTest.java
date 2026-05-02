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

    /**
     * Base pengiriman — status "Memuat", persetujuan mandor/admin masih PENDING.
     * Dipakai untuk tes yang TIDAK butuh status sudah "Tiba di Tujuan".
     */
    private Pengiriman pengiriman;

    /**
     * Pengiriman yang statusnya sudah "Tiba di Tujuan" — dipakai untuk tes reviewByMandor.
     */
    private Pengiriman pengirimanTiba;

    /**
     * Pengiriman yang statusPersetujuanMandor sudah "DISETUJUI" — dipakai untuk tes reviewByAdmin.
     */
    private Pengiriman pengirimanDisetujuiMandor;

    @BeforeEach
    void setUp() {
        pengirimanId = UUID.randomUUID();
        mandorId     = UUID.randomUUID();
        supirId      = UUID.randomUUID();
        panenId1     = UUID.randomUUID();
        panenId2     = UUID.randomUUID();
        listPanenId  = List.of(panenId1, panenId2);

        // Pengiriman dasar (status Memuat, semua PENDING)
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
                .alasanPenolakan(null)
                .beratDiakui(0.0)
                .build();

        // Pengiriman yang sudah "Tiba di Tujuan" — wajib untuk reviewByMandor
        pengirimanTiba = Pengiriman.builder()
                .id(pengirimanId)
                .mandorId(mandorId)
                .supirId(supirId)
                .hasilPanen(listPanenId)
                .totalBeratKg(300.0)
                .status("Tiba di Tujuan")          // <-- sudah tiba
                .tanggalPengiriman(LocalDateTime.now())
                .statusPersetujuanMandor("PENDING")
                .statusPersetujuanAdmin("PENDING")
                .alasanPenolakan(null)
                .beratDiakui(0.0)
                .build();

        // Pengiriman yang sudah disetujui Mandor — wajib untuk reviewByAdmin
        pengirimanDisetujuiMandor = Pengiriman.builder()
                .id(pengirimanId)
                .mandorId(mandorId)
                .supirId(supirId)
                .hasilPanen(listPanenId)
                .totalBeratKg(300.0)
                .status("Tiba di Tujuan")
                .tanggalPengiriman(LocalDateTime.now())
                .statusPersetujuanMandor("DISETUJUI")  // <-- sudah disetujui Mandor
                .statusPersetujuanAdmin("PENDING")
                .alasanPenolakan(null)
                .beratDiakui(0.0)
                .build();

        // Stub eksternal yang sering dipakai (lenient agar tidak UnnecessaryStubbingException)
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

    // =========================================================
    // tugaskanSupir
    // =========================================================

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
    void testTugaskanSupir_Success_Path() {
        CreatePengirimanRequestDTO request = new CreatePengirimanRequestDTO();
        request.setSupirId(supirId);
        request.setHasilPanenId(listPanenId);

        when(pengirimanRepository.save(any(Pengiriman.class))).thenReturn(pengiriman);

        PengirimanResponseDTO response = pengirimanService.tugaskanSupir(request, mandorId);

        assertNotNull(response);
        verify(pengirimanRepository).save(any());
    }

    @Test
    void testTugaskanSupir_ExceedCapacity() {
        CreatePengirimanRequestDTO request = new CreatePengirimanRequestDTO();
        request.setHasilPanenId(listPanenId);

        when(eksternalService.getPanenByIds(listPanenId))
                .thenReturn(List.of(PanenDTO.builder().kilogramSawit(500.0).build()));

        assertThrows(IllegalArgumentException.class,
                () -> pengirimanService.tugaskanSupir(request, mandorId));
    }

    // =========================================================
    // updateStatusPengiriman
    // UPDATE: method sekarang menerima supirId sebagai parameter ke-2
    //         dan memvalidasi kepemilikan + urutan status
    // =========================================================

    @Test
    void testUpdateStatus_Success() {
        // "Memuat" → "Mengirim" adalah langkah valid
        UpdateStatusRequestDTO request = new UpdateStatusRequestDTO();
        request.setStatus("Mengirim");

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(pengirimanRepository.save(any())).thenReturn(pengiriman);

        // UPDATE: tambah supirId (harus sama dengan pengiriman.supirId)
        PengirimanResponseDTO response =
                pengirimanService.updateStatusPengiriman(pengirimanId, supirId, request);

        assertEquals("Mengirim", response.getStatusPengiriman());
    }

    @Test
    void testUpdateStatus_InvalidStatus() {
        UpdateStatusRequestDTO request = new UpdateStatusRequestDTO();
        request.setStatus("Istirahat"); // Status tidak ada di STATUS_ORDER

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));

        // UPDATE: tambah supirId
        assertThrows(IllegalArgumentException.class,
                () -> pengirimanService.updateStatusPengiriman(pengirimanId, supirId, request));
    }

    @Test
    void testUpdateStatus_WrongOwner() {
        // Supir lain tidak boleh update status pengiriman ini
        UUID supirLain = UUID.randomUUID();
        UpdateStatusRequestDTO request = new UpdateStatusRequestDTO();
        request.setStatus("Mengirim");

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));

        assertThrows(IllegalArgumentException.class,
                () -> pengirimanService.updateStatusPengiriman(pengirimanId, supirLain, request));
    }

    @Test
    void testUpdateStatus_SkipStep_Throws() {
        // "Memuat" → "Tiba di Tujuan" melewati satu langkah — harus ditolak
        UpdateStatusRequestDTO request = new UpdateStatusRequestDTO();
        request.setStatus("Tiba di Tujuan");

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));

        assertThrows(IllegalArgumentException.class,
                () -> pengirimanService.updateStatusPengiriman(pengirimanId, supirId, request));
    }

    // =========================================================
    // reviewByMandor
    // UPDATE: method sekarang menerima mandorId sebagai parameter ke-2
    //         dan memvalidasi status = "Tiba di Tujuan"
    // =========================================================

    @Test
    void testReviewByMandor_Approve() {
        ReviewMandorRequestDTO request = new ReviewMandorRequestDTO();
        request.setApproved(true);

        // UPDATE: gunakan pengirimanTiba (status "Tiba di Tujuan")
        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengirimanTiba));
        when(pengirimanRepository.save(any())).thenReturn(pengirimanTiba);

        // UPDATE: tambah mandorId
        PengirimanResponseDTO response =
                pengirimanService.reviewByMandor(pengirimanId, mandorId, request);

        assertEquals("DISETUJUI", response.getStatusPersetujuanMandor());
    }

    @Test
    void testReviewByMandor_RejectSuccess() {
        ReviewMandorRequestDTO request = new ReviewMandorRequestDTO();
        request.setApproved(false);
        request.setAlasanPenolakan("Kualitas buah kurang baik");

        // UPDATE: gunakan pengirimanTiba
        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengirimanTiba));
        when(pengirimanRepository.save(any())).thenReturn(pengirimanTiba);

        // UPDATE: tambah mandorId
        PengirimanResponseDTO response =
                pengirimanService.reviewByMandor(pengirimanId, mandorId, request);

        assertEquals("DITOLAK", response.getStatusPersetujuanMandor());
        verify(pengirimanRepository).save(
                argThat(p -> "Kualitas buah kurang baik".equals(p.getAlasanPenolakan())));
    }

    @Test
    void testReviewByMandor_RejectNoReason() {
        ReviewMandorRequestDTO request = new ReviewMandorRequestDTO();
        request.setApproved(false);
        request.setAlasanPenolakan(""); // Kosong

        // UPDATE: gunakan pengirimanTiba
        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengirimanTiba));

        // UPDATE: tambah mandorId
        assertThrows(IllegalArgumentException.class,
                () -> pengirimanService.reviewByMandor(pengirimanId, mandorId, request));
    }

    @Test
    void testReviewByMandor_NotTibaDiTujuan_Throws() {
        // Pengiriman masih "Memuat" — Mandor belum boleh review
        ReviewMandorRequestDTO request = new ReviewMandorRequestDTO();
        request.setApproved(true);

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));

        assertThrows(IllegalStateException.class,
                () -> pengirimanService.reviewByMandor(pengirimanId, mandorId, request));
    }

    @Test
    void testReviewByMandor_WrongOwner_Throws() {
        UUID mandorLain = UUID.randomUUID();
        ReviewMandorRequestDTO request = new ReviewMandorRequestDTO();
        request.setApproved(true);

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengirimanTiba));

        assertThrows(IllegalArgumentException.class,
                () -> pengirimanService.reviewByMandor(pengirimanId, mandorLain, request));
    }

    // =========================================================
    // reviewByAdmin
    // UPDATE: Admin hanya boleh review jika statusPersetujuanMandor = "DISETUJUI"
    //         Gunakan pengirimanDisetujuiMandor di semua tes reviewByAdmin
    // =========================================================

    @Test
    void testReviewByAdmin_Approve() {
        ReviewAdminRequestDTO request = new ReviewAdminRequestDTO();
        request.setStatusAproval("Approve");

        // UPDATE: gunakan pengirimanDisetujuiMandor
        when(pengirimanRepository.findById(pengirimanId))
                .thenReturn(Optional.of(pengirimanDisetujuiMandor));
        when(pengirimanRepository.save(any())).thenReturn(pengirimanDisetujuiMandor);

        pengirimanService.reviewByAdmin(pengirimanId, request);

        verify(pengirimanRepository).save(
                argThat(p -> p.getBeratDiakui() == p.getTotalBeratKg()));
    }

    @Test
    void testReviewByAdmin_Approve_Path() {
        ReviewAdminRequestDTO request = new ReviewAdminRequestDTO();
        request.setStatusAproval("Approve");

        // UPDATE: gunakan pengirimanDisetujuiMandor
        when(pengirimanRepository.findById(pengirimanId))
                .thenReturn(Optional.of(pengirimanDisetujuiMandor));
        when(pengirimanRepository.save(any())).thenReturn(pengirimanDisetujuiMandor);

        pengirimanService.reviewByAdmin(pengirimanId, request);

        verify(pengirimanRepository).save(
                argThat(p -> p.getBeratDiakui() == p.getTotalBeratKg()));
    }

    @Test
    void testReviewByAdmin_PartialReject_Success() {
        ReviewAdminRequestDTO request = new ReviewAdminRequestDTO();
        request.setStatusAproval("Partial_Reject");
        request.setBeratdiAkuiKg(250.0);
        request.setAlasanPenolakan("Ada sawit busuk");

        // UPDATE: gunakan pengirimanDisetujuiMandor
        when(pengirimanRepository.findById(pengirimanId))
                .thenReturn(Optional.of(pengirimanDisetujuiMandor));
        when(pengirimanRepository.save(any())).thenReturn(pengirimanDisetujuiMandor);

        pengirimanService.reviewByAdmin(pengirimanId, request);

        verify(pengirimanRepository).save(argThat(p -> p.getBeratDiakui() == 250.0));
    }

    @Test
    void testReviewByAdmin_PartialReject_MissingFields() {
        ReviewAdminRequestDTO request = new ReviewAdminRequestDTO();
        request.setStatusAproval("Partial_Reject");
        // Berat dan alasan sengaja tidak diisi

        // UPDATE: gunakan pengirimanDisetujuiMandor
        when(pengirimanRepository.findById(pengirimanId))
                .thenReturn(Optional.of(pengirimanDisetujuiMandor));

        assertThrows(IllegalArgumentException.class,
                () -> pengirimanService.reviewByAdmin(pengirimanId, request));
    }

    @Test
    void testReviewByAdmin_RejectOnly() {
        ReviewAdminRequestDTO request = new ReviewAdminRequestDTO();
        request.setStatusAproval("Reject");
        request.setAlasanPenolakan("Dokumen tidak lengkap");

        // UPDATE: gunakan pengirimanDisetujuiMandor
        when(pengirimanRepository.findById(pengirimanId))
                .thenReturn(Optional.of(pengirimanDisetujuiMandor));
        when(pengirimanRepository.save(any())).thenReturn(pengirimanDisetujuiMandor);

        pengirimanService.reviewByAdmin(pengirimanId, request);

        verify(pengirimanRepository).save(
                argThat(p -> "Dokumen tidak lengkap".equals(p.getAlasanPenolakan())));
    }

    @Test
    void testReviewByAdmin_Reject_Path() {
        ReviewAdminRequestDTO request = new ReviewAdminRequestDTO();
        request.setStatusAproval("Reject");
        request.setAlasanPenolakan("Dokumen tidak valid");

        // UPDATE: gunakan pengirimanDisetujuiMandor
        when(pengirimanRepository.findById(pengirimanId))
                .thenReturn(Optional.of(pengirimanDisetujuiMandor));
        when(pengirimanRepository.save(any())).thenReturn(pengirimanDisetujuiMandor);

        pengirimanService.reviewByAdmin(pengirimanId, request);

        verify(pengirimanRepository).save(
                argThat(p -> "Dokumen tidak valid".equals(p.getAlasanPenolakan())));
    }

    @Test
    void testReviewByAdmin_MandorBelumSetujui_Throws() {
        // Admin tidak boleh review jika statusPersetujuanMandor masih PENDING
        ReviewAdminRequestDTO request = new ReviewAdminRequestDTO();
        request.setStatusAproval("Approve");

        // Gunakan pengiriman biasa (mandor PENDING)
        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));

        assertThrows(IllegalStateException.class,
                () -> pengirimanService.reviewByAdmin(pengirimanId, request));
    }

    // =========================================================
    // getDaftarPengiriman (endpoint umum)
    // UPDATE: parameter supirId bertipe UUID (bukan Long)
    // =========================================================

    @Test
    void testGetDaftarPengiriman_WithFilters() {
        String dateStr = LocalDateTime.now().toLocalDate().toString();
        when(pengirimanRepository.findAll()).thenReturn(List.of(pengiriman));

        // UPDATE: supirId = null agar tidak filter
        List<PengirimanResponseDTO> result =
                pengirimanService.getDaftarPengiriman(null, null, dateStr);
        assertEquals(1, result.size());

        List<PengirimanResponseDTO> emptyResult =
                pengirimanService.getDaftarPengiriman("Tiba di Tujuan", null, null);
        assertEquals(0, emptyResult.size());
    }

    @Test
    void testGetDaftarPengiriman_AllFilters() {
        String dateStr = pengiriman.getTanggalPengiriman().toLocalDate().toString();
        when(pengirimanRepository.findAll()).thenReturn(List.of(pengiriman));

        // UPDATE: supirId sebagai UUID
        List<PengirimanResponseDTO> result =
                pengirimanService.getDaftarPengiriman("Memuat", null, dateStr);

        assertEquals(1, result.size());
    }

    @Test
    void testGetDaftarPengiriman_FilterBySupirId() {
        when(pengirimanRepository.findAll()).thenReturn(List.of(pengiriman));

        // Filter dengan supirId yang cocok
        List<PengirimanResponseDTO> result =
                pengirimanService.getDaftarPengiriman(null, supirId, null);
        assertEquals(1, result.size());

        // Filter dengan supirId yang tidak cocok
        List<PengirimanResponseDTO> empty =
                pengirimanService.getDaftarPengiriman(null, UUID.randomUUID(), null);
        assertEquals(0, empty.size());
    }

    // =========================================================
    // findOrThrow — masih melempar IllegalArgumentException
    // =========================================================

    @Test
    void testFindOrThrow_NotFound() {
        when(pengirimanRepository.findById(any())).thenReturn(Optional.empty());
        UpdateStatusRequestDTO request = new UpdateStatusRequestDTO();
        request.setStatus("Mengirim");

        // UPDATE: tambah supirId
        assertThrows(IllegalArgumentException.class,
                () -> pengirimanService.updateStatusPengiriman(UUID.randomUUID(), supirId, request));
    }

    // =========================================================
    // convertToResponseDTO — null safety
    // =========================================================

    @Test
    void testConvertToResponseDTO_ExternalDataNotFound() {
        when(eksternalService.getMandorById(any())).thenReturn(null);
        when(eksternalService.getSupirById(any())).thenReturn(null);

        UpdateStatusRequestDTO request = new UpdateStatusRequestDTO();
        request.setStatus("Mengirim");

        when(pengirimanRepository.findById(pengirimanId)).thenReturn(Optional.of(pengiriman));
        when(pengirimanRepository.save(any())).thenReturn(pengiriman);

        // UPDATE: tambah supirId
        PengirimanResponseDTO response =
                pengirimanService.updateStatusPengiriman(pengirimanId, supirId, request);

        assertEquals("Data Mandor Tidak Ditemukan", response.getNamaMandor());
        assertEquals("Data Supir Tidak Ditemukan", response.getNamaSupir());
    }
}
