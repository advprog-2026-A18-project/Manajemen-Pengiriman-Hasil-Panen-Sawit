package org.example.modul4menejemenpengirimansawit.model;
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
    private long mandorId;
    private long supirId;
    private List<Long> hasilPanen;
    private double totalBeratKg;
    private String status;
    private LocalDateTime tanggalPengiriman;
    private String statusPersetujuanMandor;
    private String statusPersetujuanAdmin;
    private String alasanPenolakan;
    private double beratDiakui;


}
