package org.example.modul4menejemenpengirimansawit.repository;

import org.example.modul4menejemenpengirimansawit.model.Pengiriman;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PengirimanRepository extends JpaRepository<Pengiriman, Long>{
    List<Pengiriman> findBySupirId(Long supirId);

    List<Pengiriman> findByMandorId(Long mandorId) ;

    List<Pengiriman> findByStatus(String status);
}
