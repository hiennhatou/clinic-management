package com.ou.clinicmanagement.controllers;

import com.ou.pojos.DBUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.Connection;
import java.time.Duration;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class DangNhapController implements Initializable {
    private Connection connection = DBUtils.getConnection();
    
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
    
    @FXML
    private VBox loader;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginbtn.setOnAction(e -> {
            loader.setVisible(true);
        });
    }
}