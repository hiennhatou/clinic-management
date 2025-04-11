package com.ou.clinicmanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;

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
    private Text errorMsg;

    @FXML
    private VBox loader;

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        App.setTitle("Đăng nhập");
        loginbtn.setOnAction(this::onLogin);
        tosignup.setOnMouseClicked(this::moveToSignup);
    }

    private void moveToSignup(MouseEvent mouseEvent) {
        App.moveScene("dang-ky.fxml");
    }

    private void onLogin(ActionEvent actionEvent) {
        loader.setVisible(true);
    }
}