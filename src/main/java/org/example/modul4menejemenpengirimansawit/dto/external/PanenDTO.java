package org.example.modul4menejemenpengirimansawit.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PanenDTO {
    private UUID id;
    private Double kilogramSawit;
    private String beritaHasilPanen;
}