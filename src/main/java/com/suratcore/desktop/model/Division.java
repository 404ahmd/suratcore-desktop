package com.suratcore.desktop.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Division {

    private Long id;

    @JsonProperty("kode_divisi")
    @JsonAlias({"kodeDivisi", "kode_divisi", "kode"})
    private String kodeDivisi;

    @JsonProperty("nama_divisi")
    @JsonAlias({"namaDivisi", "nama_divisi", "nama"})
    private String namaDivisi;

    public Division() {
    }

    public Division(Long id, String kodeDivisi, String namaDivisi) {
        this.id = id;
        this.kodeDivisi = kodeDivisi;
        this.namaDivisi = namaDivisi;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKodeDivisi() {
        return kodeDivisi;
    }

    public void setKodeDivisi(String kodeDivisi) {
        this.kodeDivisi = kodeDivisi;
    }

    public String getNamaDivisi() {
        return namaDivisi;
    }

    public void setNamaDivisi(String namaDivisi) {
        this.namaDivisi = namaDivisi;
    }

    @Override
    public String toString() {
        if (kodeDivisi != null && namaDivisi != null) {
            return kodeDivisi + " - " + namaDivisi;
        } else if (namaDivisi != null) {
            return namaDivisi;
        } else if (kodeDivisi != null) {
            return kodeDivisi;
        }
        return "Divisi #" + id;
    }
}

