package com.suratcore.desktop.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegistrasiSuratRequest {

    @JsonProperty("nomor_surat")
    private String nomorSurat;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("divisi_id")
    private Long divisiId;

    private String keterangan;
    private String tanggal;
    private String status;

    public RegistrasiSuratRequest() {
    }

    public RegistrasiSuratRequest(String nomorSurat, Long userId, Long divisiId, String keterangan, String tanggal, String status) {
        this.nomorSurat = nomorSurat;
        this.userId = userId;
        this.divisiId = divisiId;
        this.keterangan = keterangan;
        this.tanggal = tanggal;
        this.status = status;
    }

    public String getNomorSurat() {
        return nomorSurat;
    }

    public void setNomorSurat(String nomorSurat) {
        this.nomorSurat = nomorSurat;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDivisiId() {
        return divisiId;
    }

    public void setDivisiId(Long divisiId) {
        this.divisiId = divisiId;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

