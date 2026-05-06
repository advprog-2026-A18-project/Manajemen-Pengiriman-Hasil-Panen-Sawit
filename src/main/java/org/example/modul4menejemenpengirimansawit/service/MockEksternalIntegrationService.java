package org.example.modul4menejemenpengirimansawit.service;

import org.example.modul4menejemenpengirimansawit.dto.external.EksternalIntegrationService;
import org.example.modul4menejemenpengirimansawit.dto.external.PanenDTO;
import org.example.modul4menejemenpengirimansawit.dto.external.UserDTO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


@Service
public class MockEksternalIntegrationService implements EksternalIntegrationService {

    private static final UUID KEBUN_DEFAULT_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Override
    public UserDTO getSupirById(UUID supirId) {
        return UserDTO.builder()
                .id(supirId)
                .nama("Supir Bayangan")
                .role("SUPIR")
                .kebunId(KEBUN_DEFAULT_ID)
                .build();
    }

    @Override
    public UserDTO getMandorById(UUID mandorId) {
        return UserDTO.builder()
                .id(mandorId)
                .nama("Mandor Bayangan")
                .role("MANDOR")
                .kebunId(KEBUN_DEFAULT_ID)
                .build();
    }

    @Override
    public List<PanenDTO> getPanenByIds(List<UUID> panenIds) {
        List<PanenDTO> mockList = new ArrayList<>();
        if (panenIds == null) {
            return mockList;
        }

        for (UUID id : panenIds) {
            PanenDTO panen = new PanenDTO();
            panen.setId(id);
            panen.setKilogramSawit(100.0);
            panen.setStatusPersetujuanMandor("DISETUJUI");
            mockList.add(panen);
        }
        return mockList;
    }

    @Override
    public UUID getKebunIdByMandorId(UUID mandorId) {
        return KEBUN_DEFAULT_ID;
    }

    @Override
    public UUID getKebunIdBySupirId(UUID supirId) {
        return KEBUN_DEFAULT_ID;
    }

    @Override
    public List<UserDTO> getSupirByKebun(UUID kebunId, String searchNama) {
        if (!KEBUN_DEFAULT_ID.equals(kebunId)) {
            return List.of();
        }

        List<UserDTO> supir = List.of(
                UserDTO.builder()
                        .id(UUID.fromString("00000000-0000-0000-0000-000000000101"))
                        .nama("Supir Bayangan")
                        .role("SUPIR")
                        .kebunId(KEBUN_DEFAULT_ID)
                        .build(),
                UserDTO.builder()
                        .id(UUID.fromString("00000000-0000-0000-0000-000000000102"))
                        .nama("Supir Cadangan")
                        .role("SUPIR")
                        .kebunId(KEBUN_DEFAULT_ID)
                        .build()
        );

        if (searchNama == null || searchNama.isBlank()) {
            return supir;
        }

        String normalizedSearch = searchNama.toLowerCase(Locale.ROOT);
        return supir.stream()
                .filter(s -> s.getNama() != null
                        && s.getNama().toLowerCase(Locale.ROOT).contains(normalizedSearch))
                .toList();
    }

    @Async
    @Override
    public void createPayrollSupir(UUID pengirimanId, UUID supirId, double kilogramSawit, String deskripsi) {
        // Mock integration point for Modul Manajemen Pembayaran.
    }

    @Async
    @Override
    public void createPayrollMandor(UUID pengirimanId, UUID mandorId, double kilogramSawit, String deskripsi) {
        // Mock integration point for Modul Manajemen Pembayaran.
    }
}
