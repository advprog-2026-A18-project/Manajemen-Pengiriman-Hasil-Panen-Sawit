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
            @RequestParam UUID mandorId) {
        PengirimanResponseDTO response = pengirimanService.tugaskanSupir(request, mandorId);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<PengirimanResponseDTO> updateStatusPengiriman(
            @PathVariable UUID id,
            @RequestBody UpdateStatusRequestDTO request) {
        PengirimanResponseDTO response = pengirimanService.updateStatusPengiriman(id, request);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}/review/mandor")
    public ResponseEntity<PengirimanResponseDTO> reviewPengirimanByMandor(
            @PathVariable UUID id,
            @RequestBody ReviewMandorRequestDTO request) {
        PengirimanResponseDTO response = pengirimanService.reviewByMandor(id, request);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}/review/admin")
    public ResponseEntity<PengirimanResponseDTO> reviewPengirimanByAdmin(
            @PathVariable UUID id,
            @RequestBody ReviewAdminRequestDTO request) {
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