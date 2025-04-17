package com.ou.clinicmanagement.welcome;

import com.ou.clinicmanagement.App;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeAdminController implements Initializable {
    public Button mvToUserManagement;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mvToUserManagement.setOnAction(event -> {
            App.moveScene("user-management.fxml", true);
        });
    }
}
