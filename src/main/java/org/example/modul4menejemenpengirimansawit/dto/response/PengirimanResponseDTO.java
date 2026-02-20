package org.example.modul4menejemenpengirimansawit.dto.response;

import jakarta.persistence.Id;
import org.example.modul4menejemenpengirimansawit.dto.external.PanenDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.*;
@Getter
@Setter
public class PengirimanResponseDTO {
    @Id
    private UUID Id;
    private Long mandorId;
    private String namaMandor;
    private Long supirId;
    private String namaSupir;
    private  List<PanenDTO> detailPanen;
    private Double totalBeratKg;
    private String statusPengiriman;
    private LocalDateTime tanggalPengiriman;
    private String statusPersetujuanMandor;
    private String statusPersetujuanAdmin;
    private String alasanPenolakan;
    private Double beratDiakuiKg;



}
