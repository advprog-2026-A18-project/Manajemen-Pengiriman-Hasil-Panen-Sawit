package org.example.modul4menejemenpengirimansawit.dto.external;

import java.util.List;
import java.util.UUID;

public interface EksternalIntegrationService {
    UserDTO getSupirById(UUID supirId);
    UserDTO getMandorById(UUID mandorId);
    List<PanenDTO> getPanenByIds(List<UUID> panenIds);
    UUID getKebunIdByMandorId(UUID mandorId);
    UUID getKebunIdBySupirId(UUID supirId);
    List<UserDTO> getSupirByKebun(UUID kebunId, String searchNama);
    void createPayrollSupir(UUID pengirimanId, UUID supirId, double kilogramSawit, String deskripsi);
    void createPayrollMandor(UUID pengirimanId, UUID mandorId, double kilogramSawit, String deskripsi);
}
