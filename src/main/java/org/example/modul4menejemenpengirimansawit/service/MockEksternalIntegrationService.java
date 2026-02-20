package org.example.modul4menejemenpengirimansawit.service;

import org.example.modul4menejemenpengirimansawit.dto.external.EksternalIntegrationService;
import org.example.modul4menejemenpengirimansawit.dto.external.PanenDTO;
import org.example.modul4menejemenpengirimansawit.dto.external.UserDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class MockEksternalIntegrationService implements EksternalIntegrationService {

    @Override
    public UserDTO getSupirById(Long supirId) {
        UserDTO mockSupir = new UserDTO();
        mockSupir.setId(supirId);
        mockSupir.setNama("Supir Bayangan");
        return mockSupir;
    }

    @Override
    public UserDTO getMandorById(Long mandorId) {
        UserDTO mockMandor = new UserDTO();
        mockMandor.setId(mandorId);
        mockMandor.setNama("Mandor Bayangan");
        return mockMandor;
    }

    @Override
    public List<PanenDTO> getPanenByIds(List<Long> panenIds) {
        List<PanenDTO> mockList = new ArrayList<>();
        // Asumsi setiap panen beratnya 100 Kg untuk testing
        for (Long id : panenIds) {
            PanenDTO panen = new PanenDTO();
            panen.setId(id);
            panen.setKilogramSawit(100.0);
            mockList.add(panen);
        }
        return mockList;
    }
}