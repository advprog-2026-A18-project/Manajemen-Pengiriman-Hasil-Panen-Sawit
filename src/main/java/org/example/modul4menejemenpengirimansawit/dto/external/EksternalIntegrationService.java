package org.example.modul4menejemenpengirimansawit.dto.external;

import org.example.modul4menejemenpengirimansawit.dto.external.UserDTO;
import org.example.modul4menejemenpengirimansawit.dto.external.PanenDTO;
import java.util.List;

public interface EksternalIntegrationService {
    UserDTO getSupirById(Long supirId);
    UserDTO getMandorById(Long mandorId);
    List<PanenDTO> getPanenByIds(List<Long> panenIds);
}