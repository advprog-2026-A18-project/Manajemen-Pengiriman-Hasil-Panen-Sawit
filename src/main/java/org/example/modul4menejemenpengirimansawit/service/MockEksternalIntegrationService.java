package org.example.modul4menejemenpengirimansawit.service;

import org.example.modul4menejemenpengirimansawit.dto.external.EksternalIntegrationService;
import org.example.modul4menejemenpengirimansawit.dto.external.PanenDTO;
import org.example.modul4menejemenpengirimansawit.dto.external.UserDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class MockEksternalIntegrationService implements EksternalIntegrationService {

    @Override
    public UserDTO getSupirById(UUID supirId) {
        return UserDTO.builder()
                .id(supirId)
                .nama("Supir Bayangan")
                .build();
    }

    @Override
    public UserDTO getMandorById(UUID mandorId) {
        return UserDTO.builder()
                .id(mandorId)
                .nama("Mandor Bayangan")
                .build();
    }

    @Override
    public List<PanenDTO> getPanenByIds(List<UUID> panenIds) {
        List<PanenDTO> mockList = new ArrayList<>();

        for (UUID id : panenIds) {
            PanenDTO panen = new PanenDTO();
            panen.setId(id);
            panen.setKilogramSawit(100.0);
            mockList.add(panen);
        }
        return mockList;
    }
}