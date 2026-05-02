package org.example.modul4menejemenpengirimansawit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.modul4menejemenpengirimansawit.dto.request.*;
import org.example.modul4menejemenpengirimansawit.dto.response.PengirimanResponseDTO;
import org.example.modul4menejemenpengirimansawit.service.PengirimanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PengirimanController.class)
class PengirimanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PengirimanService pengirimanService;

    @Autowired
    private ObjectMapper objectMapper;

    // ----------------------------------------------------------
    // POST /api/pengiriman — Mandor menugaskan Supir
    // ----------------------------------------------------------
    @Test
    void testTugaskanSupir() throws Exception {
        UUID mandorId = UUID.randomUUID();
        CreatePengirimanRequestDTO request = new CreatePengirimanRequestDTO();
        request.setSupirId(UUID.randomUUID());
        request.setHasilPanenId(List.of(UUID.randomUUID()));

        PengirimanResponseDTO response = new PengirimanResponseDTO();
        when(pengirimanService.tugaskanSupir(any(), eq(mandorId))).thenReturn(response);

        mockMvc.perform(post("/api/pengiriman")
                        .param("mandorId", mandorId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ----------------------------------------------------------
    // PUT /api/pengiriman/{id}/status — Supir update status
    // UPDATE: supirId sekarang dikirim sebagai @RequestParam
    // UPDATE: mock pakai 3 argumen (id, supirId, request)
    // ----------------------------------------------------------
    @Test
    void testUpdateStatus() throws Exception {
        UUID id     = UUID.randomUUID();
        UUID supirId = UUID.randomUUID();
        UpdateStatusRequestDTO request = new UpdateStatusRequestDTO();
        request.setStatus("Mengirim");

        when(pengirimanService.updateStatusPengiriman(eq(id), eq(supirId), any()))
                .thenReturn(new PengirimanResponseDTO());

        mockMvc.perform(put("/api/pengiriman/" + id + "/status")
                        .param("supirId", supirId.toString())       // param baru
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ----------------------------------------------------------
    // PUT /api/pengiriman/{id}/review/mandor — Mandor approve/reject
    // UPDATE: mandorId sekarang dikirim sebagai @RequestParam
    // UPDATE: mock pakai 3 argumen (id, mandorId, request)
    // ----------------------------------------------------------
    @Test
    void testReviewMandor() throws Exception {
        UUID id      = UUID.randomUUID();
        UUID mandorId = UUID.randomUUID();
        ReviewMandorRequestDTO request = new ReviewMandorRequestDTO();
        request.setApproved(true);

        when(pengirimanService.reviewByMandor(eq(id), eq(mandorId), any()))
                .thenReturn(new PengirimanResponseDTO());

        mockMvc.perform(put("/api/pengiriman/" + id + "/review/mandor")
                        .param("mandorId", mandorId.toString())     // param baru
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ----------------------------------------------------------
    // PUT /api/pengiriman/{id}/review/admin — Admin approve/reject
    // Tidak berubah signature
    // ----------------------------------------------------------
    @Test
    void testReviewAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        ReviewAdminRequestDTO request = new ReviewAdminRequestDTO();
        request.setStatusAproval("Approve");

        when(pengirimanService.reviewByAdmin(eq(id), any()))
                .thenReturn(new PengirimanResponseDTO());

        mockMvc.perform(put("/api/pengiriman/" + id + "/review/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ----------------------------------------------------------
    // GET /api/pengiriman — list umum dengan filter
    // UPDATE: supirId sekarang bertipe UUID (bukan Long)
    // ----------------------------------------------------------
    @Test
    void testGetDaftarPengiriman() throws Exception {
        when(pengirimanService.getDaftarPengiriman(any(), any(), any()))
                .thenReturn(List.of(new PengirimanResponseDTO()));

        mockMvc.perform(get("/api/pengiriman")
                        .param("status", "Memuat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ----------------------------------------------------------
    // GET /api/pengiriman/supir — Supir lihat daftarnya sendiri (endpoint baru)
    // ----------------------------------------------------------
    @Test
    void testGetDaftarPengirimanSupir() throws Exception {
        UUID supirId = UUID.randomUUID();
        when(pengirimanService.getDaftarPengirimanSupir(eq(supirId), any()))
                .thenReturn(List.of(new PengirimanResponseDTO()));

        mockMvc.perform(get("/api/pengiriman/supir")
                        .param("supirId", supirId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ----------------------------------------------------------
    // GET /api/pengiriman/mandor — Mandor lihat pengiriman kebunnya (endpoint baru)
    // ----------------------------------------------------------
    @Test
    void testGetDaftarPengirimanMandor() throws Exception {
        UUID mandorId = UUID.randomUUID();
        when(pengirimanService.getDaftarPengirimanMandor(eq(mandorId), any()))
                .thenReturn(List.of(new PengirimanResponseDTO()));

        mockMvc.perform(get("/api/pengiriman/mandor")
                        .param("mandorId", mandorId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ----------------------------------------------------------
    // GET /api/pengiriman/admin/disetujui — Admin lihat yang sudah disetujui Mandor (endpoint baru)
    // ----------------------------------------------------------
    @Test
    void testGetDaftarPengirimanDisetujuiMandor() throws Exception {
        when(pengirimanService.getDaftarPengirimanDisetujuiMandor(any()))
                .thenReturn(List.of(new PengirimanResponseDTO(), new PengirimanResponseDTO()));

        mockMvc.perform(get("/api/pengiriman/admin/disetujui"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
