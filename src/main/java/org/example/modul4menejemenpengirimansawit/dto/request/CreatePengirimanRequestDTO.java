package org.example.modul4menejemenpengirimansawit.dto.request;

import java.util.List;
import java.util.UUID;

import lombok.*;
@Getter
@Setter
public class CreatePengirimanRequestDTO {
    private UUID supirId;
    private List<UUID> hasilPanenId;
    private double totalBeratKg;


}
