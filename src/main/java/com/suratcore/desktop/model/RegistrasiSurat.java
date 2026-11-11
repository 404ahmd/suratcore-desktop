package com.suratcore.desktop.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrasiSurat {

    private Long id;

    @JsonProperty("nomor_surat")
    @JsonAlias({"nomorSurat", "nomor_surat"})
    private String nomorSurat;

    @JsonProperty("user_id")
    @JsonAlias({"userId", "user_id"})
    private Long userId;

    @JsonProperty("divisi_id")
    @JsonAlias({"divisiId", "divisi_id"})
    private Long divisiId;

    private String keterangan;
    private String tanggal;
    private String status;

    private Division divisi;

    public RegistrasiSurat() {
    }

    public RegistrasiSurat(Long id, String nomorSurat, Long userId, Long divisiId, String keterangan, String tanggal, String status) {
        this.id = id;
        this.nomorSurat = nomorSurat;
        this.userId = userId;
        this.divisiId = divisiId;
        this.keterangan = keterangan;
        this.tanggal = tanggal;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Division getDivisi() {
        return divisi;
    }

    public void setDivisi(Division divisi) {
        this.divisi = divisi;
    }

    /**
     * Helper to get Division display label for table view.
     */
    public String getDivisiDisplay() {
        if (divisi != null) {
            return divisi.getKodeDivisi() != null ? divisi.getKodeDivisi() : divisi.getNamaDivisi();
        }
        return divisiId != null ? "Divisi #" + divisiId : "-";
    }
}

