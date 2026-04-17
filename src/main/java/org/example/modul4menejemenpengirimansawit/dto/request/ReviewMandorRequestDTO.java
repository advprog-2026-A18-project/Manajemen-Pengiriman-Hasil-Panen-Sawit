package org.example.modul4menejemenpengirimansawit.dto.request;

import lombok.*;

@Setter
@Getter
public class ReviewMandorRequestDTO {
    private boolean isApproved;
    private String alasanPenolakan;  // fix: was 'AlasanPenolakan' (uppercase A)
}
