package org.example.modul4menejemenpengirimansawit.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

    @Column(nullable = false)
    private List<UUID> hasilPanen;

    @Column(nullable = false)
    private double totalBeratKg;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime tanggalPengiriman;

    @Column(nullable = false)
    private String statusPersetujuanMandor;

    @Column(nullable = false)
    private String statusPersetujuanAdmin;

    @Column(nullable = false)
    private String alasanPenolakan;

    @Column(nullable = false)
    private double beratDiakui;


}
