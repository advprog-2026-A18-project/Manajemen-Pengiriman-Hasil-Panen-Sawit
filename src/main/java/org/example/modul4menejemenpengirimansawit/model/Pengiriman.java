package org.example.modul4menejemenpengirimansawit.model;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pengiriman {
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
