package org.example.modul4menejemenpengirimansawit.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

import lombok.*;
@Getter
@Setter
public class CreatePengirimanRequestDTO {
    private UUID supirId;
    @JsonProperty("hasilPanenIds")
    @JsonAlias("hasilPanenId")
    private List<UUID> hasilPanenId;
    private double totalBeratKg;


}
