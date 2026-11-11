package com.suratcore.desktop.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewManager {

    private static ViewManager instance;
    private Stage primaryStage;

    private ViewManager() {
    }

    public static synchronized ViewManager getInstance() {
        if (instance == null) {
            instance = new ViewManager();
        }
        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Switch current scene on primary stage.
     */
    public <T> T switchScene(String fxmlPath, String title, double width, double height) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root, width, height);

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();

        return loader.getController();
    }

    /**
     * Open a modal dialog.
     */
    public <T> FXMLLoader openModal(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Stage modalStage = new Stage();
        modalStage.setTitle(title);
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initOwner(primaryStage);
        modalStage.setScene(new Scene(root));
        modalStage.setResizable(false);
        modalStage.show();

        return loader;
    }
}

