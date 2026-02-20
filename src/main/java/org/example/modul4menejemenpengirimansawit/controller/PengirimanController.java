package org.example.modul4menejemenpengirimansawit.controller;

import org.example.modul4menejemenpengirimansawit.dto.request.*;
import org.example.modul4menejemenpengirimansawit.dto.response.PengirimanResponseDTO;
import org.example.modul4menejemenpengirimansawit.service.PengirimanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID; // Import UUID

@RestController
@RequestMapping("/api/pengiriman")
public class PengirimanController {

    private final PengirimanService pengirimanService;

    public PengirimanController(PengirimanService pengirimanService) {
        this.pengirimanService = pengirimanService;
    }

    @PostMapping
    public ResponseEntity<PengirimanResponseDTO> tugaskanSupir(
            @RequestBody CreatePengirimanRequestDTO request,
            @RequestParam Long mandorId) {
        PengirimanResponseDTO response = pengirimanService.tugaskanSupir(request, mandorId);
        return ResponseEntity.ok(response);
    }

    // Ubah @PathVariable dari Long menjadi UUID
    @PutMapping("/{id}/status")
    public ResponseEntity<PengirimanResponseDTO> updateStatusPengiriman(
            @PathVariable UUID id,
            @RequestBody UpdateStatusRequestDTO request) {
        PengirimanResponseDTO response = pengirimanService.updateStatusPengiriman(id, request);
        return ResponseEntity.ok(response);
    }

    // Review Mandor: Menyetujui atau menolak pengiriman [cite: 131, 132]
    @PutMapping("/{id}/review/mandor")
    public ResponseEntity<PengirimanResponseDTO> reviewPengirimanByMandor(
            @PathVariable UUID id,
            @RequestBody ReviewMandorRequestDTO request) {
        // Sesuaikan dengan nama method di service: reviewByMandor
        PengirimanResponseDTO response = pengirimanService.reviewByMandor(id, request);
        return ResponseEntity.ok(response);
    }

    // Review Admin: Bisa Approve, Reject, atau Partial Reject [cite: 139, 140, 141]
    @PutMapping("/{id}/review/admin")
    public ResponseEntity<PengirimanResponseDTO> reviewPengirimanByAdmin(
            @PathVariable UUID id,
            @RequestBody ReviewAdminRequestDTO request) {
        // Sesuaikan dengan nama method di service: reviewByAdmin
        PengirimanResponseDTO response = pengirimanService.reviewByAdmin(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PengirimanResponseDTO>> getDaftarPengiriman(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long supirId,
            @RequestParam(required = false) String tanggal) {
        List<PengirimanResponseDTO> response = pengirimanService.getDaftarPengiriman(status, supirId, tanggal);
        return ResponseEntity.ok(response);
    }
}