package org.example.modul4menejemenpengirimansawit.controller;

import org.example.modul4menejemenpengirimansawit.dto.request.CreatePengirimanRequestDTO;
import org.example.modul4menejemenpengirimansawit.dto.request.ReviewAdminRequestDTO;
import org.example.modul4menejemenpengirimansawit.dto.request.ReviewMandorRequestDTO;
import org.example.modul4menejemenpengirimansawit.dto.request.UpdateStatusRequestDTO;
import org.example.modul4menejemenpengirimansawit.dto.response.PengirimanResponseDTO;
import org.example.modul4menejemenpengirimansawit.service.PengirimanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/{id}/status")
    public ResponseEntity<PengirimanResponseDTO> updateStatusPengiriman(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequestDTO request) {
        PengirimanResponseDTO response = pengirimanService.updateStatusPengiriman(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/review/mandor")
    public ResponseEntity<PengirimanResponseDTO> reviewPengirimanByMandor(
            @PathVariable Long id,
            @RequestBody ReviewMandorRequestDTO request) {
        PengirimanResponseDTO response = pengirimanService.reviewPengirimanByMandor(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/review/admin")
    public ResponseEntity<PengirimanResponseDTO> reviewPengirimanByAdmin(
            @PathVariable Long id,
            @RequestBody ReviewAdminRequestDTO request) {
        PengirimanResponseDTO response = pengirimanService.reviewPengirimanByAdmin(id, request);
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