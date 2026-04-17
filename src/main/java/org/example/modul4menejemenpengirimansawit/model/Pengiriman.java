package org.example.modul4menejemenpengirimansawit.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pengiriman {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID mandorId;

    @Column(nullable = false)
    private UUID supirId;

    @ElementCollection
    @CollectionTable(name = "pengiriman_hasil_panen", joinColumns = @JoinColumn(name = "pengiriman_id"))
    @Column(name = "hasil_panen_id", nullable = false)
    private List<UUID> hasilPanen;

    @Column(nullable = false)
    private double totalBeratKg;

    /**
     * Status pengiriman: Memuat | Mengirim | Tiba di Tujuan
     * Hanya bisa maju, tidak bisa mundur.
     */
    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime tanggalPengiriman;

    /**
     * PENDING | DISETUJUI | DITOLAK
     * Mandor hanya bisa review jika status = "Tiba di Tujuan"
     */
    @Column(nullable = false)
    private String statusPersetujuanMandor;

    /**
     * PENDING | DISETUJUI | DITOLAK | PARTIAL_DITOLAK
     * Admin hanya bisa review jika statusPersetujuanMandor = DISETUJUI
     */
    @Column(nullable = false)
    private String statusPersetujuanAdmin;

    /**
     * Nullable — hanya diisi saat penolakan
     */
    @Column(nullable = true)
    private String alasanPenolakan;

    /**
     * Kg yang diakui pabrik (diisi saat partial reject atau approve penuh)
     */
    @Column(nullable = false)
    private double beratDiakui;
}
