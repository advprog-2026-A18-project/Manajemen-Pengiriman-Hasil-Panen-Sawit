package org.example.modul4menejemenpengirimansawit.dto.response;

import org.example.modul4menejemenpengirimansawit.dto.external.PanenDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PengirimanResponseDTO {
    private UUID id;                         // fix: was 'Id' (uppercase) dengan @Id annotation salah
    private UUID mandorId;
    private String namaMandor;
    private UUID supirId;
    private String namaSupir;
    private List<PanenDTO> detailPanen;
    private Double totalBeratKg;
    private String statusPengiriman;
    private LocalDateTime tanggalPengiriman;
    private String statusPersetujuanMandor;
    private String statusPersetujuanAdmin;
    private String alasanPenolakan;          // nullable — hanya ada saat ditolak
    private Double beratDiakuiKg;            // hanya terisi saat partial reject atau approve
}
