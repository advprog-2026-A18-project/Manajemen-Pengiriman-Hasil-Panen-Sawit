package org.example.modul4menejemenpengirimansawit.service;

import org.example.modul4menejemenpengirimansawit.dto.*;
import org.example.modul4menejemenpengirimansawit.dto.external.EksternalIntegrationService;
import org.example.modul4menejemenpengirimansawit.dto.request.CreatePengirimanRequestDTO;
import org.example.modul4menejemenpengirimansawit.dto.request.ReviewAdminRequestDTO;
import org.example.modul4menejemenpengirimansawit.dto.request.ReviewMandorRequestDTO;
import org.example.modul4menejemenpengirimansawit.dto.request.UpdateStatusRequestDTO;
import org.example.modul4menejemenpengirimansawit.dto.response.PengirimanResponseDTO;
import org.example.modul4menejemenpengirimansawit.model.Pengiriman;
import org.example.modul4menejemenpengirimansawit.repository.PengirimanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PengirimanService {

    private  PengirimanRepository pengirimanRepository;
    private  EksternalIntegrationService eksternalService;
    private  PengirimanEventPublisher eventPublisher;

    // Constructor Injection (Lebih disarankan daripada @Autowired)
    public PengirimanService(PengirimanRepository pengirimanRepository, EksternalIntegrationService eksternalService, PengirimanEventPublisher eventPublisher) {
        this.pengirimanRepository = pengirimanRepository;
        this.eksternalService = eksternalService;
        this.eventPublisher = eventPublisher;
    }

    public PengirimanResponseDTO tugaskanSupir(CreatePengirimanRequestDTO request, Long mandorId) {


//         TODO 1: Panggil eksternalService.getPanenByIds() untuk totalin berat sawitnya.
//         TODO 2: Validasi apakah total berat > 400 kg[cite: 125]. Kalau iya, throw exception.
//         TODO 3: Buat objek Pengiriman baru, set status awal "Memuat"[cite: 127].
//         TODO 4: Simpan pakai pengirimanRepository.save().
//         TODO 5: Kembalikan dalam bentuk PengirimanResponseDTO.
        return null;
    }

    public PengirimanResponseDTO updateStatusPengiriman(Long pengirimanId, UpdateStatusRequestDTO request) {
        // TODO 1: Cari pengiriman berdasarkan ID.
//        [cite_start]// TODO 2: Ubah statusnya sesuai request (Memuat -> Mengirim -> Tiba di Tujuan)[cite: 126].
        // TODO 3: Simpan perubahan ke database.
        return null;
    }

    public PengirimanResponseDTO reviewPengirimanByMandor(Long pengirimanId, ReviewMandorRequestDTO request) {
        // TODO 1: Cari pengiriman berdasarkan ID.
        // TODO 2: Cek request.isApproved().
//        [cite_start]// TODO 3: Jika false, validasi wajib ada alasan penolakan[cite: 132].
        // TODO 4: Update status approval Mandor dan simpan.
//        [cite_start]// TODO 5: Jika disetujui (true), panggil eventPublisher.publishSupirPayrollEvent()[cite: 135].
        return null;
    }

    public PengirimanResponseDTO reviewPengirimanByAdmin(Long pengirimanId, ReviewAdminRequestDTO request) {
        // TODO 1: Cari pengiriman berdasarkan ID.
        // TODO 2: Cek statusApproval dari request (Approve / Reject / Partial_Reject).
//        [cite_start]// TODO 3: Jika Reject/Partial_Reject, wajib simpan alasan penolakan[cite: 140, 142].
        // TODO 4: Update database.
//        [cite/_start]// TODO 5: Jika Approve atau Partial_Reject, panggil eventPublisher.publishMandorPayrollEvent()[cite: 140, 143].
        return null;
    }

    public List<PengirimanResponseDTO> getDaftarPengiriman(String status, Long supirId, String tanggal) {
        // TODO 1: Ambil data dari repository (bisa pakai findAll atau method custom di repository).
        // TODO 2: Terapkan filter sesuai parameter yang tidak null.
        // TODO 3: Loop data pengiriman, panggil eksternalService untuk ambil nama supir/mandor, lalu map ke List<PengirimanResponseDTO>.
        return null;
    }
}