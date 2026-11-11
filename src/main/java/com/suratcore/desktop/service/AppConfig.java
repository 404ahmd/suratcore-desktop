package com.suratcore.desktop.service;

import java.io.InputStream;
import java.util.Properties;

/**
 * Manages application environment and configuration.
 * Resolves API Base URL from:
 * 1. Environment Variable (SURATCORE_API_URL or API_BASE_URL)
 * 2. JVM System Property (-Dapi.baseUrl)
 * 3. Classpath configuration file (application.properties)
 * 4. Fallback default (http://localhost:8080)
 */
public class AppConfig {

    private static final String DEFAULT_URL = "http://localhost:8080";
    private static final String PROPERTIES_FILE = "/application.properties";
    private static String apiBaseUrl;

    static {
        loadConfiguration();
    }

    private static void loadConfiguration() {
        // 1. Cek Environment Variable terlebih dahulu
        String envUrl = System.getenv("SURATCORE_API_URL");
        if (envUrl == null || envUrl.trim().isEmpty()) {
            envUrl = System.getenv("API_BASE_URL");
        }

        if (envUrl != null && !envUrl.trim().isEmpty()) {
            apiBaseUrl = sanitizeUrl(envUrl);
            return;
        }

        // 2. Cek JVM System Property (-Dapi.baseUrl=...)
        String propUrl = System.getProperty("api.baseUrl");
        if (propUrl != null && !propUrl.trim().isEmpty()) {
            apiBaseUrl = sanitizeUrl(propUrl);
            return;
        }

        // 3. Muat dari file application.properties di resources
        try (InputStream input = AppConfig.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                String urlFromProp = prop.getProperty("api.baseUrl");
                if (urlFromProp != null && !urlFromProp.trim().isEmpty()) {
                    apiBaseUrl = sanitizeUrl(urlFromProp);
                    return;
                }
            }
        } catch (Exception ignored) {
        }

        // 4. Default fallback
        apiBaseUrl = DEFAULT_URL;
    }

    private static String sanitizeUrl(String url) {
        String clean = url.trim();
        return clean.endsWith("/") ? clean.substring(0, clean.length() - 1) : clean;
    }

    public static String getApiBaseUrl() {
        if (apiBaseUrl == null) {
            loadConfiguration();
        }
        return apiBaseUrl;
    }

    public static void setApiBaseUrl(String url) {
        if (url != null && !url.trim().isEmpty()) {
            apiBaseUrl = sanitizeUrl(url);
        }
    }
}

