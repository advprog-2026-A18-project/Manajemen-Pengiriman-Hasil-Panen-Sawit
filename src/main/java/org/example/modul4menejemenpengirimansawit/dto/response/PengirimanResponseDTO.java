package org.example.modul4menejemenpengirimansawit.dto.response;

import org.example.modul4menejemenpengirimansawit.dto.external.PanenDTO;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
public class PengirimanResponseDTO {
    private Long id;
    private Long mandorId;
    private String namaMandor;
    private Long supirId;
    private String namaSupir;
    private  List<PanenDTO> panenDTO;
    private Double totalBeratKg;
    private String statusPengiriman;
    private LocalDateTime tanggalPengiriman;
    private String statusPersetujuanMandor;
    private String statusPersetujuanAdmin;
    private String alasanPenolakan;
    private Double beratDiakuiKg;
}
