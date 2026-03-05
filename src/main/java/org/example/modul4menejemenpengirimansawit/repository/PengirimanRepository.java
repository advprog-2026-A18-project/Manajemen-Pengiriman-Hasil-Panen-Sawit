package org.example.modul4menejemenpengirimansawit.repository;

import org.example.modul4menejemenpengirimansawit.model.Pengiriman;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PengirimanRepository extends JpaRepository<Pengiriman, UUID> {

    List<Pengiriman> findBySupirId(UUID supirId);
    List<Pengiriman> findByMandorId(UUID mandorId);
    List<Pengiriman> findByStatus(String status);

}