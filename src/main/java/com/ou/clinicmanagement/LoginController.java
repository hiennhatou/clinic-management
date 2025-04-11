package com.ou.clinicmanagement;

import com.ou.services.UserService;
import com.ou.utils.exceptions.AuthFail;
import com.ou.utils.exceptions.ValidatorException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;

public class LoginController implements Initializable {
    UserService userService = new UserService();

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
        App.moveScene("register.fxml");
    }

    private void onLogin(ActionEvent actionEvent) {
        loader.setVisible(true);
        errorMsg.setText("");
        Thread.ofVirtual().start(() -> {
            try {
                String username = this.username.getText();
                String password = this.password.getText();

                if (username == null || password == null || username.isEmpty() || password.isEmpty())
                    throw new ValidatorException("Vui lòng nhập đầy đủ thông tin", "general");

                userService.authenticate(username, password);
                App.moveScene("welcome.fxml");
            } catch (ValidatorException | AuthFail e) {
                errorMsg.setText(e.getMessage());
            } catch (Exception e) {
                App.showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi hệ thống", null, null);
                Logger.getLogger(App.class.getName()).log(Level.WARNING, e.getMessage(), e);
            } finally {
                loader.setVisible(false);
            }
        });
    }
}