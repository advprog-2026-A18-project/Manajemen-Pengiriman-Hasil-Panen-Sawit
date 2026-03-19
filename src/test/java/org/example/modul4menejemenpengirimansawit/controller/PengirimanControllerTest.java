package org.example.modul4menejemenpengirimansawit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.modul4menejemenpengirimansawit.dto.request.*;
import org.example.modul4menejemenpengirimansawit.dto.response.PengirimanResponseDTO;
import org.example.modul4menejemenpengirimansawit.service.PengirimanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
// IMPORT BARU: Menggantikan org.springframework.boot.test.mock.mockito.MockBean
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

    @MockitoBean // Pakai ini sekarang, bukan @MockBean
    private PengirimanService pengirimanService;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    void testUpdateStatus() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateStatusRequestDTO request = new UpdateStatusRequestDTO();
        request.setStatus("Mengirim");

        when(pengirimanService.updateStatusPengiriman(eq(id), any())).thenReturn(new PengirimanResponseDTO());

        mockMvc.perform(put("/api/pengiriman/" + id + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testReviewMandor() throws Exception {
        UUID id = UUID.randomUUID();
        ReviewMandorRequestDTO request = new ReviewMandorRequestDTO();
        request.setApproved(true);

        when(pengirimanService.reviewByMandor(eq(id), any())).thenReturn(new PengirimanResponseDTO());

        mockMvc.perform(put("/api/pengiriman/" + id + "/review/mandor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testReviewAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        ReviewAdminRequestDTO request = new ReviewAdminRequestDTO();
        request.setStatusAproval("Approve");

        when(pengirimanService.reviewByAdmin(eq(id), any())).thenReturn(new PengirimanResponseDTO());

        mockMvc.perform(put("/api/pengiriman/" + id + "/review/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetDaftarPengiriman() throws Exception {
        when(pengirimanService.getDaftarPengiriman(any(), any(), any())).thenReturn(List.of(new PengirimanResponseDTO()));

        mockMvc.perform(get("/api/pengiriman")
                        .param("status", "Memuat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}