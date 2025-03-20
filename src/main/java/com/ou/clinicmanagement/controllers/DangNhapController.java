package com.ou.clinicmanagement.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class DangNhapController implements Initializable {
    @FXML
    private Text tosignup;
    
    @FXML
    private TextField username;
    
    @FXML
    private PasswordField password;
    
    @FXML
    private Button loginbtn;
    
    @FXML
    private Button closebtn;
    
    @FXML
    private Text errorMsg;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
}