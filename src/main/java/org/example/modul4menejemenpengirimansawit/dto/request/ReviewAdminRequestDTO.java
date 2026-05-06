package org.example.modul4menejemenpengirimansawit.dto.request;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok. *;
@Setter
@Getter
public class ReviewAdminRequestDTO {
    @JsonProperty("statusApproval")
    @JsonAlias("statusAproval")
    private String statusAproval;
    private String alasanPenolakan;
    @JsonProperty("beratDiakuiKg")
    @JsonAlias("beratdiAkuiKg")
    private Double beratdiAkuiKg;
}
