package com.suratcore.desktop.controller;

import com.suratcore.desktop.service.ApiService;
import com.suratcore.desktop.service.ViewManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Label lblError;

    private final ApiService apiService = new ApiService();

    @FXML
    public void initialize() {
        progressIndicator.setVisible(false);
        lblError.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText() != null ? txtUsername.getText().trim() : "";
        String password = txtPassword.getText() != null ? txtPassword.getText() : "";

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username dan password wajib diisi!");
            return;
        }

        setLoading(true);
        hideError();

        apiService.loginAsync(username, password)
                .thenAccept(authResponse -> Platform.runLater(() -> {
                    setLoading(false);
                    try {
                        ViewManager.getInstance().switchScene(
                                "/com/suratcore/desktop/view/dashboard-view.fxml",
                                "SuratCore - Dashboard Registrasi Surat",
                                1150,
                                720
                        );
                    } catch (Exception e) {
                        showError("Gagal membuka dashboard: " + e.getMessage());
                    }
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        setLoading(false);
                        String errorMsg = throwable.getCause() != null ?
                                throwable.getCause().getMessage() : throwable.getMessage();
                        showError("Login Gagal: " + errorMsg);
                    });
                    return null;
                });
    }

    private void setLoading(boolean loading) {
        progressIndicator.setVisible(loading);
        btnLogin.setDisable(loading);
        txtUsername.setDisable(loading);
        txtPassword.setDisable(loading);
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }

    private void hideError() {
        lblError.setVisible(false);
    }
}
