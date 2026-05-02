package org.example.modul4menejemenpengirimansawit.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PengirimanTest {

    @Test
    void testGetterAndSetter() {
        Pengiriman pengiriman = new Pengiriman();

        UUID id         = UUID.randomUUID();
        UUID mandorId   = UUID.randomUUID();
        UUID supirId    = UUID.randomUUID();
        List<UUID> hasilPanen = List.of(UUID.randomUUID(), UUID.randomUUID());
        LocalDateTime now = LocalDateTime.now();

        pengiriman.setId(id);
        pengiriman.setMandorId(mandorId);
        pengiriman.setSupirId(supirId);
        pengiriman.setHasilPanen(hasilPanen);
        pengiriman.setTotalBeratKg(450.5);
        pengiriman.setStatus("Memuat");
        pengiriman.setTanggalPengiriman(now);
        pengiriman.setStatusPersetujuanMandor("DISETUJUI");
        pengiriman.setStatusPersetujuanAdmin("PENDING");
        pengiriman.setAlasanPenolakan("Kapasitas penuh");
        pengiriman.setBeratDiakui(400.0);

        assertEquals(id,             pengiriman.getId());
        assertEquals(mandorId,       pengiriman.getMandorId());
        assertEquals(supirId,        pengiriman.getSupirId());
        assertEquals(hasilPanen,     pengiriman.getHasilPanen());
        assertEquals(450.5,          pengiriman.getTotalBeratKg());
        assertEquals("Memuat",       pengiriman.getStatus());
        assertEquals(now,            pengiriman.getTanggalPengiriman());
        assertEquals("DISETUJUI",    pengiriman.getStatusPersetujuanMandor());
        assertEquals("PENDING",      pengiriman.getStatusPersetujuanAdmin());
        assertEquals("Kapasitas penuh", pengiriman.getAlasanPenolakan());
        assertEquals(400.0,          pengiriman.getBeratDiakui());
    }

    @Test
    void testAllArgsConstructor() {
        UUID id         = UUID.randomUUID();
        UUID mandorId   = UUID.randomUUID();
        UUID supirId    = UUID.randomUUID();
        List<UUID> hasilPanen = List.of(UUID.randomUUID());
        LocalDateTime now = LocalDateTime.now();

        // UPDATE: alasanPenolakan sekarang nullable — boleh null di sini
        Pengiriman pengiriman = new Pengiriman(
                id, mandorId, supirId, hasilPanen,
                500.0, "Mengirim", now,
                "DISETUJUI", "DISETUJUI",
                null,   // alasanPenolakan nullable
                500.0);

        assertEquals(id,          pengiriman.getId());
        assertEquals(mandorId,    pengiriman.getMandorId());
        assertEquals(supirId,     pengiriman.getSupirId());
        assertEquals(hasilPanen,  pengiriman.getHasilPanen());
        assertEquals(500.0,       pengiriman.getTotalBeratKg());
        assertEquals("Mengirim",  pengiriman.getStatus());
        assertEquals(now,         pengiriman.getTanggalPengiriman());
        assertEquals("DISETUJUI", pengiriman.getStatusPersetujuanMandor());
        assertEquals("DISETUJUI", pengiriman.getStatusPersetujuanAdmin());
        assertNull(pengiriman.getAlasanPenolakan());   // nullable — null adalah valid
        assertEquals(500.0,       pengiriman.getBeratDiakui());
    }

    @Test
    void testBuilder() {
        UUID id         = UUID.randomUUID();
        UUID mandorId   = UUID.randomUUID();
        UUID supirId    = UUID.randomUUID();
        List<UUID> hasilPanen = List.of(UUID.randomUUID());
        LocalDateTime now = LocalDateTime.now();

        Pengiriman pengiriman = Pengiriman.builder()
                .id(id)
                .mandorId(mandorId)
                .supirId(supirId)
                .hasilPanen(hasilPanen)
                .totalBeratKg(350.0)
                .status("Tiba di Tujuan")
                .tanggalPengiriman(now)
                .statusPersetujuanMandor("DISETUJUI")
                .statusPersetujuanAdmin("DITOLAK")
                .alasanPenolakan("Kualitas sawit buruk")
                .beratDiakui(0.0)
                .build();

        assertEquals(id,          pengiriman.getId());
        assertEquals(mandorId,    pengiriman.getMandorId());
        assertEquals(supirId,     pengiriman.getSupirId());
        assertEquals(hasilPanen,  pengiriman.getHasilPanen());
        assertEquals(350.0,       pengiriman.getTotalBeratKg());
        assertEquals("Tiba di Tujuan",  pengiriman.getStatus());
        assertEquals(now,         pengiriman.getTanggalPengiriman());
        assertEquals("DISETUJUI", pengiriman.getStatusPersetujuanMandor());
        assertEquals("DITOLAK",   pengiriman.getStatusPersetujuanAdmin());
        assertEquals("Kualitas sawit buruk", pengiriman.getAlasanPenolakan());
        assertEquals(0.0,         pengiriman.getBeratDiakui());
    }

    @Test
    void testAlasanPenolakan_Nullable() {
        // UPDATE: alasanPenolakan kini nullable — verifikasi pengiriman normal boleh null
        Pengiriman pengiriman = Pengiriman.builder()
                .id(UUID.randomUUID())
                .mandorId(UUID.randomUUID())
                .supirId(UUID.randomUUID())
                .hasilPanen(List.of(UUID.randomUUID()))
                .totalBeratKg(100.0)
                .status("Memuat")
                .tanggalPengiriman(LocalDateTime.now())
                .statusPersetujuanMandor("PENDING")
                .statusPersetujuanAdmin("PENDING")
                .alasanPenolakan(null)  // default pada pengiriman baru
                .beratDiakui(0.0)
                .build();

        assertNull(pengiriman.getAlasanPenolakan());
    }
}
