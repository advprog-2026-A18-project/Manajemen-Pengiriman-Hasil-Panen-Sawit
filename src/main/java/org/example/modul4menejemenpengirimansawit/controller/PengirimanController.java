package org.example.modul4menejemenpengirimansawit.controller;

import org.example.modul4menejemenpengirimansawit.dto.request.*;
import org.example.modul4menejemenpengirimansawit.dto.external.UserDTO;
import org.example.modul4menejemenpengirimansawit.dto.response.PengirimanResponseDTO;
import org.example.modul4menejemenpengirimansawit.service.PengirimanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pengiriman")
public class PengirimanController {

    private final PengirimanService pengirimanService;

    public PengirimanController(PengirimanService pengirimanService) {
        this.pengirimanService = pengirimanService;
    }

    // ----------------------------------------------------------
    // MANDOR: Tugaskan Supir untuk mengangkut hasil panen
    // POST /api/pengiriman?mandorId=<UUID>
    // ----------------------------------------------------------
    @PostMapping
    public ResponseEntity<PengirimanResponseDTO> tugaskanSupir(
            @RequestBody CreatePengirimanRequestDTO request,
            @RequestParam UUID mandorId) {
        return ResponseEntity.ok(pengirimanService.tugaskanSupir(request, mandorId));
    }

    // ----------------------------------------------------------
    // UMUM: Lihat daftar pengiriman dengan filter opsional
    // GET /api/pengiriman?status=Memuat&supirId=<UUID>&tanggal=yyyy-MM-dd
    // ----------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<PengirimanResponseDTO>> getDaftarPengiriman(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID supirId,
            @RequestParam(required = false) String tanggal) {
        return ResponseEntity.ok(pengirimanService.getDaftarPengiriman(status, supirId, tanggal));
    }

    // ----------------------------------------------------------
    // SUPIR: Update status pengiriman (Memuat → Mengirim → Tiba di Tujuan)
    // PUT /api/pengiriman/{id}/status?supirId=<UUID>
    // ----------------------------------------------------------
    @PutMapping("/{id}/status")
    public ResponseEntity<PengirimanResponseDTO> updateStatusPengiriman(
            @PathVariable UUID id,
            @RequestParam UUID supirId,
            @RequestBody UpdateStatusRequestDTO request) {
        return ResponseEntity.ok(pengirimanService.updateStatusPengiriman(id, supirId, request));
    }

    // ----------------------------------------------------------
    // SUPIR: Lihat daftar pengiriman milik sendiri (filter tanggal)
    // GET /api/pengiriman/supir?supirId=<UUID>&tanggal=yyyy-MM-dd
    // ----------------------------------------------------------
    @GetMapping("/supir")
    public ResponseEntity<List<PengirimanResponseDTO>> getDaftarPengirimanSupir(
            @RequestParam UUID supirId,
            @RequestParam(required = false) String tanggal) {
        return ResponseEntity.ok(pengirimanService.getDaftarPengirimanSupir(supirId, tanggal));
    }

    // ----------------------------------------------------------
    // MANDOR: Lihat daftar pengiriman di kebunnya (filter status)
    // GET /api/pengiriman/mandor?mandorId=<UUID>&status=Mengirim
    // ----------------------------------------------------------
    @GetMapping("/mandor")
    public ResponseEntity<List<PengirimanResponseDTO>> getDaftarPengirimanMandor(
            @RequestParam UUID mandorId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(pengirimanService.getDaftarPengirimanMandor(mandorId, status));
    }

    // ----------------------------------------------------------
    // MANDOR: Lihat pengiriman spesifik seorang Supir (profil Supir)
    // GET /api/pengiriman/mandor/{mandorId}/supir/{supirId}
    // ----------------------------------------------------------
    @GetMapping("/mandor/{mandorId}/supir/{supirId}")
    public ResponseEntity<List<PengirimanResponseDTO>> getDaftarPengirimanSupirByMandor(
            @PathVariable UUID mandorId,
            @PathVariable UUID supirId) {
        return ResponseEntity.ok(
            pengirimanService.getDaftarPengirimanSupirByMandor(mandorId, supirId));
    }

    // ----------------------------------------------------------
    // MANDOR: Lihat daftar Supir pada kebun yang sama (filter nama)
    // GET /api/pengiriman/mandor/{mandorId}/supir?searchNama=...
    // ----------------------------------------------------------
    @GetMapping("/mandor/{mandorId}/supir")
    public ResponseEntity<List<UserDTO>> getDaftarSupirSatuKebun(
            @PathVariable UUID mandorId,
            @RequestParam(required = false) String searchNama) {
        return ResponseEntity.ok(
                pengirimanService.getDaftarSupirSatuKebun(mandorId, searchNama));
    }

    // ----------------------------------------------------------
    // MANDOR: Approve atau Reject pengiriman setelah "Tiba di Tujuan"
    // PUT /api/pengiriman/{id}/review/mandor?mandorId=<UUID>
    // ----------------------------------------------------------
    @PutMapping("/{id}/review/mandor")
    public ResponseEntity<PengirimanResponseDTO> reviewPengirimanByMandor(
            @PathVariable UUID id,
            @RequestParam UUID mandorId,
            @RequestBody ReviewMandorRequestDTO request) {
        return ResponseEntity.ok(pengirimanService.reviewByMandor(id, mandorId, request));
    }

    // ----------------------------------------------------------
    // ADMIN: Lihat daftar pengiriman yang sudah disetujui Mandor
    // GET /api/pengiriman/admin/disetujui?tanggal=yyyy-MM-dd
    // ----------------------------------------------------------
    @GetMapping("/admin/disetujui")
    public ResponseEntity<List<PengirimanResponseDTO>> getDaftarPengirimanDisetujuiMandor(
            @RequestParam(required = false) String tanggal,
            @RequestParam(required = false) String searchNamaMandor) {
        return ResponseEntity.ok(
                pengirimanService.getDaftarPengirimanDisetujuiMandor(tanggal, searchNamaMandor));
    }

    // ----------------------------------------------------------
    // ADMIN: Approve / Reject / Partial Reject pengiriman
    // PUT /api/pengiriman/{id}/review/admin
    // ----------------------------------------------------------
    @PutMapping("/{id}/review/admin")
    public ResponseEntity<PengirimanResponseDTO> reviewPengirimanByAdmin(
            @PathVariable UUID id,
            @RequestBody ReviewAdminRequestDTO request) {
        return ResponseEntity.ok(pengirimanService.reviewByAdmin(id, request));
    }
}
