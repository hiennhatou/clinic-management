package com.ou.clinicmanagement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    private static Scene scene;
    private static Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        App.stage = stage;
        scene = new Scene(getFXMLLoader("dang-nhap.fxml").load());
        stage.setScene(scene);
        stage.show();
    }

    public static void moveScene(String fxml) throws IOException {
        App.scene.setRoot(getFXMLLoader(fxml).load());
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
}