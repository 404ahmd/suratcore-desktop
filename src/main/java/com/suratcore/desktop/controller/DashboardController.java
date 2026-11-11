package com.suratcore.desktop.controller;

import com.suratcore.desktop.model.AuthResponse;
import com.suratcore.desktop.model.Division;
import com.suratcore.desktop.model.RegistrasiSurat;
import com.suratcore.desktop.model.RegistrasiSuratRequest;
import com.suratcore.desktop.service.ApiService;
import com.suratcore.desktop.service.SessionManager;
import com.suratcore.desktop.service.ViewManager;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DashboardController {

    @FXML
    private Label lblUserGreeting;

    @FXML
    private Label lblUserRole;

    @FXML
    private Label lblTotalSurat;

    @FXML
    private Label lblSuratDiproses;

    @FXML
    private Label lblSuratSelesai;

    @FXML
    private Label lblTotalDivisi;

    @FXML
    private TextField txtSearch;

    @FXML
    private ComboBox<String> cbFilterStatus;

    @FXML
    private ComboBox<String> cbFilterDivisi;

    @FXML
    private Button btnGenerateNomor;

    @FXML
    private Button btnRefresh;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Label lblStatus;

    // Table & Columns
    @FXML
    private TableView<RegistrasiSurat> tblSurat;

    @FXML
    private TableColumn<RegistrasiSurat, Number> colNo;

    @FXML
    private TableColumn<RegistrasiSurat, String> colNomorSurat;

    @FXML
    private TableColumn<RegistrasiSurat, String> colDivisi;

    @FXML
    private TableColumn<RegistrasiSurat, String> colKeterangan;

    @FXML
    private TableColumn<RegistrasiSurat, String> colTanggal;

    @FXML
    private TableColumn<RegistrasiSurat, String> colStatus;

    @FXML
    private TableColumn<RegistrasiSurat, Void> colAction;

    private final ApiService apiService = new ApiService();
    private final ObservableList<RegistrasiSurat> masterData = FXCollections.observableArrayList();
    private final Map<Long, Division> divisionMap = new HashMap<>();

    @FXML
    public void initialize() {
        setupUserInfo();
        setupTableColumns();
        setupFilters();
        loadDivisionsAndData();
    }

    private void setupUserInfo() {
        AuthResponse user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getNama() != null ? user.getNama() : user.getUsername();
            lblUserGreeting.setText(name);
            lblUserRole.setText(user.getRole() != null ? user.getRole() : "USER");
        } else {
            lblUserGreeting.setText("User");
            lblUserRole.setText("GUEST");
        }
    }

    private void setupTableColumns() {
        colNo.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(tblSurat.getItems().indexOf(column.getValue()) + 1));
        colNo.setSortable(false);

        colNomorSurat.setCellValueFactory(new PropertyValueFactory<>("nomorSurat"));
        colNomorSurat.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b;");
                }
            }
        });

        colDivisi.setCellValueFactory(cellData -> {
            RegistrasiSurat item = cellData.getValue();
            if (item.getDivisi() != null && item.getDivisi().getKodeDivisi() != null) {
                return new ReadOnlyObjectWrapper<>(item.getDivisi().getKodeDivisi() + " - " + item.getDivisi().getNamaDivisi());
            }
            if (item.getDivisiId() != null && divisionMap.containsKey(item.getDivisiId())) {
                Division d = divisionMap.get(item.getDivisiId());
                return new ReadOnlyObjectWrapper<>(d.getKodeDivisi() + " - " + d.getNamaDivisi());
            }
            return new ReadOnlyObjectWrapper<>(item.getDivisiDisplay());
        });

        colKeterangan.setCellValueFactory(new PropertyValueFactory<>("keterangan"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));

        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    Label badge = new Label(status);
                    badge.getStyleClass().add("status-badge");
                    if ("SELESAI".equalsIgnoreCase(status)) {
                        badge.getStyleClass().add("badge-success");
                    } else {
                        badge.getStyleClass().add("badge-warning");
                    }
                    setGraphic(badge);
                    setText(null);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        setupActionColumn();
    }

    private void setupActionColumn() {
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnToggle = new Button();
            private final Button btnDelete = new Button("Hapus");
            private final HBox container = new HBox(6.0, btnToggle, btnDelete);

            {
                container.setAlignment(Pos.CENTER);
                btnToggle.getStyleClass().add("btn-action-outline");
                btnDelete.getStyleClass().add("btn-action-danger");

                btnToggle.setOnAction(e -> {
                    RegistrasiSurat item = getTableView().getItems().get(getIndex());
                    toggleStatus(item);
                });

                btnDelete.setOnAction(e -> {
                    RegistrasiSurat item = getTableView().getItems().get(getIndex());
                    confirmDelete(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    RegistrasiSurat surat = getTableView().getItems().get(getIndex());
                    if ("SELESAI".equalsIgnoreCase(surat.getStatus())) {
                        btnToggle.setText("Ke Proses");
                    } else {
                        btnToggle.setText("Selesaikan");
                    }
                    setGraphic(container);
                }
            }
        });
    }

    private void setupFilters() {
        cbFilterStatus.setItems(FXCollections.observableArrayList("Semua Status", "DIPROSES", "SELESAI"));
        cbFilterStatus.setValue("Semua Status");

        cbFilterDivisi.setItems(FXCollections.observableArrayList("Semua Divisi"));
        cbFilterDivisi.setValue("Semua Divisi");

        FilteredList<RegistrasiSurat> filteredData = new FilteredList<>(masterData, p -> true);

        // Filter listener
        Runnable applyFilter = () -> {
            String searchText = txtSearch.getText() != null ? txtSearch.getText().toLowerCase().trim() : "";
            String selectedStatus = cbFilterStatus.getValue();
            String selectedDivisi = cbFilterDivisi.getValue();

            filteredData.setPredicate(surat -> {
                // Status Filter
                if (selectedStatus != null && !selectedStatus.equals("Semua Status")) {
                    if (surat.getStatus() == null || !surat.getStatus().equalsIgnoreCase(selectedStatus)) {
                        return false;
                    }
                }

                // Divisi Filter
                if (selectedDivisi != null && !selectedDivisi.equals("Semua Divisi")) {
                    String divCode = "";
                    if (surat.getDivisi() != null && surat.getDivisi().getKodeDivisi() != null) {
                        divCode = surat.getDivisi().getKodeDivisi();
                    } else if (surat.getDivisiId() != null && divisionMap.containsKey(surat.getDivisiId())) {
                        divCode = divisionMap.get(surat.getDivisiId()).getKodeDivisi();
                    }
                    if (!selectedDivisi.startsWith(divCode)) {
                        return false;
                    }
                }

                // Text Search Filter
                if (!searchText.isEmpty()) {
                    boolean matchNomor = surat.getNomorSurat() != null && surat.getNomorSurat().toLowerCase().contains(searchText);
                    boolean matchKet = surat.getKeterangan() != null && surat.getKeterangan().toLowerCase().contains(searchText);
                    boolean matchDiv = surat.getDivisiDisplay() != null && surat.getDivisiDisplay().toLowerCase().contains(searchText);
                    return matchNomor || matchKet || matchDiv;
                }

                return true;
            });

            updateStatistics(filteredData);
            tblSurat.refresh();
        };

        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        cbFilterStatus.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        cbFilterDivisi.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());

        SortedList<RegistrasiSurat> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblSurat.comparatorProperty());
        tblSurat.setItems(sortedData);
    }

    private void updateStatistics(List<RegistrasiSurat> list) {
        int total = masterData.size();
        long diproses = masterData.stream().filter(s -> "DIPROSES".equalsIgnoreCase(s.getStatus())).count();
        long selesai = masterData.stream().filter(s -> "SELESAI".equalsIgnoreCase(s.getStatus())).count();

        lblTotalSurat.setText(String.valueOf(total));
        lblSuratDiproses.setText(String.valueOf(diproses));
        lblSuratSelesai.setText(String.valueOf(selesai));
        lblTotalDivisi.setText(String.valueOf(divisionMap.size()));
    }

    @FXML
    public void loadDivisionsAndData() {
        setLoading(true);
        lblStatus.setText("Mengambil data dari server REST API...");

        // Load Divisions first
        apiService.getDivisionsAsync()
                .thenAccept(divisions -> Platform.runLater(() -> {
                    divisionMap.clear();
                    ObservableList<String> divOptions = FXCollections.observableArrayList("Semua Divisi");
                    for (Division d : divisions) {
                        divisionMap.put(d.getId(), d);
                        divOptions.add(d.getKodeDivisi() + " - " + d.getNamaDivisi());
                    }
                    cbFilterDivisi.setItems(divOptions);
                    cbFilterDivisi.setValue("Semua Divisi");

                    loadSuratData();
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        // If divisions failed, try loading surat directly anyway
                        loadSuratData();
                    });
                    return null;
                });
    }

    private void loadSuratData() {
        apiService.getRegistrasiSuratListAsync()
                .thenAccept(list -> Platform.runLater(() -> {
                    setLoading(false);
                    masterData.clear();
                    masterData.addAll(list);
                    updateStatistics(masterData);
                    lblStatus.setText("Data berhasil disinkronisasi. Total: " + list.size() + " dokumen.");
                    tblSurat.refresh();
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        setLoading(false);
                        String errorMsg = throwable.getCause() != null ?
                                throwable.getCause().getMessage() : throwable.getMessage();
                        lblStatus.setText("Gagal sinkronisasi data: " + errorMsg);
                    });
                    return null;
                });
    }

    @FXML
    private void handleOpenGenerateDialog() {
        try {
            FXMLLoader loader = ViewManager.getInstance().openModal(
                    "/com/suratcore/desktop/view/generate-dialog.fxml",
                    "SuratCore - Buat Nomor Surat Baru"
            );
            GenerateDialogController controller = loader.getController();
            controller.setSuggestedSequence(masterData.size() + 1);
            controller.setOnSavedCallback(success -> {
                if (success) {
                    loadSuratData();
                }
            });
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan", "Gagal membuka dialog generate: " + e.getMessage());
        }
    }

    private void toggleStatus(RegistrasiSurat surat) {
        String newStatus = "SELESAI".equalsIgnoreCase(surat.getStatus()) ? "DIPROSES" : "SELESAI";

        RegistrasiSuratRequest request = new RegistrasiSuratRequest(
                surat.getNomorSurat(),
                surat.getUserId() != null ? surat.getUserId() : 1L,
                surat.getDivisiId() != null ? surat.getDivisiId() : 1L,
                surat.getKeterangan(),
                surat.getTanggal(),
                newStatus
        );

        setLoading(true);
        lblStatus.setText("Memperbarui status nomor " + surat.getNomorSurat() + "...");

        apiService.updateRegistrasiSuratAsync(surat.getId(), request)
                .thenAccept(updated -> Platform.runLater(() -> {
                    setLoading(false);
                    surat.setStatus(newStatus);
                    tblSurat.refresh();
                    updateStatistics(masterData);
                    lblStatus.setText("Status " + surat.getNomorSurat() + " berhasil diubah ke " + newStatus);
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        setLoading(false);
                        String msg = throwable.getCause() != null ?
                                throwable.getCause().getMessage() : throwable.getMessage();
                        showAlert(Alert.AlertType.ERROR, "Gagal Update", msg);
                    });
                    return null;
                });
    }

    private void confirmDelete(RegistrasiSurat surat) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText("Hapus Nomor Registrasi Surat?");
        confirm.setContentText("Anda yakin ingin menghapus nomor registrasi:\n" +
                surat.getNomorSurat() + "\nPerihal: " + surat.getKeterangan());

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            setLoading(true);
            lblStatus.setText("Menghapus data registrasi...");

            apiService.deleteRegistrasiSuratAsync(surat.getId())
                    .thenAccept(v -> Platform.runLater(() -> {
                        setLoading(false);
                        masterData.remove(surat);
                        tblSurat.refresh();
                        updateStatistics(masterData);
                        lblStatus.setText("Data surat berhasil dihapus.");
                    }))
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> {
                            setLoading(false);
                            String msg = throwable.getCause() != null ?
                                    throwable.getCause().getMessage() : throwable.getMessage();
                            showAlert(Alert.AlertType.ERROR, "Gagal Menghapus", msg);
                        });
                        return null;
                    });
        }
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Logout");
        confirm.setHeaderText("Keluar dari Sistem");
        confirm.setContentText("Apakah Anda yakin ingin keluar dari SuratCore?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            SessionManager.getInstance().clearSession();
            try {
                ViewManager.getInstance().switchScene(
                        "/com/suratcore/desktop/view/login-view.fxml",
                        "SuratCore - Login Petugas",
                        480,
                        560
                );
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal kembali ke login: " + e.getMessage());
            }
        }
    }

    private void setLoading(boolean loading) {
        progressIndicator.setVisible(loading);
        btnRefresh.setDisable(loading);
        btnGenerateNomor.setDisable(loading);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

