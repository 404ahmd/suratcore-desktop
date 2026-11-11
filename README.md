# SuratCore Desktop (JavaFX + OkHttp)

Aplikasi desktop berbasis **JavaFX** untuk **Manajemen Nomor Dokumen & Registrasi Surat Perusahaan** yang terintegrasi penuh dengan backend REST API **Spring Boot** (JWT Authentication).

---

## Konfigurasi Lingkungan (Environment Backend URL)

URL backend **tidak ditampilkan** pada layar Login agar tampilan lebih bersih, profesional, dan aman. Aplikasi memuat URL backend secara otomatis dengan hierarki prioritas:

1. **Environment Variable Sistem (Prioritas Tertinggi)**:
   ```bash
   export SURATCORE_API_URL="http://localhost:8080"
   # atau
   export API_BASE_URL="https://api.perusahaan.com"
   ```
2. **JVM System Property (`-D`)**:
   ```bash
   ./mvnw javafx:run -Dapi.baseUrl="http://localhost:8080"
   ```
3. **File Konfigurasi (`src/main/resources/application.properties`)**:
   ```properties
   api.baseUrl=http://localhost:8080
   ```
4. **Default Fallback**: `http://localhost:8080`

---

## Fitur Aplikasi

1. **Authentication (Login Screen Bersih & Minimalis)**
   - Form sederhana dan fokus: hanya **Username** dan **Password**.
   - Terproteksi JWT Bearer Token (`/api/auth/login`).
   - Sesi pengguna otomatis tersimpan di `SessionManager`.

2. **Dashboard Manajemen Nomor Surat**
   - **Tabel Registrasi Surat**: Menampilkan data dari endpoint `GET /api/registrasi-surat`.
   - **Filter & Search Real-time**: Pencarian instan berdasarkan nomor surat, perihal, atau divisi serta filter status (`DIPROSES` / `SELESAI`).
   - **Ringkasan KPI / Metrik**: Total surat terdaftar, surat dalam proses, surat selesai, dan total divisi.
   - **Aksi Cepat**: Tombol *Selesaikan* / *Ke Proses* serta tombol *Hapus*.

3. **Generator & Registrasi Nomor Surat Baru**
   - Modal dialog khusus untuk registrasi surat (`POST /api/registrasi-surat`).
   - Format penomoran otomatis resmi:
     `[No Urut]/[Kode Divisi]/SRT/[Bulan Romawi]/[Tahun]` *(contoh: `001/IT/SRT/XI/2026`)*.
   - Sinkronisasi otomatis data divisi dari `GET /api/divisions`.

---

## Struktur File

```text
suratcore-desktop/
├── pom.xml
├── mvnw
├── README.md
└── src/
    └── main/
        ├── java/com/suratcore/desktop/
        │   ├── App.java                              # Main entry point JavaFX
        │   ├── Launcher.java                         # Launcher bootstrap class
        │   ├── controller/
        │   │   ├── LoginController.java              # Controller Login Screen
        │   │   ├── DashboardController.java          # Controller Dashboard & Tabel
        │   │   └── GenerateDialogController.java     # Controller Dialog Generate Nomor
        │   ├── model/
        │   │   ├── AuthResponse.java                 # Model Response Login & JWT
        │   │   ├── LoginRequest.java                 # DTO Request Login
        │   │   ├── Division.java                     # Model Entity Divisi
        │   │   ├── RegistrasiSurat.java              # Model Entity Registrasi Surat
        │   │   └── RegistrasiSuratRequest.java       # DTO Request Create/Update Surat
        │   └── service/
        │       ├── AppConfig.java                    # Konfigurasi environment (Env Var, Props, Default)
        │       ├── ApiService.java                   # OkHttp Client (JWT Bearer, Jackson)
        │       ├── SessionManager.java               # Pengelola sesi user & token
        │       ├── DocumentNumberGenerator.java      # Generator format nomor surat
        │       └── ViewManager.java                  # Helper navigasi antar layar & dialog
        └── resources/
            ├── application.properties                # Konfigurasi default URL backend
            └── com/suratcore/desktop/
                ├── view/
                │   ├── login-view.fxml               # Layout Login Screen (bersih, tanpa field URL)
                │   ├── dashboard-view.fxml           # Layout Dashboard Utama
                │   └── generate-dialog.fxml          # Layout Dialog Generate Nomor
                └── css/
                    └── style.css                     # Modern Enterprise Theme
```

---

## Cara Menjalankan

```bash
# Menjalankan aplikasi dengan URL backend default (http://localhost:8080)
./mvnw clean javafx:run

# Atau menjalankan dengan URL backend khusus via environment variable:
SURATCORE_API_URL=https://api.perusahaan.com ./mvnw clean javafx:run
```
