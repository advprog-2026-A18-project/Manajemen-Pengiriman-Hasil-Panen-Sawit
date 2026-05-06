# Manajemen Pengiriman Hasil Panen Sawit API

API Modul 4 untuk mengatur pengiriman hasil panen sawit valid dari kebun ke pabrik.

## Aturan Utama

- Mandor hanya dapat menugaskan Supir Truk yang berada pada kebun yang sama.
- Hasil panen yang dikirim harus sudah disetujui Mandor pada modul hasil panen.
- Total muatan satu pengiriman tidak boleh melebihi 400 kg.
- Status pengiriman berjalan satu arah: `Memuat` -> `Mengirim` -> `Tiba di Tujuan`.
- Approval Mandor memicu payroll Supir secara async melalui integrasi eksternal.
- Approval atau partial reject Admin memicu payroll Mandor secara async melalui integrasi eksternal.

## Endpoint

### 1. Mandor melihat daftar Supir satu kebun

```http
GET /api/pengiriman/mandor/{mandorId}/supir?searchNama=Supir
```

Mengembalikan daftar Supir Truk yang bertugas pada kebun yang sama dengan Mandor. `searchNama` opsional.

### 2. Mandor menugaskan Supir

```http
POST /api/pengiriman?mandorId={mandorId}
```

Body:

```json
{
  "supirId": "uuid",
  "hasilPanenIds": ["uuid"]
}
```

### 3. Supir update status pengiriman

```http
PUT /api/pengiriman/{id}/status?supirId={supirId}
```

Body:

```json
{
  "status": "Mengirim"
}
```

Status valid: `Memuat`, `Mengirim`, `Tiba di Tujuan`. Update hanya boleh maju satu langkah.

### 4. Supir melihat daftar pengiriman miliknya

```http
GET /api/pengiriman/supir?supirId={supirId}&tanggal=2026-05-06
```

`tanggal` opsional.

### 5. Mandor melihat daftar pengiriman di kebunnya

```http
GET /api/pengiriman/mandor?mandorId={mandorId}&status=Mengirim
```

`status` opsional.

### 6. Mandor melihat pengiriman spesifik Supir

```http
GET /api/pengiriman/mandor/{mandorId}/supir/{supirId}
```

### 7. Mandor approve/reject pengiriman

```http
PUT /api/pengiriman/{id}/review/mandor?mandorId={mandorId}
```

Body:

```json
{
  "approved": false,
  "alasanPenolakan": "Kualitas muatan tidak sesuai"
}
```

Review Mandor hanya bisa dilakukan setelah status pengiriman `Tiba di Tujuan`.

### 8. Admin melihat pengiriman yang sudah disetujui Mandor

```http
GET /api/pengiriman/admin/disetujui?tanggal=2026-05-06&searchNamaMandor=Mandor
```

`tanggal` dan `searchNamaMandor` opsional.

### 9. Admin approve/reject/partial reject pengiriman

```http
PUT /api/pengiriman/{id}/review/admin
```

Body approve:

```json
{
  "statusApproval": "Approve"
}
```

Body reject:

```json
{
  "statusApproval": "Reject",
  "alasanPenolakan": "Dokumen tidak lengkap"
}
```

Body partial reject:

```json
{
  "statusApproval": "Partial_Reject",
  "beratDiakuiKg": 250.0,
  "alasanPenolakan": "Sebagian sawit tidak lengkap"
}
```

Admin hanya dapat mereview pengiriman yang sudah disetujui Mandor.
