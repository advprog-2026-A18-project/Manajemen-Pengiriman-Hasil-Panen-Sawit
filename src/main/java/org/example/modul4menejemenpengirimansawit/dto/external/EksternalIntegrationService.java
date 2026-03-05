package org.example.modul4menejemenpengirimansawit.dto.external;

import java.util.List;
import java.util.UUID;

public interface EksternalIntegrationService {
    UserDTO getSupirById(UUID supirId);
    UserDTO getMandorById(UUID mandorId);
    List<PanenDTO> getPanenByIds(List<UUID> panenIds);
}