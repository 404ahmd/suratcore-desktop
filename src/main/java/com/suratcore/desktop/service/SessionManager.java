package com.suratcore.desktop.service;

import com.suratcore.desktop.model.AuthResponse;

public class SessionManager {

    private static SessionManager instance;

    private String baseUrl;
    private AuthResponse currentUser;
    private String token;

    private SessionManager() {
        this.baseUrl = AppConfig.getApiBaseUrl();
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setSession(AuthResponse authResponse) {
        this.currentUser = authResponse;
        if (authResponse != null) {
            this.token = authResponse.getToken();
        } else {
            this.token = null;
        }
    }

    public void clearSession() {
        this.currentUser = null;
        this.token = null;
    }

    public boolean isLoggedIn() {
        return token != null && !token.trim().isEmpty();
    }

    public AuthResponse getCurrentUser() {
        return currentUser;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBaseUrl() {
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            baseUrl = AppConfig.getApiBaseUrl();
        }
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        if (baseUrl != null && !baseUrl.trim().isEmpty()) {
            this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        }
    }
}
