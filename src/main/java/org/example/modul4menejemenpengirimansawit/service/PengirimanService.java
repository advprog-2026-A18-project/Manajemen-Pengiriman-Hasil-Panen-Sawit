package org.example.modul4menejemenpengirimansawit.service;

import org.example.modul4menejemenpengirimansawit.dto.external.*;
import org.example.modul4menejemenpengirimansawit.dto.request.*;
import org.example.modul4menejemenpengirimansawit.dto.response.PengirimanResponseDTO;
import org.example.modul4menejemenpengirimansawit.model.Pengiriman;
import org.example.modul4menejemenpengirimansawit.repository.PengirimanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PengirimanService {

    private final PengirimanRepository pengirimanRepository;
    private final EksternalIntegrationService eksternalService;


    public PengirimanService(PengirimanRepository pengirimanRepository, EksternalIntegrationService eksternalService) {
        this.pengirimanRepository = pengirimanRepository;
        this.eksternalService = eksternalService;
    }


    @Transactional
    public PengirimanResponseDTO tugaskanSupir(CreatePengirimanRequestDTO request, Long mandorId) {

        List<PanenDTO> listPanen = eksternalService.getPanenByIds(request.getHasilPanenId());


        double totalBerat = listPanen.stream().mapToDouble(PanenDTO::getKilogramSawit).sum();


        if (totalBerat > 400.0) {
            throw new IllegalArgumentException("Total muatan melebihi kapasitas maksimum truk (400 Kg).");
        }

        Pengiriman pengiriman = Pengiriman.builder()
                .id(UUID.randomUUID()) // Gunakan UUID sesuai model
                .mandorId(mandorId)
                .supirId(request.getSupirId())
                .hasilPanen(request.getHasilPanenId())
                .totalBeratKg(totalBerat)
                .status("Memuat") // Status default [cite: 127]
                .tanggalPengiriman(LocalDateTime.now())
                .statusPersetujuanMandor("PENDING")
                .statusPersetujuanAdmin("PENDING")
                .build();

        pengiriman = pengirimanRepository.save(pengiriman);
        return convertToResponseDTO(pengiriman);
    }


    @Transactional
    public PengirimanResponseDTO updateStatusPengiriman(UUID pengirimanId, UpdateStatusRequestDTO request) {
        Pengiriman pengiriman = findPengirimanOrThrow(pengirimanId);

        String status = request.getStatus();
        // Validasi alur status sesuai spek: Memuat, Mengirim, Tiba di Tujuan [cite: 126]
        if (!List.of("Memuat", "Mengirim", "Tiba di Tujuan").contains(status)) {
            throw new IllegalArgumentException("Status pengiriman tidak valid.");
        }

        pengiriman.setStatus(status);
        return convertToResponseDTO(pengirimanRepository.save(pengiriman));
    }


    @Transactional
    public PengirimanResponseDTO reviewByMandor(UUID pengirimanId, ReviewMandorRequestDTO request) {
        Pengiriman pengiriman = findPengirimanOrThrow(pengirimanId);

        if (request.isApproved()) {
            pengiriman.setStatusPersetujuanMandor("DISETUJUI");
            // Trigger Payroll Supir secara asinkronus [cite: 135]
            // eventPublisher.publishEvent(new SupirPayrollEvent(pengiriman));
        } else {
            if (request.getAlasanPenolakan() == null || request.getAlasanPenolakan().isBlank()) {
                throw new IllegalArgumentException("Alasan penolakan wajib diisi[cite: 132].");
            }
            pengiriman.setStatusPersetujuanMandor("DITOLAK");
            pengiriman.setAlasanPenolakan(request.getAlasanPenolakan());
        }

        return convertToResponseDTO(pengirimanRepository.save(pengiriman));
    }


    @Transactional
    public PengirimanResponseDTO reviewByAdmin(UUID pengirimanId, ReviewAdminRequestDTO request) {
        Pengiriman pengiriman = findPengirimanOrThrow(pengirimanId);

        String status = request.getStatusAproval(); // "Approve", "Reject", atau "Partial_Reject"
        pengiriman.setStatusPersetujuanAdmin(status);

        if ("Partial_Reject".equalsIgnoreCase(status)) {
            if (request.getBeratdiAkuiKg() == null || request.getAlasanPenolakan() == null) {
                throw new IllegalArgumentException("Partial Reject memerlukan berat diakui dan alasan[cite: 142].");
            }
            pengiriman.setBeratDiakui(request.getBeratdiAkuiKg());
            pengiriman.setAlasanPenolakan(request.getAlasanPenolakan());
        } else if ("Approve".equalsIgnoreCase(status)) {
            pengiriman.setBeratDiakui(pengiriman.getTotalBeratKg());

        } else {
            pengiriman.setAlasanPenolakan(request.getAlasanPenolakan());
        }

        return convertToResponseDTO(pengirimanRepository.save(pengiriman));
    }


    public List<PengirimanResponseDTO> getDaftarPengiriman(String status, Long supirId, String tanggal) {
        return pengirimanRepository.findAll().stream()
                .filter(p -> status == null || p.getStatus().equalsIgnoreCase(status))
                .filter(p -> supirId == null || p.getSupirId() == supirId)
                .filter(p -> tanggal == null || p.getTanggalPengiriman().toLocalDate().toString().equals(tanggal))
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }


    private Pengiriman findPengirimanOrThrow(UUID id) {
        return pengirimanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pengiriman dengan ID " + id + " tidak ditemukan."));
    }


    private PengirimanResponseDTO convertToResponseDTO(Pengiriman entity) {
        UserDTO mandor = eksternalService.getMandorById(entity.getMandorId());
        UserDTO supir = eksternalService.getSupirById(entity.getSupirId());
        List<PanenDTO> details = eksternalService.getPanenByIds(entity.getHasilPanen());

        PengirimanResponseDTO dto = new PengirimanResponseDTO();
        dto.setId(entity.getId());
        dto.setMandorId(entity.getMandorId());
        dto.setNamaMandor(mandor != null ? mandor.getNama() : "Data Mandor Tidak Ditemukan");
        dto.setSupirId(entity.getSupirId());
        dto.setNamaSupir(supir != null ? supir.getNama() : "Data Supir Tidak Ditemukan");
        dto.setDetailPanen(details);
        dto.setTotalBeratKg(entity.getTotalBeratKg());
        dto.setStatusPengiriman(entity.getStatus());
        dto.setTanggalPengiriman(entity.getTanggalPengiriman());
        dto.setStatusPersetujuanMandor(entity.getStatusPersetujuanMandor());
        dto.setStatusPersetujuanAdmin(entity.getStatusPersetujuanAdmin());
        return dto;
    }
}