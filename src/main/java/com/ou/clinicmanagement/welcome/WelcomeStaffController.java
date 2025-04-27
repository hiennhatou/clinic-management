package com.ou.clinicmanagement.welcome;

import com.ou.clinicmanagement.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeStaffController implements Initializable {
    @FXML
    public Button toPatientManagement;
    @FXML
    public Button toTicketManagement;
    @FXML
    public Button toAppointmentManagement;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toAppointmentManagement.setOnAction(e -> App.moveScene("appointment-management.fxml", true));
        toPatientManagement.setOnAction(event -> App.moveScene("patient-management.fxml", true));
        toTicketManagement.setOnAction(event -> App.moveScene("ticket-management.fxml", true));
    }
}
