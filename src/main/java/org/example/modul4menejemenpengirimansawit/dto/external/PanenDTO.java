package org.example.modul4menejemenpengirimansawit.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.*;
@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PanenDTO {
    private Long id;
    private Double kilogramSawit;
    private String beritaHasilPanen;
}