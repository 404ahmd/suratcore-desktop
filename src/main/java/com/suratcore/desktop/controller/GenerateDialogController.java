package com.suratcore.desktop.controller;

import com.suratcore.desktop.model.Division;
import com.suratcore.desktop.model.RegistrasiSuratRequest;
import com.suratcore.desktop.service.ApiService;
import com.suratcore.desktop.service.DocumentNumberGenerator;
import com.suratcore.desktop.service.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class GenerateDialogController {

    @FXML
    private ComboBox<Division> cbDivision;

    @FXML
    private DatePicker dpTanggal;

    @FXML
    private Spinner<Integer> spSequence;

    @FXML
    private TextField txtNomorSurat;

    @FXML
    private TextArea txtKeterangan;

    @FXML
    private ComboBox<String> cbStatus;

    @FXML
    private Button btnGenerate;

    @FXML
    private Button btnSave;

    @FXML
    private Label lblError;

    @FXML
    private ProgressIndicator progressIndicator;

    private final ApiService apiService = new ApiService();
    private Consumer<Boolean> onSavedCallback;
    private int currentTotalLetters = 0;

    @FXML
    public void initialize() {
        progressIndicator.setVisible(false);
        lblError.setVisible(false);

        dpTanggal.setValue(LocalDate.now());

        cbStatus.setItems(FXCollections.observableArrayList("DIPROSES", "SELESAI"));
        cbStatus.setValue("DIPROSES");

        spSequence.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99999, 1));

        // Auto update preview when Division or Date or Sequence changes
        cbDivision.valueProperty().addListener((obs, oldVal, newVal) -> updateGeneratedNumber());
        dpTanggal.valueProperty().addListener((obs, oldVal, newVal) -> updateGeneratedNumber());
        spSequence.valueProperty().addListener((obs, oldVal, newVal) -> updateGeneratedNumber());

        loadDivisions();
    }

    public void setOnSavedCallback(Consumer<Boolean> onSavedCallback) {
        this.onSavedCallback = onSavedCallback;
    }

    public void setSuggestedSequence(int sequence) {
        this.currentTotalLetters = sequence;
        spSequence.getValueFactory().setValue(Math.max(1, sequence));
        updateGeneratedNumber();
    }

    private void loadDivisions() {
        progressIndicator.setVisible(true);
        apiService.getDivisionsAsync()
                .thenAccept(divisions -> Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    cbDivision.setItems(FXCollections.observableArrayList(divisions));
                    if (!divisions.isEmpty()) {
                        cbDivision.setValue(divisions.get(0));
                    }
                    updateGeneratedNumber();
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        progressIndicator.setVisible(false);
                        showError("Gagal mengambil data divisi: " + throwable.getMessage());
                    });
                    return null;
                });
    }

    @FXML
    private void updateGeneratedNumber() {
        Division selectedDiv = cbDivision.getValue();
        String kodeDivisi = selectedDiv != null ? selectedDiv.getKodeDivisi() : "DIV";
        LocalDate date = dpTanggal.getValue() != null ? dpTanggal.getValue() : LocalDate.now();
        int seq = spSequence.getValue() != null ? spSequence.getValue() : 1;

        String generated = DocumentNumberGenerator.generate(seq, kodeDivisi, date);
        txtNomorSurat.setText(generated);
    }

    @FXML
    private void handleSave() {
        String nomorSurat = txtNomorSurat.getText().trim();
        Division division = cbDivision.getValue();
        LocalDate tanggal = dpTanggal.getValue();
        String keterangan = txtKeterangan.getText().trim();
        String status = cbStatus.getValue();

        if (division == null) {
            showError("Pilih divisi tujuan!");
            return;
        }

        if (nomorSurat.isEmpty()) {
            showError("Nomor surat wajib diisi / digenerate!");
            return;
        }

        if (keterangan.isEmpty()) {
            showError("Keterangan surat wajib diisi!");
            return;
        }

        Long userId = 1L;
        if (SessionManager.getInstance().getCurrentUser() != null &&
                SessionManager.getInstance().getCurrentUser().getId() != null) {
            userId = SessionManager.getInstance().getCurrentUser().getId();
        }

        RegistrasiSuratRequest request = new RegistrasiSuratRequest(
                nomorSurat,
                userId,
                division.getId(),
                keterangan,
                tanggal.toString(),
                status != null ? status : "DIPROSES"
        );

        setLoading(true);
        hideError();

        apiService.createRegistrasiSuratAsync(request)
                .thenAccept(result -> Platform.runLater(() -> {
                    setLoading(false);
                    if (onSavedCallback != null) {
                        onSavedCallback.accept(true);
                    }
                    closeStage();
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        setLoading(false);
                        String msg = throwable.getCause() != null ?
                                throwable.getCause().getMessage() : throwable.getMessage();
                        showError("Gagal menyimpan: " + msg);
                    });
                    return null;
                });
    }

    @FXML
    private void handleCancel() {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) txtNomorSurat.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    private void setLoading(boolean loading) {
        progressIndicator.setVisible(loading);
        btnSave.setDisable(loading);
        btnGenerate.setDisable(loading);
    }

    private void showError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
    }

    private void hideError() {
        lblError.setVisible(false);
    }
}

