package com.suratcore.desktop;

import com.suratcore.desktop.service.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        ViewManager.getInstance().setPrimaryStage(stage);
        ViewManager.getInstance().switchScene(
                "/com/suratcore/desktop/view/login-view.fxml",
                "SuratCore - Login Petugas",
                480,
                560
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
