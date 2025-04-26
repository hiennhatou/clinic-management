package com.ou.clinicmanagement.welcome;

import com.ou.clinicmanagement.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class WelcomePharmacistController implements Initializable {
    @FXML
    public Button medicineManagement;
    @FXML
    public Button provideMedicineBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        medicineManagement.setOnAction(event -> App.moveScene("medicine-management.fxml", true));
        provideMedicineBtn.setOnAction(event -> App.moveScene("provide-medicine.fxml", true));
    }
}
