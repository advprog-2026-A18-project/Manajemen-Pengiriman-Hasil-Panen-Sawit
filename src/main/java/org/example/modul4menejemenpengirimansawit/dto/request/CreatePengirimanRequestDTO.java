package org.example.modul4menejemenpengirimansawit.dto.request;

import java.util.List;
import lombok.*;
@Getter
@Setter
public class CreatePengirimanRequestDTO {
    private long supirId;
    private List<Long> hasilPanenId;
    private double totalBeratKg;


}
