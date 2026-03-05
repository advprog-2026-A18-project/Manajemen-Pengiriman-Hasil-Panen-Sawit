
---

# Manajemen Pengiriman Hasil Panen Sawit API

## API Reference

Berikut adalah daftar *endpoint* API yang dapat digunakan untuk mengelola data pengiriman hasil panen sawit.

#### 1. Menugaskan Supir

```http
  POST /api/pengiriman

```

### Request Parameters

| Parameter | Type   | Description |
| --- |--------| --- |
| `mandorId` | `UUID` | **Required (Query Param)**. ID Mandor yang melakukan penugasan |

### Request Body (`CreatePengirimanRequestDTO`)

| Parameter | Type         | Description |
| --- |--------------| --- |
| `supirId` | `UUID`       | **Required**. ID Supir yang ditugaskan |
| `hasilPanenIds` | `List<UUID>` | **Required**. Daftar ID hasil panen yang akan dikirim |

### Response (`PengirimanResponseDTO`)

| Parameter | Type             | Description |
| --- |------------------| --- |
| `id` | `UUID`           | ID unik pengiriman |
| `mandorId` | `UUID`           | ID Mandor penanggung jawab |
| `namaMandor` | `String`         | Nama Mandor |
| `supirId` | `UUID`           | ID Supir yang bertugas |
| `namaSupir` | `String`         | Nama Supir |
| `detailPanen` | `List<PanenDTO>` | Detail hasil panen yang dibawa |
| `totalBeratKg` | `Double`         | Total berat muatan dalam Kg |
| `statusPengiriman` | `String`         | Status saat ini (Memuat / Mengirim / Tiba di Tujuan) |
| `tanggalPengiriman` | `DateTime`       | Waktu pengiriman dibuat |
| `statusApprovalMandor` | `String`         | Status persetujuan dari Mandor |
| `statusApprovalAdmin` | `String`         | Status persetujuan dari Admin |

---

#### 2. Update Status Pengiriman (Oleh Supir)

```http
  PUT /api/pengiriman/{Id}/status

```

### Path Variables

| Parameter | Type | Description |
| --- | --- | --- |
| `Id` | `UUID` | **Required**. ID dari pengiriman yang ingin diupdate |

### Request Body (`UpdateStatusRequestDTO`)

| Parameter | Type | Description |
| --- | --- | --- |
| `status` | `String` | **Required**. Status baru (Memuat, Mengirim, Tiba di Tujuan) |

### Response

Mengembalikan objek `PengirimanResponseDTO` yang telah diperbarui.

---

#### 3. Review Pengiriman (Oleh Mandor)

```http
  PUT /api/pengiriman/{Id}/review/mandor

```

### Path Variables

| Parameter | Type | Description |
| --- | --- | --- |
| `Id` | `UUID` | **Required**. ID dari pengiriman yang ingin direview |

### Request Body (`ReviewMandorRequestDTO`)

| Parameter | Type | Description |
| --- | --- | --- |
| `approved` | `boolean` | **Required**. `true` jika disetujui, `false` jika ditolak |
| `alasanPenolakan` | `String` | **Required jika ditolak**. Alasan mengapa pengiriman ditolak |

### Response

Mengembalikan objek `PengirimanResponseDTO` yang telah diperbarui.

---

#### 4. Review Pengiriman (Oleh Admin)

```http
  PUT /api/pengiriman/{Id}/review/admin

```

### Path Variables

| Parameter | Type | Description |
| --- | --- | --- |
| `Id` | `UUID` | **Required**. ID dari pengiriman yang ingin direview |

### Request Body (`ReviewAdminRequestDTO`)

| Parameter | Type | Description |
| --- | --- | --- |
| `statusApproval` | `String` | **Required**. Status persetujuan (Approve, Reject, Partial_Reject) |
| `alasanPenolakan` | `String` | **Required jika Reject/Partial_Reject**. Alasan penolakan |

### Response

Mengembalikan objek `PengirimanResponseDTO` yang telah diperbarui.

---

#### 5. Get Daftar Pengiriman

```http
  GET /api/pengiriman

```

### Request Parameters (Query)

*Semua parameter bersifat opsional dan digunakan untuk filter data.*

| Parameter | Type | Description |
| --- | --- | --- |
| `status` | `String` | Filter berdasarkan status pengiriman (ex: Memuat, Mengirim) |
| `supirId` | `Long` | Filter berdasarkan ID Supir tertentu |
| `tanggal` | `String` | Filter berdasarkan tanggal pengiriman (Format: YYYY-MM-DD) |

### Response

Mengembalikan `List/Array` berisi objek `PengirimanResponseDTO`.

---

## Authors

* [@gebxby](https://www.google.com/search?q=https://www.github.com/gebxby)