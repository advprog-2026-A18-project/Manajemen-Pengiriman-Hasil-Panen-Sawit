package org.example.modul4menejemenpengirimansawit.service;

import lombok.RequiredArgsConstructor;
import org.example.modul4menejemenpengirimansawit.dto.external.*;
import org.example.modul4menejemenpengirimansawit.dto.request.*;
import org.example.modul4menejemenpengirimansawit.dto.response.PengirimanResponseDTO;
import org.example.modul4menejemenpengirimansawit.model.Pengiriman;
import org.example.modul4menejemenpengirimansawit.repository.PengirimanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PengirimanService {

    private final PengirimanRepository pengirimanRepository;
    private final EksternalIntegrationService eksternalService;

    // =========================================================
    // STATUS ORDERING: digunakan untuk validasi status flow
    // =========================================================
    private static final List<String> STATUS_ORDER = List.of("Memuat", "Mengirim", "Tiba di Tujuan");

    // =========================================================
    // 1. Mandor menugaskan Supir untuk mengangkut hasil panen
    // =========================================================
    @Transactional
    public PengirimanResponseDTO tugaskanSupir(CreatePengirimanRequestDTO request, UUID mandorId) {
        List<PanenDTO> listPanen = eksternalService.getPanenByIds(request.getHasilPanenId());

        double totalBerat = listPanen.stream().mapToDouble(PanenDTO::getKilogramSawit).sum();

        if (totalBerat > 400.0) {
            throw new IllegalArgumentException(
                "Total muatan (" + totalBerat + " Kg) melebihi kapasitas maksimum truk (400 Kg).");
        }

        Pengiriman pengiriman = Pengiriman.builder()
                .id(UUID.randomUUID())
                .mandorId(mandorId)
                .supirId(request.getSupirId())
                .hasilPanen(request.getHasilPanenId())
                .totalBeratKg(totalBerat)
                .status("Memuat")                    // default status
                .tanggalPengiriman(LocalDateTime.now())
                .statusPersetujuanMandor("PENDING")
                .statusPersetujuanAdmin("PENDING")
                .alasanPenolakan(null)
                .beratDiakui(0.0)
                .build();

        pengiriman = pengirimanRepository.save(pengiriman);
        return convertToResponseDTO(pengiriman);
    }

    // =========================================================
    // 2. Supir mengubah status pengiriman (hanya bisa maju)
    // =========================================================
    @Transactional
    public PengirimanResponseDTO updateStatusPengiriman(UUID pengirimanId, UUID supirId,
                                                        UpdateStatusRequestDTO request) {
        Pengiriman pengiriman = findPengirimanOrThrow(pengirimanId);

        // Validasi kepemilikan: hanya supir yang ditugaskan
        if (!pengiriman.getSupirId().equals(supirId)) {
            throw new IllegalArgumentException("Anda tidak berwenang mengubah status pengiriman ini.");
        }

        String statusBaru = request.getStatus();
        int indexSekarang = STATUS_ORDER.indexOf(pengiriman.getStatus());
        int indexBaru     = STATUS_ORDER.indexOf(statusBaru);

        if (indexBaru < 0) {
            throw new IllegalArgumentException(
                "Status tidak valid. Pilihan: Memuat, Mengirim, Tiba di Tujuan.");
        }
        if (indexBaru != indexSekarang + 1) {
            throw new IllegalArgumentException(
                "Status hanya boleh maju satu langkah. Status saat ini: " + pengiriman.getStatus());
        }

        pengiriman.setStatus(statusBaru);
        return convertToResponseDTO(pengirimanRepository.save(pengiriman));
    }

    // =========================================================
    // 3. Supir melihat daftar pengiriman miliknya (filter tanggal)
    // =========================================================
    public List<PengirimanResponseDTO> getDaftarPengiriman(String status, UUID supirId, String tanggal) {
        return pengirimanRepository.findAll().stream()
                .filter(p -> status == null || p.getStatus().equalsIgnoreCase(status))
                .filter(p -> supirId == null || p.getSupirId().equals(supirId))
                .filter(p -> tanggal == null ||
                        p.getTanggalPengiriman().toLocalDate().equals(LocalDate.parse(tanggal)))
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PengirimanResponseDTO> getDaftarPengirimanSupir(UUID supirId, String tanggal) {
        return pengirimanRepository.findBySupirId(supirId).stream()
                .filter(p -> tanggal == null ||
                        p.getTanggalPengiriman().toLocalDate().equals(LocalDate.parse(tanggal)))
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // =========================================================
    // 4. Mandor melihat daftar pengiriman di kebunnya (filter status)
    // =========================================================
    public List<PengirimanResponseDTO> getDaftarPengirimanMandor(UUID mandorId, String status) {
        return pengirimanRepository.findByMandorId(mandorId).stream()
                .filter(p -> status == null || p.getStatus().equalsIgnoreCase(status))
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // =========================================================
    // 5. Mandor melihat pengiriman spesifik seorang Supir
    // =========================================================
    public List<PengirimanResponseDTO> getDaftarPengirimanSupirByMandor(UUID mandorId, UUID supirId) {
        return pengirimanRepository.findByMandorId(mandorId).stream()
                .filter(p -> p.getSupirId().equals(supirId))
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // =========================================================
    // 6. Mandor menyetujui / menolak pengiriman
    //    - Hanya bisa dilakukan setelah status = "Tiba di Tujuan"
    //    - Jika approve → trigger payroll Supir (async)
    // =========================================================
    @Transactional
    public PengirimanResponseDTO reviewByMandor(UUID pengirimanId, UUID mandorId,
                                                ReviewMandorRequestDTO request) {
        Pengiriman pengiriman = findPengirimanOrThrow(pengirimanId);

        // Validasi kepemilikan mandor
        if (!pengiriman.getMandorId().equals(mandorId)) {
            throw new IllegalArgumentException("Anda tidak berwenang mereview pengiriman ini.");
        }
        // Hanya boleh review jika sudah Tiba di Tujuan
        if (!"Tiba di Tujuan".equals(pengiriman.getStatus())) {
            throw new IllegalStateException(
                "Pengiriman belum tiba di tujuan. Status saat ini: " + pengiriman.getStatus());
        }
        // Tidak boleh review ulang
        if (!"PENDING".equals(pengiriman.getStatusPersetujuanMandor())) {
            throw new IllegalStateException("Pengiriman ini sudah direview oleh Mandor.");
        }

        if (request.isApproved()) {
            pengiriman.setStatusPersetujuanMandor("DISETUJUI");
            // TODO: trigger payroll Supir secara asinkronus
            // eventPublisher.publishEvent(new SupirPayrollEvent(pengiriman));
        } else {
            if (request.getAlasanPenolakan() == null || request.getAlasanPenolakan().isBlank()) {
                throw new IllegalArgumentException("Alasan penolakan wajib diisi.");
            }
            pengiriman.setStatusPersetujuanMandor("DITOLAK");
            pengiriman.setAlasanPenolakan(request.getAlasanPenolakan());
        }

        return convertToResponseDTO(pengirimanRepository.save(pengiriman));
    }

    // =========================================================
    // 7. Admin melihat daftar pengiriman yang disetujui Mandor
    //    (filter nama mandor dilakukan di layer luar / controller)
    // =========================================================
    public List<PengirimanResponseDTO> getDaftarPengirimanDisetujuiMandor(String tanggal) {
        return pengirimanRepository.findByStatusPersetujuanMandor("DISETUJUI").stream()
                .filter(p -> tanggal == null ||
                        p.getTanggalPengiriman().toLocalDate().equals(LocalDate.parse(tanggal)))
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // =========================================================
    // 8. Admin menyetujui / menolak / partial-reject pengiriman
    //    - Hanya boleh jika statusPersetujuanMandor = DISETUJUI
    //    - Jika approve → trigger payroll Mandor (async)
    //    - Jika partial reject → beratDiakui = angka yang diakui
    // =========================================================
    @Transactional
    public PengirimanResponseDTO reviewByAdmin(UUID pengirimanId, ReviewAdminRequestDTO request) {
        Pengiriman pengiriman = findPengirimanOrThrow(pengirimanId);

        if (!"DISETUJUI".equals(pengiriman.getStatusPersetujuanMandor())) {
            throw new IllegalStateException(
                "Pengiriman belum disetujui oleh Mandor. Status persetujuan Mandor: "
                + pengiriman.getStatusPersetujuanMandor());
        }
        if (!"PENDING".equals(pengiriman.getStatusPersetujuanAdmin())) {
            throw new IllegalStateException("Pengiriman ini sudah direview oleh Admin.");
        }

        String status = request.getStatusAproval();

        switch (status.toUpperCase()) {
            case "APPROVE" -> {
                pengiriman.setStatusPersetujuanAdmin("DISETUJUI");
                pengiriman.setBeratDiakui(pengiriman.getTotalBeratKg());
                // TODO: trigger payroll Mandor secara asinkronus
                // eventPublisher.publishEvent(new MandorPayrollEvent(pengiriman));
            }
            case "REJECT" -> {
                if (request.getAlasanPenolakan() == null || request.getAlasanPenolakan().isBlank()) {
                    throw new IllegalArgumentException("Alasan penolakan wajib diisi.");
                }
                pengiriman.setStatusPersetujuanAdmin("DITOLAK");
                pengiriman.setAlasanPenolakan(request.getAlasanPenolakan());
            }
            case "PARTIAL_REJECT" -> {
                if (request.getBeratdiAkuiKg() == null || request.getAlasanPenolakan() == null
                        || request.getAlasanPenolakan().isBlank()) {
                    throw new IllegalArgumentException(
                        "Partial Reject memerlukan berat diakui dan alasan penolakan.");
                }
                if (request.getBeratdiAkuiKg() <= 0 ||
                        request.getBeratdiAkuiKg() > pengiriman.getTotalBeratKg()) {
                    throw new IllegalArgumentException(
                        "Berat diakui harus > 0 dan tidak melebihi total berat (" +
                        pengiriman.getTotalBeratKg() + " Kg).");
                }
                pengiriman.setStatusPersetujuanAdmin("PARTIAL_DITOLAK");
                pengiriman.setBeratDiakui(request.getBeratdiAkuiKg());
                pengiriman.setAlasanPenolakan(request.getAlasanPenolakan());
                // TODO: trigger payroll Mandor dengan berat parsial
                // eventPublisher.publishEvent(new MandorPayrollEvent(pengiriman, request.getBeratdiAkuiKg()));
            }
            default -> throw new IllegalArgumentException(
                "Status approval tidak valid. Pilihan: Approve, Reject, Partial_Reject.");
        }

        return convertToResponseDTO(pengirimanRepository.save(pengiriman));
    }

    // =========================================================
    // HELPER
    // =========================================================
    private Pengiriman findPengirimanOrThrow(UUID id) {
        return pengirimanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Pengiriman dengan ID " + id + " tidak ditemukan."));
    }

    private PengirimanResponseDTO convertToResponseDTO(Pengiriman entity) {
        UserDTO mandor  = eksternalService.getMandorById(entity.getMandorId());
        UserDTO supir   = eksternalService.getSupirById(entity.getSupirId());
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
        dto.setAlasanPenolakan(entity.getAlasanPenolakan());
        dto.setBeratDiakuiKg(entity.getBeratDiakui());
        return dto;
    }
}
