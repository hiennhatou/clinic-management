package com.ou.clinicmanagement;

import com.ou.services.UserService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App extends Application {
    private static Scene scene;
    private static Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        App.stage = stage;

        try {
            if (UserService.getCurrentUser() == null) throw new RuntimeException("User not logged in");
            scene = new Scene(getFXMLLoader("welcome.fxml").load());
        } catch (Exception e) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, e);
            scene = new Scene(getFXMLLoader("login.fxml").load());
        }

        stage.setScene(scene);
        stage.show();
    }

    public static void moveScene(String fxml) {
        try {
            App.scene.setRoot(getFXMLLoader(fxml).load());
        } catch (IOException e) {
            App.showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi hệ thống", null, null);
            Logger.getLogger(App.class.getName()).log(Level.WARNING, e.getMessage(), e);
        }
    }

    public static void setTitle(String title) {
        App.stage.setTitle(title);
    }

    public static FXMLLoader getFXMLLoader(String fxml) {
        return new FXMLLoader(App.class.getResource(fxml));
    }

    public static void main(String[] args) {
        launch();
    }

    public static void showAlert(Alert.AlertType alertType, String title, String message, String headerText, EventHandler<DialogEvent> event) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(headerText);
            alert.setContentText(message);
            alert.setOnHiding(event);
            alert.showAndWait();
        });
    }
}