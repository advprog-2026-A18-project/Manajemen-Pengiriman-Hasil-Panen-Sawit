package org.example.modul4menejemenpengirimansawit.service;

import org.example.modul4menejemenpengirimansawit.dto.external.PanenDTO;
import org.example.modul4menejemenpengirimansawit.dto.external.UserDTO;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class MockEksternalIntegrationServiceTest {

    private final MockEksternalIntegrationService service = new MockEksternalIntegrationService();

    @Test
    void testMockServiceMethods() {
        UUID id = UUID.randomUUID();

        UserDTO supir = service.getSupirById(id);
        UserDTO mandor = service.getMandorById(id);
        List<PanenDTO> panen = service.getPanenByIds(List.of(id));

        assertEquals("Supir Bayangan", supir.getNama());
        assertEquals("Mandor Bayangan", mandor.getNama());
        assertFalse(panen.isEmpty());
        assertEquals(100.0, panen.get(0).getKilogramSawit());
    }

}