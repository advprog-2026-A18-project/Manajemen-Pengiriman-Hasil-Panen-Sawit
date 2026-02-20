package org.example.modul4menejemenpengirimansawit.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PengirimanTest {

    @Test
    void testSetDanGetTotalBeratKg() {
        // Persiapan (Arrange)
        Pengiriman pengiriman = new Pengiriman();
        Double beratEkspektasi = 350.5;

        // Aksi (Act)
        pengiriman.setTotalBeratKg(beratEkspektasi);

        // Pengecekan (Assert)
        assertEquals(beratEkspektasi, pengiriman.getTotalBeratKg(),
                "Fungsi getter dan setter untuk totalBeratKg gagal!");
    }
//testes
    @Test
    void testInisialisasiStatus() {
        // Persiapan (Arrange)
        Pengiriman pengiriman = new Pengiriman();

        // Aksi (Act)
        pengiriman.setStatus("Memuat");

        // Pengecekan (Assert)
        assertNotNull(pengiriman.getStatus(), "Status tidak boleh null setelah di-set");
        assertEquals("Memuat", pengiriman.getStatus(), "Status harus sesuai dengan yang di-set");
    }
}