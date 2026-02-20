package org.example.modul4menejemenpengirimansawit.dto.external;

import java.util.List;

public interface EksternalIntegrationService {
    UserDTO getSupirById(Long supirId);
    UserDTO getMandorById(Long mandorId);
    List<PanenDTO> getPanenByIds(List<Long> panenIds);
}