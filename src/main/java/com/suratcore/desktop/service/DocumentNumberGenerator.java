package com.suratcore.desktop.service;

import java.time.LocalDate;

public class DocumentNumberGenerator {

    private static final String[] ROMAN_MONTHS = {
            "I", "II", "III", "IV", "V", "VI",
            "VII", "VIII", "IX", "X", "XI", "XII"
    };

    /**
     * Generate standard corporate letter registration number.
     * Format: [SEQ]/[KODE_DIVISI]/SRT/[ROMAN_MONTH]/[YEAR]
     * Example: 001/IT/SRT/XI/2026
     */
    public static String generate(int sequenceNumber, String kodeDivisi, LocalDate date) {
        if (kodeDivisi == null || kodeDivisi.trim().isEmpty()) {
            kodeDivisi = "CORP";
        } else {
            kodeDivisi = kodeDivisi.trim().toUpperCase();
        }

        if (date == null) {
            date = LocalDate.now();
        }

        String formattedSeq = String.format("%03d", Math.max(1, sequenceNumber));
        String romanMonth = toRomanMonth(date.getMonthValue());
        int year = date.getYear();

        return String.format("%s/%s/SRT/%s/%d", formattedSeq, kodeDivisi, romanMonth, year);
    }

    public static String toRomanMonth(int month) {
        if (month >= 1 && month <= 12) {
            return ROMAN_MONTHS[month - 1];
        }
        return "I";
    }
}

