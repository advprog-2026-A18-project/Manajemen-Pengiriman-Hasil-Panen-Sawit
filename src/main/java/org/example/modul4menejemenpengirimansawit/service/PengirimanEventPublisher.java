package org.example.modul4menejemenpengirimansawit.service;

import org.springframework.stereotype.Component;

@Component
public class PengirimanEventPublisher {

    public void publishSupirPayrollEvent(Long pengirimanId, Long supirId) {
        System.out.println("Mem-publish event payroll Supir: " + supirId);
    }

    public void publishMandorPayrollEvent(Long pengirimanId, Long mandorId, Double beratDiakuiKg) {
        System.out.println("Mem-publish event payroll Mandor: " + mandorId);
    }
}