package com.ou.clinicmanagement.welcome;

import com.ou.clinicmanagement.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeDoctorController implements Initializable {
    @FXML
    public Button ticketBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ticketBtn.setOnAction(event -> App.moveScene("receive-ticket.fxml", true));
    }
}
